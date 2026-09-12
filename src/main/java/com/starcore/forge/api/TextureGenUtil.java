package com.starcore.forge.api;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * 纯 Java PNG 纹理生成工具（无需外部依赖）
 * 根据材料颜色和变体形状掩码生成 16×16 物品纹理
 */
public class TextureGenUtil {

    // 游戏运行目录为 run/<name>，回写仓库文件必须用 ../../ 相对路径（详见 BatchMaterialRegistrar 说明）
    private static final String TEXTURE_DIR_SRC = "../../src/main/resources/assets/starcore_forge/textures/item/";
    private static final String TEXTURE_DIR_BUILD = "../../build/resources/main/assets/starcore_forge/textures/item/";

    /**
     * 生成并保存纹理 PNG
     */
    public static void generate(MaterialConfig material, MaterialVariantType type, long seed) {
        boolean[][] mask = type.getShapeMask();
        Random rng = new Random(seed);
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (!mask[y][x]) {
                    img.setRGB(x, y, 0x00000000); // 透明
                    continue;
                }

                // 计算是否边缘像素
                boolean isEdge = false;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int ny = y + dy, nx = x + dx;
                        if (ny < 0 || ny >= 16 || nx < 0 || nx >= 16 || !mask[ny][nx]) {
                            isEdge = true;
                            break;
                        }
                    }
                    if (isEdge) break;
                }

                int r, g, b;
                if (isEdge) {
                    // 边缘：深色描边
                    r = clamp(material.getRed() - 85 + rng.nextInt(15));
                    g = clamp(material.getGreen() - 85 + rng.nextInt(15));
                    b = clamp(material.getBlue() - 85 + rng.nextInt(15));
                } else {
                    // 内部：主色到副色渐变 + 噪声
                    double t = (x + y) / 30.0 + rng.nextDouble() * 0.15;
                    r = clamp((int) (material.getRed() * (1 - t) + material.getSecRed() * t) + rng.nextInt(21) - 10);
                    g = clamp((int) (material.getGreen() * (1 - t) + material.getSecGreen() * t) + rng.nextInt(21) - 10);
                    b = clamp((int) (material.getBlue() * (1 - t) + material.getSecBlue() * t) + rng.nextInt(21) - 10);
                }


                img.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        // DUST 后处理：3D 光照 + 亮暗色块间隔
        if (type == MaterialVariantType.DUST) {
            applyDustLighting(img, mask, material, seed);
        }

        String filename = material.getRegistryName(type) + ".png";
        String[] dirs = {
            TEXTURE_DIR_SRC,
            TEXTURE_DIR_BUILD
        };
        boolean saved = false;
        for (String dirPath : dirs) {
            try {
                File dir = new File(dirPath);
                dir.mkdirs();
                File out = new File(dir, filename);
                ImageIO.write(img, "PNG", out);
                saved = true;
            } catch (Exception ignored) {}
        }
        if (saved) {
            System.out.println("[StarCore] Generated texture: " + filename);
        } else {
            System.err.println("[StarCore] Failed to generate texture: " + filename);
        }
    }

    /**
     * DUST 专用后处理：3D 方向光照 + 内部亮暗色块间隔
     */
    private static void applyDustLighting(BufferedImage img, boolean[][] mask,
                                           MaterialConfig material, long seed) {
        Random rng = new Random(seed + 777);
        // 先收集所有像素颜色
        int[][] baseR = new int[16][16];
        int[][] baseG = new int[16][16];
        int[][] baseB = new int[16][16];
        boolean[][] isEdge = new boolean[16][16];

        // 重新检测边缘
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (!mask[y][x]) continue;
                Color c = new Color(img.getRGB(x, y), true);
                baseR[y][x] = c.getRed();
                baseG[y][x] = c.getGreen();
                baseB[y][x] = c.getBlue();
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int ny = y + dy, nx = x + dx;
                        if (ny < 0 || ny >= 16 || nx < 0 || nx >= 16 || !mask[ny][nx]) {
                            isEdge[y][x] = true;
                        }
                    }
                }
            }
        }

        // 找到菱形中心（mask 的质心）
        double cx = 0, cy = 0;
        int count = 0;
        for (int y = 0; y < 16; y++)
            for (int x = 0; x < 16; x++)
                if (mask[y][x]) { cx += x; cy += y; count++; }
        cx /= count;
        cy /= count;

        // 计算边缘到中心的平均距离，确定内部区域
        double avgRad = 0;
        int edgeCount = 0;
        for (int y = 0; y < 16; y++)
            for (int x = 0; x < 16; x++)
                if (mask[y][x] && isEdge[y][x]) {
                    avgRad += Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                    edgeCount++;
                }
        avgRad /= edgeCount;

        // 对角线投影轴：从左上到右下
        // proj > 0 = 偏右下, proj < 0 = 偏左上
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (!mask[y][x]) continue;

                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                double proj = (x - cx) + (y - cy); // 左上(-)→右下(+)
                double maxProj = avgRad * 1.4;
                double t = clamp01((proj + maxProj) / (2 * maxProj)); // 0=左上, 1=右下

                if (isEdge[y][x]) {
                    // 边缘：左上亮、右下暗
                    // 左上端亮化：最多加亮到内部基准亮度的 80%
                    // 右下端暗化：最多额外减暗 60
                    double edgeBright = 0;
                    if (t < 0.5) {
                        // 左上：亮化
                        edgeBright = (0.5 - t) * 2.0 * 40; // 0~40
                    } else {
                        // 右下：暗化
                        edgeBright = -(t - 0.5) * 2.0 * 50; // 0~-50
                    }
                    // 只在非边缘暗纹上调整，不对内部色块做此处理
                    int r = clamp(baseR[y][x] + (int) edgeBright);
                    int g = clamp(baseG[y][x] + (int) edgeBright);
                    int b = clamp(baseB[y][x] + (int) edgeBright);
                    img.setRGB(x, y, new Color(r, g, b).getRGB());
                } else {
                    // 内部：亮暗色块间隔排列（类 checkerboard）
                    int check = (x + y * 3) % 5;
                    int adj = 0;
                    if (check == 0 || check == 3) {
                        adj = 15 + rng.nextInt(15); // 亮块
                    } else if (check == 1 || check == 4) {
                        adj = -(15 + rng.nextInt(15)); // 暗块
                    }
                    int r = clamp(baseR[y][x] + adj);
                    int g = clamp(baseG[y][x] + adj);
                    int b = clamp(baseB[y][x] + adj);
                    img.setRGB(x, y, new Color(r, g, b).getRGB());
                }
            }
        }
    }

    private static double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
