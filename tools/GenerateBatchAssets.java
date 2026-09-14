import com.starcore.forge.api.MaterialConfig;
import com.starcore.forge.api.MaterialVariantType;
import com.starcore.forge.api.TextureGenUtil;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 开发期预生成材料批量变体资产（贴图/模型/配方），不依赖游戏运行。
 * 输出与运行时 BatchMaterialRegistrar.registerAllBuiltIn() 完全一致：
 * - 贴图：复用 TextureGenUtil，种子公式相同（SEED_BASE + name.hashCode() + ordinal*31）
 * - 模型/配方 JSON：复刻 BatchMaterialRegistrar 的字符串格式（含 粉→锭 熔炉配方）
 * 新增材料预生成时，把 materials 数组里的 MaterialConfig 换掉即可。
 *
 * 重要：TextureGenUtil 的回写路径与运行时一致（../../ 相对路径，详见 BatchMaterialRegistrar 说明），
 * 因此本工具必须以 run/client 为工作目录运行：
 *   cd run/client && java -cp ../../build/pregen GenerateBatchAssets
 */
public class GenerateBatchAssets {

    static final String MOD_ID = "starcore_forge";
    static final String HAMMER_ITEM = "starcore_forge:starcore_hammer";
    static final long SEED_BASE = 20240713L;

    public static void main(String[] args) throws Exception {
        MaterialConfig[] materials = {
                new MaterialConfig("tin", "锡", "Tin", 0xCDD3D4, 0xE2E7E8, "starcore_forge:tin_ingot"),
                new MaterialConfig("silver", "银", "Silver", 0xC9E4F3, 0xDDF0FA, "starcore_forge:silver_ingot"),
                new MaterialConfig("bronze", "青铜", "Bronze", 0xE7800D, 0xF2A24E, "starcore_forge:bronze_ingot"),
                new MaterialConfig("lead", "铅", "Lead", 0x7D4FA4, 0x976FC1, "starcore_forge:lead_ingot"),
                new MaterialConfig("nickel", "镍", "Nickel", 0xFFC552, 0xFFDA8A, "starcore_forge:nickel_ingot"),
                new MaterialConfig("aluminum", "铝", "Aluminum", 0x86E0FF, 0xAEECFF, "starcore_forge:aluminum_ingot"),
                new MaterialConfig("cobalt", "钴", "Cobalt", 0x7270FF, 0x9B99FF, "starcore_forge:cobalt_ingot"),
                new MaterialConfig("zinc", "锌", "Zinc", 0xE4FFCA, 0xEFFFE0, "starcore_forge:zinc_ingot")
        };

        for (MaterialConfig m : materials) {
            for (MaterialVariantType type : MaterialVariantType.values()) {
                TextureGenUtil.generate(m, type, SEED_BASE + m.name().hashCode() + type.ordinal() * 31L);
                writeModel(m.getRegistryName(type));
            }
            writeRecipe(m.name() + "_plate_from_hammer", plateJson(m));
            writeRecipe(m.name() + "_rod_from_hammer", rodJson(m));
            writeRecipe(m.name() + "_gear_from_plate_and_rod", gearJson(m));
            writeRecipe(m.name() + "_dust_from_hammer", dustJson(m));
            writeRecipe(m.name() + "_ingot_from_smelting_" + m.name() + "_dust", dustSmeltingJson(m));
        }
        System.out.println("Pre-generation done.");
    }

    // ========== 模型 ==========

    static void writeModel(String name) throws Exception {
        String json = "{\n" +
                "  \"parent\": \"minecraft:item/generated\",\n" +
                "  \"textures\": {\n" +
                "    \"layer0\": \"" + MOD_ID + ":item/" + name + "\"\n" +
                "  }\n" +
                "}\n";
        write("../../src/main/resources/assets/starcore_forge/models/item/" + name + ".json", json);
    }

    // ========== 配方（格式与 BatchMaterialRegistrar 一致） ==========

    static String plateJson(MaterialConfig m) {
        return "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"H\",\n" +
                "    \"I\",\n" +
                "    \"I\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"H\": { \"item\": \"" + HAMMER_ITEM + "\" },\n" +
                "    \"I\": { \"item\": \"" + m.ingotItem() + "\" }\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"" + MOD_ID + ":" + m.getRegistryName(MaterialVariantType.PLATE) + "\",\n" +
                "    \"count\": 1\n" +
                "  }\n" +
                "}\n";
    }

    static String rodJson(MaterialConfig m) {
        return "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"H  \",\n" +
                "    \" I \",\n" +
                "    \"  I\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"H\": { \"item\": \"" + HAMMER_ITEM + "\" },\n" +
                "    \"I\": { \"item\": \"" + m.ingotItem() + "\" }\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"" + MOD_ID + ":" + m.getRegistryName(MaterialVariantType.ROD) + "\",\n" +
                "    \"count\": 2\n" +
                "  }\n" +
                "}\n";
    }

    static String gearJson(MaterialConfig m) {
        String plateId = MOD_ID + ":" + m.getRegistryName(MaterialVariantType.PLATE);
        String rodId = MOD_ID + ":" + m.getRegistryName(MaterialVariantType.ROD);
        return "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \" P \",\n" +
                "    \"RIR\",\n" +
                "    \" P \"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"P\": { \"item\": \"" + plateId + "\" },\n" +
                "    \"R\": { \"item\": \"" + rodId + "\" },\n" +
                "    \"I\": { \"item\": \"" + m.ingotItem() + "\" }\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"" + MOD_ID + ":" + m.getRegistryName(MaterialVariantType.GEAR) + "\",\n" +
                "    \"count\": 1\n" +
                "  }\n" +
                "}\n";
    }

    static String dustJson(MaterialConfig m) {
        return "{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [\n" +
                "    \"H\",\n" +
                "    \"I\"\n" +
                "  ],\n" +
                "  \"key\": {\n" +
                "    \"H\": { \"item\": \"" + HAMMER_ITEM + "\" },\n" +
                "    \"I\": { \"item\": \"" + m.ingotItem() + "\" }\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"" + MOD_ID + ":" + m.getRegistryName(MaterialVariantType.DUST) + "\",\n" +
                "    \"count\": 1\n" +
                "  }\n" +
                "}\n";
    }

    static String dustSmeltingJson(MaterialConfig m) {
        return "{\n" +
                "  \"type\": \"minecraft:smelting\",\n" +
                "  \"ingredient\": {\n" +
                "    \"item\": \"" + MOD_ID + ":" + m.getRegistryName(MaterialVariantType.DUST) + "\"\n" +
                "  },\n" +
                "  \"result\": {\n" +
                "    \"id\": \"" + m.ingotItem() + "\"\n" +
                "  },\n" +
                "  \"experience\": 0.7,\n" +
                "  \"cookingtime\": 200\n" +
                "}\n";
    }

    static void writeRecipe(String recipeName, String json) throws Exception {
        write("../../src/main/resources/data/starcore_forge/recipe/" + recipeName + ".json", json);
    }

    static void write(String path, String content) throws Exception {
        Path p = Path.of(path);
        Files.createDirectories(p.getParent());
        Files.writeString(p, content);
        System.out.println("Generated: " + p);
    }
}
