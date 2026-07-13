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

    private static <T extends Block> DeferredBlock<T> registerBlock(
            String name, Supplier<T> blockSupplier) {
        DeferredBlock<T> registered = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name,
                () -> new BlockItem(registered.get(), new Item.Properties()));
        return registered;
    }
}
