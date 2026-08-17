package com.starcore.forge.api;

import java.util.List;

/**
 * 预定义材料清单：原版 + 星核
 */
public class BuiltInMaterials {

    public static final MaterialConfig IRON = new MaterialConfig(
            "iron", "铁", "Iron", 0xC0C0C0, 0xD8D8D8,
            "minecraft:iron_ingot"
    );

    public static final MaterialConfig GOLD = new MaterialConfig(
            "gold", "金", "Gold", 0xFFD700, 0xFFEC80,
            "minecraft:gold_ingot"
    );

    public static final MaterialConfig COPPER = new MaterialConfig(
            "copper", "铜", "Copper", 0xFF7F50, 0xFF8C6A,
            "minecraft:copper_ingot"
    );

    public static final MaterialConfig NETHERITE = new MaterialConfig(
            "netherite", "下界合金", "Netherite", 0x4B3E4D, 0x635064,
            "minecraft:netherite_ingot"
    );

    public static final MaterialConfig STARCORE = new MaterialConfig(
            "starcore", "星辰", "StarCore", 0x78B0F0, 0x68D0E8,
            "starcore_forge:starcore_ingot"
    );

    public static final List<MaterialConfig> ALL = List.of(IRON, GOLD, COPPER, NETHERITE, STARCORE);

    private BuiltInMaterials() {}
}
