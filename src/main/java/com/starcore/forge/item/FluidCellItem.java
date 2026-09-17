package com.starcore.forge.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 流体单元：装载流体的基础容器，交互逻辑 1:1 复刻原版 BucketItem：
 *
 * 空单元（fluid 为 null）＝空桶：
 *   use() 用 ClipContext.Fluid.SOURCE_ONLY 潜望——命中格即流体源格，调用
 *   BucketPickup.pickupBlock 排液（返回的满桶物品不使用，仅借其判定成败）；
 *   命中的是流动流体时（SOURCE_ONLY 不可见）回退尝试命中格面前的流体格。
 * 满单元＝满桶：
 *   use() 用 ClipContext.Fluid.NONE，向命中格面前一格倒流体；
 *   支持 LiquidBlockContainer（炼药锅/可含水方块）直灌；水在炎热维度蒸发。
 *
 * 与桶的差异仅两处：
 *   1. 吸取后的产物是本模组的对应单元而非铁桶（由 FILLED_CELLS 映射）；
 *   2. 桶无法舀取流动流体，这里补充支持（直接移除该格 + 原版灌装音效）。
 * 另外重写 useOn()：右键方块时确定性地走同一套逻辑，不依赖客户端
 * "方块交互未消费才补发 use 包"的回退链。
 * 新流体（氢/氧等）加入时在 ModItems 用同一构造器注册对应单元即可。
 */
public class FluidCellItem extends Item {

    /** 支持装载的流体 → 对应满单元 */
    private static final Map<Fluid, Supplier<Item>> FILLED_CELLS = Map.of(
            Fluids.WATER, () -> ModItems.WATER_CELL.get(),
            Fluids.LAVA, () -> ModItems.LAVA_CELL.get()
    );

    /** 单元装载的流体；null 表示空单元 */
    private final Supplier<? extends Fluid> fluid;

    public FluidCellItem(Supplier<? extends Fluid> fluid, Properties properties) {
        super(properties);
        this.fluid = fluid;
    }

    /** 空单元工厂：fluid 为 null 表示未装载任何流体 */
    public static FluidCellItem emptyCell(Properties properties) {
        return new FluidCellItem(null, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(
                level, player, fluid == null ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        if (hit.getType() == HitResult.Type.MISS || hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }
        BlockPos hitPos = hit.getBlockPos();
        Direction direction = hit.getDirection();
        BlockPos frontPos = hitPos.relative(direction);
        if (!level.mayInteract(player, hitPos) || !player.mayUseItemAt(frontPos, direction, stack)) {
            return InteractionResultHolder.fail(stack);
        }
        return fluid == null
                ? pickUp(level, player, stack, hitPos, frontPos)
                : pourOut(level, player, stack, frontPos, direction);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        BlockPos hitPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos frontPos = hitPos.relative(direction);
        if (!level.mayInteract(player, hitPos) || !player.mayUseItemAt(frontPos, direction, stack)) {
            return InteractionResult.FAIL;
        }
        InteractionResultHolder<ItemStack> result = fluid == null
                ? pickUp(level, player, stack, hitPos, frontPos)
                : pourOut(level, player, stack, frontPos, direction);
        return result.getResult();
    }

    /**
     * 吸取：先试命中格本身（SOURCE_ONLY 直接命中的源格，原版桶主路径），
     * 再试命中格面前的流体格（流动流体，桶不支持，这里补充）；
     * 两处都不是可装载流体时返回 pass（不消费，让方块交互/use 兜底继续）
     */
    private InteractionResultHolder<ItemStack> pickUp(
            Level level, Player player, ItemStack stack, BlockPos hitPos, BlockPos frontPos) {
        InteractionResultHolder<ItemStack> result = tryPickupCell(level, player, stack, hitPos);
        if (result == null) {
            result = tryPickupCell(level, player, stack, frontPos);
        }
        return result != null ? result : InteractionResultHolder.pass(stack);
    }

    /**
     * 尝试吸取单个格；该格不是可装载流体时返回 null（由调用方继续尝试/放行）
     */
    private InteractionResultHolder<ItemStack> tryPickupCell(Level level, Player player, ItemStack stack, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BucketPickup pickup)) {
            return null;
        }
        Fluid found = normalize(state.getFluidState().getType());
        Supplier<Item> filled = FILLED_CELLS.get(found);
        if (filled == null) {
            return null;
        }
        if (!level.isClientSide()) {
            ItemStack drained = pickup.pickupBlock(player, level, pos, state);
            if (drained.isEmpty()) {
                if (level.getFluidState(pos).isEmpty()) {
                    return null; // 未能排掉任何流体，按桶语义判失败
                }
                // 流动流体原版不处理，直接移除该格并补原版灌装音效
                level.removeBlock(pos, false);
                level.playSound(null, pos, fillSound(found), SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                // 原版桶路径：BucketPickup 自带灌装音效（水/岩浆各自的桶声）+ 游戏事件
                pickup.getPickupSound(state).ifPresent(sound -> player.playSound(sound, 1.0F, 1.0F));
                level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(
                ItemUtils.createFilledResult(stack, player, new ItemStack(filled.get())), level.isClientSide());
    }

    /**
     * 倒出：优先灌入可含液体的方块（炼药锅等），否则放到面前一格
     */
    private InteractionResultHolder<ItemStack> pourOut(
            Level level, Player player, ItemStack stack, BlockPos pos, Direction direction) {
        BlockState state = level.getBlockState(pos);
        boolean canContain = state.isAir()
                || state.canBeReplaced()
                || (state.getBlock() instanceof LiquidBlockContainer container
                    && container.canPlaceLiquid(player, level, pos, state, fluid.get()));
        if (!canContain) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide()) {
            if (state.getBlock() instanceof LiquidBlockContainer container
                    && container.canPlaceLiquid(player, level, pos, state, fluid.get())) {
                container.placeLiquid(level, pos, state, ((FlowingFluid) fluid.get()).getSource(false));
            } else if (level.dimensionType().ultraWarm() && fluid.get() == Fluids.WATER) {
                // 原版规则：水在炎热维度（下界）蒸发，只播放熄灭音效
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
            } else {
                level.setBlock(pos, fluid.get().defaultFluidState().createLegacyBlock(), 11);
                level.playSound(null, pos, pourSound(fluid.get()), SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(
                ItemUtils.createFilledResult(stack, player, new ItemStack(ModItems.CELL.get())), level.isClientSide());
    }

    /** 流动流体归一化为对应源流体，便于查表 */
    private static Fluid normalize(Fluid fluid) {
        if (fluid == Fluids.FLOWING_WATER) return Fluids.WATER;
        if (fluid == Fluids.FLOWING_LAVA) return Fluids.LAVA;
        return fluid;
    }

    private static SoundEvent fillSound(Fluid fluid) {
        return fluid == Fluids.LAVA ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
    }

    private static SoundEvent pourSound(Fluid fluid) {
        return fluid == Fluids.LAVA ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
    }
}
