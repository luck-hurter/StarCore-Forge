import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * 生成流体单元贴图：空单元 cell / 水单元 water_cell / 岩浆单元 lava_cell
 * 造型：平顶全封闭金属罐（无瓶口/旋盖凸起），罐身中部一条观察窗——
 * 空罐观察窗为真透明（alpha 0），水/岩浆罐窗内直接显示流体色。
 * 开发期一次性工具，不参与构建。
 * 用法: java tools/GenerateCellTextures.java <项目根目录>
 */
public class GenerateCellTextures {

    // 金属配色
    static final int OUTLINE = 0x3C424B;   // 罐体描边
    static final int HL = 0xE9EDF1;        // 高光
    static final int CAP_LIGHT = 0xC9CFD6; // 亮金属
    static final int CAP_MID = 0x9BA3AD;   // 中金属
    static final int CAP_DARK = 0x6B727C;  // 暗金属

    // 流体环带配色：液面 / 主体 / 深部
    record FluidColors(int surface, int body, int deep) {}

    static final FluidColors WATER = new FluidColors(0x82B9FF, 0x4A82E8, 0x2F5FC9);
    static final FluidColors LAVA = new FluidColors(0xFFD97A, 0xF58A1E, 0xC25E0A);

    // 罐体几何：x=3..12 为罐身（含左右描边），y=1..14
    static final int BODY_L = 3, BODY_R = 12;
    static final int IN_L = 4, IN_R = 11;
    static final int BODY_TOP = 1, BODY_BOTTOM = 13;
    static final int LID_SEAM = 2;                  // 顶盖接缝
    static final int WINDOW_TOP = 7, WINDOW_BOTTOM = 9; // 观察窗
    static final int BASE_ROW = 14;                 // 圆角底

    /** 各内壁列的金属明暗系数，模拟圆柱受光（x=4..11） */
    static final double[] COLUMN_LIGHT = {0.92, 1.00, 1.06, 1.08, 1.00, 0.92, 0.78, 0.62};

    public static void main(String[] args) throws Exception {
        Path repo = Path.of(args[0]);
        Path texDir = repo.resolve("src/main/resources/assets/starcore_forge/textures/item");
        Files.createDirectories(texDir);
        write(texDir.resolve("cell.png"), paintCell(null));
        write(texDir.resolve("water_cell.png"), paintCell(WATER));
        write(texDir.resolve("lava_cell.png"), paintCell(LAVA));
        System.out.println("Cell textures generated.");
    }

    static BufferedImage paintCell(FluidColors fluid) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Random rng = new Random(20240916L);

        for (int y = BODY_TOP; y <= BODY_BOTTOM; y++) {
            put(img, BODY_L, y, OUTLINE);
            put(img, BODY_R, y, OUTLINE);
            for (int x = IN_L; x <= IN_R; x++) {
                int color;
                if (y == BODY_TOP) {
                    color = CAP_LIGHT;                       // 平顶盖面
                } else if (y == LID_SEAM) {
                    color = scale(CAP_MID, 0.82);            // 顶盖接缝
                } else if (y == BODY_BOTTOM) {
                    color = CAP_DARK;                        // 底部封口
                } else if (y >= WINDOW_TOP && y <= WINDOW_BOTTOM) {
                    if (fluid == null) continue;             // 空罐观察窗 = 真透明
                    color = windowColor(fluid, x, y, rng);
                } else {
                    color = metalColor(x, rng);
                    if (y == WINDOW_TOP - 1 || y == WINDOW_BOTTOM + 1) {
                        color = scale(color, 0.86);          // 观察窗上下凹槽线
                    }
                }
                put(img, x, y, color);
            }
        }

        // 圆角底
        for (int x = 5; x <= 10; x++) {
            put(img, x, BASE_ROW, x == 5 || x == 10 ? CAP_DARK : CAP_MID);
        }
        return img;
    }

    /** 罐身金属：按列明暗 + 轻微噪声 */
    static int metalColor(int x, Random rng) {
        int base = switch (x) {
            case 4 -> CAP_MID;
            case 5, 8 -> CAP_LIGHT;
            case 6, 7 -> HL;
            case 9 -> CAP_MID;
            case 10 -> CAP_DARK;
            default -> scale(CAP_DARK, 0.82); // x=11 深阴影
        };
        int n = rng.nextInt(7) - 3;
        return clampRGB(((base >> 16) & 0xFF) + n, ((base >> 8) & 0xFF) + n, (base & 0xFF) + n);
    }

    /** 观察窗流体：顶部液面提亮、向下加深；岩浆带亮点 */
    static int windowColor(FluidColors f, int x, int y, Random rng) {
        int color = y == WINDOW_TOP ? f.surface()
                : blend(f.body(), f.deep(), y == WINDOW_BOTTOM ? 0.8 : 0.3);
        int n = rng.nextInt(11) - 5;
        color = clampRGB(((color >> 16) & 0xFF) + n, ((color >> 8) & 0xFF) + n, (color & 0xFF) + n);
        if (f == LAVA && rng.nextInt(14) == 0) color = 0xFFE9A0;
        return color;
    }

    static int scale(int color, double k) {
        return clampRGB(
                (int) Math.round(((color >> 16) & 0xFF) * k),
                (int) Math.round(((color >> 8) & 0xFF) * k),
                (int) Math.round((color & 0xFF) * k));
    }

    static int blend(int a, int b, double t) {
        int r = (int) Math.round(((a >> 16) & 0xFF) * (1 - t) + ((b >> 16) & 0xFF) * t);
        int g = (int) Math.round(((a >> 8) & 0xFF) * (1 - t) + ((b >> 8) & 0xFF) * t);
        int bl = (int) Math.round((a & 0xFF) * (1 - t) + (b & 0xFF) * t);
        return clampRGB(r, g, bl);
    }

    static int clampRGB(int r, int g, int b) {
        return (clamp8(r) << 16) | (clamp8(g) << 8) | clamp8(b);
    }

    static int clamp8(int v) {
        return Math.max(0, Math.min(255, v));
    }

    static void put(BufferedImage img, int x, int y, int rgb) {
        img.setRGB(x, y, 0xFF000000 | rgb);
    }

    static void write(Path out, BufferedImage img) throws Exception {
        ImageIO.write(img, "png", out.toFile());
        System.out.println("Generated: " + out);
    }
}
