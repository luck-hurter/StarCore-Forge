package com.starcore.forge;

import com.starcore.forge.api.BatchMaterialRegistrar;
import com.starcore.forge.block.ModBlocks;
import com.starcore.forge.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StarCoreForge.MOD_ID);

    public static final Supplier<CreativeModeTab> STARCORE_TAB = CREATIVE_MODE_TABS.register(
            "starcore_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.starcore_forge"))
                    .icon(() -> new ItemStack(ModItems.STARCORE.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModBlocks.STARCORE_ORE.get());
                        output.accept(ModBlocks.DEEPSLATE_STARCORE_ORE.get());
                        output.accept(ModBlocks.TIN_ORE.get());
                        output.accept(ModBlocks.DEEPSLATE_TIN_ORE.get());
                        output.accept(ModBlocks.SILVER_ORE.get());
                        output.accept(ModBlocks.DEEPSLATE_SILVER_ORE.get());
                        output.accept(ModItems.STARCORE.get());
                        output.accept(ModItems.STARCORE_INGOT.get());
                        output.accept(ModItems.RAW_TIN.get());
                        output.accept(ModItems.TIN_INGOT.get());
                        output.accept(ModItems.RAW_SILVER.get());
                        output.accept(ModItems.SILVER_INGOT.get());
                        output.accept(ModItems.BRONZE_INGOT.get());
                        output.accept(ModItems.STARCORE_HAMMER.get());
                        // 批量注册的材料变体
                        BatchMaterialRegistrar.getAllVariantItems().forEach(output::accept);
                    })
                    .build()
    );
}
