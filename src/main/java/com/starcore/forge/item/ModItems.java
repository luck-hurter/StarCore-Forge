package com.starcore.forge.item;

import com.starcore.forge.StarCoreForge;
import com.starcore.forge.api.BatchMaterialRegistrar;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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

    public static final DeferredItem<Item> STARCORE_INGOT = ITEMS.registerItem(
            "starcore_ingot",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> RAW_TIN = ITEMS.registerItem(
            "raw_tin",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> TIN_INGOT = ITEMS.registerItem(
            "tin_ingot",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> RAW_SILVER = ITEMS.registerItem(
            "raw_silver",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerItem(
            "silver_ingot",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> BRONZE_INGOT = ITEMS.registerItem(
            "bronze_ingot",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> RAW_LEAD = ITEMS.registerItem(
            "raw_lead",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerItem(
            "lead_ingot",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> RAW_NICKEL = ITEMS.registerItem(
            "raw_nickel",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> NICKEL_INGOT = ITEMS.registerItem(
            "nickel_ingot",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> RAW_ALUMINUM = ITEMS.registerItem(
            "raw_aluminum",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> ALUMINUM_INGOT = ITEMS.registerItem(
            "aluminum_ingot",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> RAW_COBALT = ITEMS.registerItem(
            "raw_cobalt",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> COBALT_INGOT = ITEMS.registerItem(
            "cobalt_ingot",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> RAW_ZINC = ITEMS.registerItem(
            "raw_zinc",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> ZINC_INGOT = ITEMS.registerItem(
            "zinc_ingot",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> STARCORE_HAMMER = ITEMS.registerItem(
            "starcore_hammer",
            properties -> new StarCoreHammerItem(properties.stacksTo(1).durability(300).attributes(
                    ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 7.0, AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED,
                                    new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.0, AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build()
            )),
            new Item.Properties()
    );

    // 批量注册所有内置材料变体（板/杆/齿轮/粉）
    static {
        BatchMaterialRegistrar.registerAllBuiltIn();
    }
}
