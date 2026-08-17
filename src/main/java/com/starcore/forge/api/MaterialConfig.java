package com.starcore.forge.api;

/**
 * 材料配置：名称、翻译、颜色、锭物品
 */
public record MaterialConfig(
        String name,           // 注册名，如 "iron", "starcore"
        String zhName,         // 中文名，如 "铁", "星核"
        String enName,         // 英文名，如 "Iron", "StarCore"
        int primaryColor,      // 主色 0xRRGGBB
        int secondaryColor,    // 副色 0xRRGGBB
        String ingotItem       // 对应"锭"物品ID，如 "minecraft:iron_ingot"；无锭材料为 null（如钻石）
) {

    public String getRegistryName(MaterialVariantType type) {
        return name + "_" + type.suffix;
    }

    public String getZhTranslation(MaterialVariantType type) {
        return zhName + type.zhName;
    }

    public String getEnTranslation(MaterialVariantType type) {
        return enName + " " + type.enName;
    }

    public int getRed() { return (primaryColor >> 16) & 0xFF; }
    public int getGreen() { return (primaryColor >> 8) & 0xFF; }
    public int getBlue() { return primaryColor & 0xFF; }
    public int getSecRed() { return (secondaryColor >> 16) & 0xFF; }
    public int getSecGreen() { return (secondaryColor >> 8) & 0xFF; }
    public int getSecBlue() { return secondaryColor & 0xFF; }
}
