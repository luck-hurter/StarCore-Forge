package com.starcore.forge;

import com.starcore.forge.block.ModBlocks;
import com.starcore.forge.item.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(StarCoreForge.MOD_ID)
public class StarCoreForge {

    public static final String MOD_ID = "starcore_forge";

    public StarCoreForge(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
