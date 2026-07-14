package com.starcore.forge.api;

/**
 * 材料变体类型：板、杆、齿轮、粉
 * 每种类型内置 16×16 形状掩码，供纹理生成使用
 */
public enum MaterialVariantType {

    PLATE("plate", "板", "Plate") {
        @Override
        public boolean[][] getShapeMask() {
            boolean[][] mask = new boolean[16][16];
            double angle = Math.toRadians(-30);
            double cx = 7.5, cy = 7.5;
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    double dx = x - cx;
                    double dy = y - cy;
                    double rx = dx * Math.cos(angle) - dy * Math.sin(angle);
                    double ry = dx * Math.sin(angle) + dy * Math.cos(angle);
                    if (Math.abs(rx) <= 5.4 && Math.abs(ry) <= 5.4) {
                        mask[y][x] = true;
                    }
                }
            }
            return mask;
        }
    },

    ROD("rod", "杆", "Rod") {
        @Override
        public boolean[][] getShapeMask() {
            boolean[][] mask = new boolean[16][16];
            double angle = Math.toRadians(135);
            double cx = 7.5, cy = 7.5;
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    double dx = x - cx;
                    double dy = y - cy;
                    double rx = dx * Math.cos(angle) - dy * Math.sin(angle);
                    double ry = dx * Math.sin(angle) + dy * Math.cos(angle);
                    if (Math.abs(rx) <= 1.5 && Math.abs(ry) <= 8.0) {
                        mask[y][x] = true;
                    }
                }
            }
            return mask;
        }
    },

    GEAR("gear", "齿轮", "Gear") {
        @Override
        public boolean[][] getShapeMask() {
            boolean[][] mask = new boolean[16][16];
            double cx = 7.5, cy = 7.5;
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    double dx = x - cx;
                    double dy = y - cy;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    double angle = Math.atan2(dy, dx);
                    double teeth = 1.0 + 0.35 * Math.abs(Math.cos(angle * 4));
                    if (dist <= 5.5 * teeth && dist >= 2.0) {
                        mask[y][x] = true;
                    }
                }
            }
            for (int y = 5; y <= 10; y++) {
                for (int x = 5; x <= 10; x++) {
                    double dx = x - 7.5, dy = y - 7.5;
                    if (Math.sqrt(dx * dx + dy * dy) <= 2.2) {
                        mask[y][x] = false;
                    }
                }
            }
            return mask;
        }
    },

    DUST("dust", "粉", "Dust") {
        @Override
        public boolean[][] getShapeMask() {
            // 完全仿原版红石粉形状：居中菱形
            boolean[][] mask = new boolean[16][16];
            // y=3: cols 7-8
            for (int x = 7; x <= 8; x++) mask[3][x] = true;
            // y=4: cols 6-9
            for (int x = 6; x <= 9; x++) mask[4][x] = true;
            // y=5: cols 5-10
            for (int x = 5; x <= 10; x++) mask[5][x] = true;
            // y=6: cols 4-11
            for (int x = 4; x <= 11; x++) mask[6][x] = true;
            // y=7: cols 3-12
            for (int x = 3; x <= 12; x++) mask[7][x] = true;
            // y=8-10: cols 2-13
            for (int y = 8; y <= 10; y++)
                for (int x = 2; x <= 13; x++) mask[y][x] = true;
            // y=11: cols 3-12
            for (int x = 3; x <= 12; x++) mask[11][x] = true;
            // y=12: cols 4-11
            for (int x = 4; x <= 11; x++) mask[12][x] = true;
            // y=13: cols 6-9
            for (int x = 6; x <= 9; x++) mask[13][x] = true;
            return mask;
        }
    };

    public final String suffix;
    public final String zhName;
    public final String enName;

    MaterialVariantType(String suffix, String zhName, String enName) {
        this.suffix = suffix;
        this.zhName = zhName;
        this.enName = enName;
    }

    public abstract boolean[][] getShapeMask();
}
