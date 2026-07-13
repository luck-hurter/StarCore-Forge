package com.starcore.forge.item;

import com.starcore.forge.StarCoreForge;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(StarCoreForge.MOD_ID);

    public static final DeferredItem<Item> STARCORE = ITEMS.registerItem(
            "starcore",
            Item::new,
            new Item.Properties()
    );
}
