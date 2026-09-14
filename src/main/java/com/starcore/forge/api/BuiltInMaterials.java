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

    public static final MaterialConfig TIN = new MaterialConfig(
            "tin", "锡", "Tin", 0xCDD3D4, 0xE2E7E8,
            "starcore_forge:tin_ingot"
    );

    public static final MaterialConfig SILVER = new MaterialConfig(
            "silver", "银", "Silver", 0xC9E4F3, 0xDDF0FA,
            "starcore_forge:silver_ingot"
    );

    /** 青铜：无矿石，锭为基础材料 */
    public static final MaterialConfig BRONZE = new MaterialConfig(
            "bronze", "青铜", "Bronze", 0xE7800D, 0xF2A24E,
            "starcore_forge:bronze_ingot"
    );

    public static final MaterialConfig LEAD = new MaterialConfig(
            "lead", "铅", "Lead", 0x7D4FA4, 0x976FC1,
            "starcore_forge:lead_ingot"
    );

    public static final MaterialConfig NICKEL = new MaterialConfig(
            "nickel", "镍", "Nickel", 0xFFC552, 0xFFDA8A,
            "starcore_forge:nickel_ingot"
    );

    public static final MaterialConfig ALUMINUM = new MaterialConfig(
            "aluminum", "铝", "Aluminum", 0x86E0FF, 0xAEECFF,
            "starcore_forge:aluminum_ingot"
    );

    public static final List<MaterialConfig> ALL =
            List.of(IRON, GOLD, COPPER, NETHERITE, STARCORE, TIN, SILVER, BRONZE, LEAD, NICKEL, ALUMINUM);

    private BuiltInMaterials() {}
}
