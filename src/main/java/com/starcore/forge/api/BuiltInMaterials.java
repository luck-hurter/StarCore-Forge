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

    public static final MaterialConfig COBALT = new MaterialConfig(
            "cobalt", "钴", "Cobalt", 0x7270FF, 0x9B99FF,
            "starcore_forge:cobalt_ingot"
    );

    public static final MaterialConfig ZINC = new MaterialConfig(
            "zinc", "锌", "Zinc", 0xE4FFCA, 0xEFFFE0,
            "starcore_forge:zinc_ingot"
    );

    /** 以下为无矿石材料，锭为基础材料 */
    public static final MaterialConfig TITANIUM = new MaterialConfig(
            "titanium", "钛", "Titanium", 0xDAFFF7, 0xEBFFFA,
            "starcore_forge:titanium_ingot"
    );

    public static final MaterialConfig VANADIUM = new MaterialConfig(
            "vanadium", "钒", "Vanadium", 0xA0ADAA, 0xBCC9C6,
            "starcore_forge:vanadium_ingot"
    );

    public static final MaterialConfig CHROMIUM = new MaterialConfig(
            "chromium", "铬", "Chromium", 0xF0E0BF, 0xFAF0DA,
            "starcore_forge:chromium_ingot"
    );

    public static final MaterialConfig GALLIUM = new MaterialConfig(
            "gallium", "镓", "Gallium", 0xE3FEFF, 0xF0FFFF,
            "starcore_forge:gallium_ingot"
    );

    public static final MaterialConfig ANTIMONY = new MaterialConfig(
            "antimony", "锑", "Antimony", 0xDBEEFF, 0xE8F5FF,
            "starcore_forge:antimony_ingot"
    );

    public static final MaterialConfig TANTALUM = new MaterialConfig(
            "tantalum", "钽", "Tantalum", 0x1F92FF, 0x63B4FF,
            "starcore_forge:tantalum_ingot"
    );

    public static final MaterialConfig MANGANESE = new MaterialConfig(
            "manganese", "锰", "Manganese", 0xAA1FFF, 0xC25FFF,
            "starcore_forge:manganese_ingot"
    );

    public static final MaterialConfig SCANDIUM = new MaterialConfig(
            "scandium", "钪", "Scandium", 0xFFE7B9, 0xFFF2D9,
            "starcore_forge:scandium_ingot"
    );

    public static final MaterialConfig SILICON = new MaterialConfig(
            "silicon", "硅", "Silicon", 0x858599, 0xA5A5B8,
            "starcore_forge:silicon_ingot"
    );

    public static final List<MaterialConfig> ALL = List.of(
            IRON, GOLD, COPPER, NETHERITE, STARCORE, TIN, SILVER, BRONZE,
            LEAD, NICKEL, ALUMINUM, COBALT, ZINC, TITANIUM, VANADIUM, CHROMIUM, GALLIUM,
            ANTIMONY, TANTALUM, MANGANESE, SCANDIUM, SILICON);

    private BuiltInMaterials() {}
}
