import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 一次性生成锡/银矿石贴图工具（开发期使用，不参与构建）。
 *
 * 原理：单个像素的亮度参考原版贴图（铁锭/粗铁/铁矿石/深层铁矿石），只把色调替换为目标金属色——
 * - 锭/粗矿：以原版铁锭/粗铁为模板，整图按"像素亮度/平均亮度"比例缩放目标色
 * - 矿石方块：以原版铁矿石/深层铁矿石为模板，仅重新着色"矿斑"像素，
 *   矿斑按暖色色相识别（铁矿石矿斑为棕褐色 R 明显大于 B，石头/深板岩背景为中性色），
 *   其余像素原样保留——因此石头外围部分与原版铁矿石完全一致
 *
 * 用法: java tools/GenerateOreTextures.java <原版贴图目录> <项目根目录>
 * 原版贴图目录形如 build/vanilla_tex/assets/minecraft/textures
 */
public class GenerateOreTextures {

    /** 基准色 = 生成贴图的平均色。锡：灰白偏白；银：淡蓝银白；青铜：橙铜色。hasOre=false 的材料（如青铜）只生成锭贴图 */
    record Material(String name, int base, boolean hasOre) {}

    /** 矿斑暖色色相阈值：R-B 超过该值视为矿斑（棕褐色矿斑 vs 中性石头/深板岩背景） */
    static final int WARM_THRESHOLD = 25;

    public static void main(String[] args) throws Exception {
        Path vanilla = Path.of(args[0]);
        Path repo = Path.of(args[1]);

        BufferedImage ingotSrc = ImageIO.read(vanilla.resolve("item/iron_ingot.png").toFile());
        BufferedImage rawSrc = ImageIO.read(vanilla.resolve("item/raw_iron.png").toFile());
        BufferedImage oreSrc = ImageIO.read(vanilla.resolve("block/iron_ore.png").toFile());
        BufferedImage deepslateOreSrc = ImageIO.read(vanilla.resolve("block/deepslate_iron_ore.png").toFile());

        Material[] materials = {
                new Material("tin", 0xC6CBCC, true),
                new Material("silver", 0xC9E4F3, true),
                new Material("bronze", 0xE7800D, false),
                new Material("lead", 0x7D4FA4, true),
                new Material("nickel", 0xFFC552, true),
                new Material("aluminum", 0x86E0FF, true),
                new Material("cobalt", 0x7270FF, true),
                new Material("zinc", 0xE4FFCA, true),
                new Material("titanium", 0xDAFFF7, false),
                new Material("vanadium", 0xA0ADAA, false),
                new Material("chromium", 0xF0E0BF, false),
                new Material("gallium", 0xE3FEFF, false)
        };

        for (Material m : materials) {
            write(repo, "item/" + m.name() + "_ingot", recolorWhole(ingotSrc, m.base()));
            if (!m.hasOre()) continue;
            write(repo, "item/raw_" + m.name(), recolorWhole(rawSrc, m.base()));
            write(repo, "block/" + m.name() + "_ore", recolorOreBlobs(oreSrc, m.base()));
            write(repo, "block/deepslate_" + m.name() + "_ore", recolorOreBlobs(deepslateOreSrc, m.base()));
        }
        System.out.println("All textures generated.");
    }

    /** 整图重着色：out = 目标色 × (该像素亮度 / 源图平均亮度) */
    static BufferedImage recolorWhole(BufferedImage src, int target) {
        int w = src.getWidth(), h = src.getHeight();
        double avg = averageLuminance(src);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = src.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                if (a == 0) continue;
                out.setRGB(x, y, scaleColor(target, luminance(argb) / avg, a));
            }
        }
        return out;
    }

    /** 矿石方块重着色：只替换暖色矿斑像素，比例基于矿斑平均亮度，背景像素原样保留 */
    static BufferedImage recolorOreBlobs(BufferedImage ore, int target) {
        int w = ore.getWidth(), h = ore.getHeight();
        boolean[][] blob = new boolean[h][w];
        double sum = 0;
        int n = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = ore.getRGB(x, y);
                if (isWarmBlob(argb)) {
                    blob[y][x] = true;
                    sum += luminance(argb);
                    n++;
                }
            }
        }
        if (n == 0) throw new IllegalStateException("No blob pixels detected (warm hue R-B > " + WARM_THRESHOLD + ")");
        double avg = sum / n;

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = ore.getRGB(x, y);
                if (blob[y][x]) {
                    out.setRGB(x, y, scaleColor(target, luminance(argb) / avg, (argb >>> 24) & 0xFF));
                } else {
                    out.setRGB(x, y, argb);
                }
            }
        }
        return out;
    }

    /** 矿斑判定：暖色色相（R 明显大于 B），石头/深板岩背景为中性色不满足 */
    static boolean isWarmBlob(int argb) {
        int r = (argb >> 16) & 0xFF, b = argb & 0xFF;
        return r - b > WARM_THRESHOLD;
    }

    static double averageLuminance(BufferedImage img) {
        double sum = 0;
        int n = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int argb = img.getRGB(x, y);
                if (((argb >>> 24) & 0xFF) == 0) continue;
                sum += luminance(argb);
                n++;
            }
        }
        return sum / n;
    }

    static double luminance(int argb) {
        int r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF;
        return (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0;
    }

    static int scaleColor(int target, double ratio, int alpha) {
        int r = clamp8((int) Math.round(((target >> 16) & 0xFF) * ratio));
        int g = clamp8((int) Math.round(((target >> 8) & 0xFF) * ratio));
        int b = clamp8((int) Math.round((target & 0xFF) * ratio));
        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    static int clamp8(int v) {
        return Math.max(0, Math.min(255, v));
    }

    static void write(Path repo, String name, BufferedImage img) throws Exception {
        Path out = repo.resolve("src/main/resources/assets/starcore_forge/textures/" + name + ".png");
        Files.createDirectories(out.getParent());
        ImageIO.write(img, "png", out.toFile());
        System.out.println("Generated: " + out);
    }
}
