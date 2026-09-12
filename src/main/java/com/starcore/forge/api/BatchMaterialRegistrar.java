package com.starcore.forge.api;

import com.starcore.forge.StarCoreForge;
import com.starcore.forge.item.ModItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 批量材料变体注册 API
 *
 * 内部调用：
 *   BatchMaterialRegistrar.registerAllBuiltIn();
 *
 * 外部模组调用：
 *   BatchMaterialRegistrar.registerMaterial(myConfig, MaterialVariantType.PLATE, MaterialVariantType.ROD);
 */
public class BatchMaterialRegistrar {

    private static final List<MaterialResult> ALL_RESULTS = new ArrayList<>();
    private static final Map<String, String> ZH_PENDING = new LinkedHashMap<>();
    private static final Map<String, String> EN_PENDING = new LinkedHashMap<>();
    private static final String SRC_RESOURCES = "src/main/resources/assets/starcore_forge/";
    private static final String BUILD_RESOURCES = "build/resources/main/assets/starcore_forge/";
    private static final String SRC_RECIPES = "src/main/resources/data/starcore_forge/recipe/";
    private static final String BUILD_RECIPES = "build/resources/main/data/starcore_forge/recipe/";
    private static final String HAMMER_ITEM = "starcore_forge:starcore_hammer";
    private static final long SEED_BASE = 20240713L;

    // ========== 对外 API ==========

    /**
     * 为单个材料批量注册变体物品
     */
    public static MaterialResult registerMaterial(MaterialConfig material, MaterialVariantType... types) {
        Map<MaterialVariantType, DeferredItem<Item>> items = new LinkedHashMap<>();
        for (MaterialVariantType type : types) {
            String name = material.getRegistryName(type);
            DeferredItem<Item> item = ModItems.ITEMS.registerItem(name, Item::new, new Item.Properties());
            items.put(type, item);

            // 生成纹理
            TextureGenUtil.generate(material, type, SEED_BASE + material.name().hashCode() + type.ordinal() * 31L);

            // 生成模型 JSON
            generateModelJson(name);

            // 收集翻译
            String key = "item." + StarCoreForge.MOD_ID + "." + name;
            ZH_PENDING.put(key, material.getZhTranslation(type));
            EN_PENDING.put(key, material.getEnTranslation(type));
        }

        MaterialResult result = new MaterialResult(material.name(), items);
        ALL_RESULTS.add(result);
        return result;
    }

    /**
     * 批量注册所有内置材料（原版 + 星核）的全部变体，并生成对应的"锤锻"配方
     */
    public static void registerAllBuiltIn() {
        for (MaterialConfig material : BuiltInMaterials.ALL) {
            registerMaterial(material, MaterialVariantType.values());
            generatePlateRecipe(material);
            generateRodRecipe(material);
            generateGearRecipe(material);
        }
        writeGenLangFiles();
    }

    /**
     * 为单个材料生成"锤锻板"有序合成配方：
     * 竖向3格 = 星辰锻造锤在上，两个锭依次在锤子正下方 → 对应板（如 铁锭×2 + 锤 → 铁板）
     * 有序模式可整体平移到合成栏任意一列，但锭必须严格在锤子正下方
     * 无锭的材料（如钻石，ingotItem 为 null）自动跳过，不生成配方
     */
    public static void generatePlateRecipe(MaterialConfig material) {
        String ingotItem = material.ingotItem();
        if (ingotItem == null) return; // 非锭材料不生成配方

        String plateName = material.getRegistryName(MaterialVariantType.PLATE);
        String recipeName = material.name() + "_plate_from_hammer";
        String json = "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"H\",\n" +
                "    \"I\",\n" +
                "    \"I\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"H\": { \"item\": \"" + HAMMER_ITEM + "\" },\n" +
                "    \"I\": { \"item\": \"" + ingotItem + "\" }\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"" + StarCoreForge.MOD_ID + ":" + plateName + "\",\n" +
                "    \"count\": 1\n" +
                "  }\n" +
                "}\n";
        writeRecipeJson(recipeName + ".json", json);
    }

    /**
     * 为单个材料生成"锤锻杆"有序合成配方：
     * 左上角为星辰锻造锤，正中与右下角为锭（对角线）→ 对应杆×2（如 铁锭×2 + 锤 → 铁杆×2）
     * 模式中的空格表示空位（原版规则，不能用其他字符），占满 3×3、位置完全固定，必须在工作台中合成
     * 注意：须与板配方（竖列 H/I/I）保持不同形状，否则同为"锤+锭×2"的两种配方会冲突，导致板无法合成
     * 无锭的材料（如钻石，ingotItem 为 null）自动跳过，不生成配方
     */
    public static void generateRodRecipe(MaterialConfig material) {
        String ingotItem = material.ingotItem();
        if (ingotItem == null) return; // 非锭材料不生成配方

        String rodName = material.getRegistryName(MaterialVariantType.ROD);
        String recipeName = material.name() + "_rod_from_hammer";
        String json = "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"H  \",\n" +
                "    \" I \",\n" +
                "    \"  I\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"H\": { \"item\": \"" + HAMMER_ITEM + "\" },\n" +
                "    \"I\": { \"item\": \"" + ingotItem + "\" }\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"" + StarCoreForge.MOD_ID + ":" + rodName + "\",\n" +
                "    \"count\": 2\n" +
                "  }\n" +
                "}\n";
        writeRecipeJson(recipeName + ".json", json);
    }

    /**
     * 为单个材料生成"齿轮"有序合成配方（不消耗锤）：
     * 十字形 = 上下为板、左右为杆、正中为锭 → 对应齿轮×1（如 铁板×2 + 铁杆×2 + 铁锭 → 铁齿轮）
     * 所需板/杆是本模组批量注册的同材料中间产物
     * 无锭的材料（如钻石，ingotItem 为 null）自动跳过，不生成配方
     */
    public static void generateGearRecipe(MaterialConfig material) {
        String ingotItem = material.ingotItem();
        if (ingotItem == null) return; // 非锭材料不生成配方

        String plateId = StarCoreForge.MOD_ID + ":" + material.getRegistryName(MaterialVariantType.PLATE);
        String rodId = StarCoreForge.MOD_ID + ":" + material.getRegistryName(MaterialVariantType.ROD);
        String gearName = material.getRegistryName(MaterialVariantType.GEAR);
        String recipeName = material.name() + "_gear_from_plate_and_rod";
        String json = "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \" P \",\n" +
                "    \"RIR\",\n" +
                "    \" P \"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"P\": { \"item\": \"" + plateId + "\" },\n" +
                "    \"R\": { \"item\": \"" + rodId + "\" },\n" +
                "    \"I\": { \"item\": \"" + ingotItem + "\" }\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"" + StarCoreForge.MOD_ID + ":" + gearName + "\",\n" +
                "    \"count\": 1\n" +
                "  }\n" +
                "}\n";
        writeRecipeJson(recipeName + ".json", json);
    }

    /**
     * 将配方 JSON 写入源目录与构建输出目录（下次启动生效）
     */
    private static void writeRecipeJson(String filename, String json) {
        String[] dirs = {
            SRC_RECIPES,
            BUILD_RECIPES,
            "../../src/main/resources/data/starcore_forge/recipe/",
            "../../build/resources/main/data/starcore_forge/recipe/"
        };
        boolean saved = false;
        for (String dirPath : dirs) {
            try {
                Path dir = Path.of(dirPath);
                Files.createDirectories(dir);
                Files.writeString(dir.resolve(filename), json);
                saved = true;
            } catch (IOException ignored) {}
        }
        if (saved) {
            System.out.println("[StarCore] Generated recipe: " + filename);
        } else {
            System.err.println("[StarCore] Failed to generate recipe: " + filename);
        }
    }

    /**
     * 获取所有已注册的变体物品（供创造模式标签页使用）
     */
    public static List<Item> getAllVariantItems() {
        List<Item> all = new ArrayList<>();
        for (MaterialResult result : ALL_RESULTS) {
            for (DeferredItem<Item> item : result.items().values()) {
                all.add(item.get());
            }
        }
        return all;
    }

    // ========== 内部方法 ==========

    private static void generateModelJson(String name) {
        String json = "{\n" +
                "  \"parent\": \"minecraft:item/generated\",\n" +
                "  \"textures\": {\n" +
                "    \"layer0\": \"starcore_forge:item/" + name + "\"\n" +
                "  }\n" +
                "}\n";
        try {
            // 写入源目录（下次构建生效）
            Path srcPath = Path.of(SRC_RESOURCES + "models/item/" + name + ".json");
            Files.createDirectories(srcPath.getParent());
            Files.writeString(srcPath, json);
            // 也写入构建输出目录（当前运行生效）
            Path buildPath = Path.of(BUILD_RESOURCES + "models/item/" + name + ".json");
            Files.createDirectories(buildPath.getParent());
            Files.writeString(buildPath, json);
        } catch (IOException e) {
            System.err.println("[StarCore] Failed to generate model for " + name + ": " + e.getMessage());
        }
    }

    /**
     * 将翻译写入独立文件（不触碰手写的 lang 文件），Minecraft 自动合并加载
     */
    private static void writeGenLangFiles() {
        mergeIntoLangFile("zh_cn.json", ZH_PENDING);
        mergeIntoLangFile("en_us.json", EN_PENDING);
    }

    private static void mergeIntoLangFile(String filename, Map<String, String> newEntries) {
        if (newEntries.isEmpty()) return;

        // 读取原文件内容
        StringBuilder sb = new StringBuilder();
        String base = "assets/starcore_forge/lang/";
        String[] readPaths = {
            SRC_RESOURCES + "lang/" + filename,
            BUILD_RESOURCES + "lang/" + filename,
            "../../src/main/resources/" + base + filename,
            "../../build/resources/main/" + base + filename,
        };
        String existing = null;
        for (String p : readPaths) {
            try {
                existing = Files.readString(Path.of(p));
                break;
            } catch (IOException ignored) {}
        }
        if (existing == null) existing = "{}";

        // 去掉末尾的 } 和换行
        existing = existing.trim();
        String inner = existing.substring(1, existing.length() - 1).trim();

        // 构建新内容
        sb.append("{\n");
        // 写入原有条目（跳过已有同 key 的）
        if (!inner.isEmpty()) {
            // simple line-by-line merge: skip lines that match our new keys
            String[] lines = inner.split("\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                // 去掉末尾逗号
                String clean = trimmed.endsWith(",") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
                // 检查是否是我们要替换的 key
                boolean isAutoKey = false;
                for (String key : newEntries.keySet()) {
                    if (clean.startsWith("\"" + key + "\"")) { isAutoKey = true; break; }
                }
                if (!isAutoKey) {
                    sb.append("  ").append(clean).append(",\n");
                }
            }
        }
        // 写入新的自动条目
        int i = 0;
        for (Map.Entry<String, String> e : newEntries.entrySet()) {
            sb.append("  \"").append(e.getKey()).append("\": \"").append(e.getValue()).append("\"");
            if (i < newEntries.size() - 1) sb.append(",");
            sb.append("\n");
            i++;
        }
        sb.append("}\n");
        String content = sb.toString();

        // 写入
        String[] writePaths = {
            SRC_RESOURCES + "lang/" + filename,
            BUILD_RESOURCES + "lang/" + filename,
            "../../src/main/resources/" + base + filename,
            "../../build/resources/main/" + base + filename,
        };
        boolean saved = false;
        for (String p : writePaths) {
            try {
                Files.createDirectories(Path.of(p).getParent());
                Files.writeString(Path.of(p), content);
                saved = true;
            } catch (IOException ignored) {}
        }
        if (saved) {
            System.out.println("[StarCore] Merged translations into: " + filename + " (" + newEntries.size() + " entries)");
        }
    }

    // ========== 数据类 ==========

    public record MaterialResult(
            String name,
            Map<MaterialVariantType, DeferredItem<Item>> items
    ) {}
}
