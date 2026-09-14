package com.starcore.forge.block;

import com.starcore.forge.StarCoreForge;
import com.starcore.forge.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(StarCoreForge.MOD_ID);

    public static final DeferredBlock<Block> STARCORE_ORE = registerBlock(
            "starcore_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final DeferredBlock<Block> DEEPSLATE_STARCORE_ORE = registerBlock(
            "deepslate_starcore_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE))
    );

    public static final DeferredBlock<Block> TIN_ORE = registerBlock(
            "tin_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final DeferredBlock<Block> DEEPSLATE_TIN_ORE = registerBlock(
            "deepslate_tin_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE))
    );

    public static final DeferredBlock<Block> SILVER_ORE = registerBlock(
            "silver_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final DeferredBlock<Block> DEEPSLATE_SILVER_ORE = registerBlock(
            "deepslate_silver_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE))
    );

    public static final DeferredBlock<Block> LEAD_ORE = registerBlock(
            "lead_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final DeferredBlock<Block> DEEPSLATE_LEAD_ORE = registerBlock(
            "deepslate_lead_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE))
    );

    public static final DeferredBlock<Block> NICKEL_ORE = registerBlock(
            "nickel_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final DeferredBlock<Block> DEEPSLATE_NICKEL_ORE = registerBlock(
            "deepslate_nickel_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE))
    );

    public static final DeferredBlock<Block> ALUMINUM_ORE = registerBlock(
            "aluminum_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final DeferredBlock<Block> DEEPSLATE_ALUMINUM_ORE = registerBlock(
            "deepslate_aluminum_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE))
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(
            String name, Supplier<T> blockSupplier) {
        DeferredBlock<T> registered = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name,
                () -> new BlockItem(registered.get(), new Item.Properties()));
        return registered;
    }
}
