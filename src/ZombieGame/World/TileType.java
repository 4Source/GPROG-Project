package ZombieGame.World;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.management.InvalidAttributeValueException;

import ZombieGame.Sprites.StaticSprite;

public enum TileType {
    GRASS(0.5), DIRT(0.5), UNKNOWN(0);

    TileType(double weight) {
        this.weight = weight;
    }

    private final double weight;
    private double start; // inclusive
    private double end; // exclusive

    private static final double TOTAL;

    static {
        double sum = 0;
        for (TileType t : values()) {
            t.start = sum;
            sum += t.weight;
            t.end = sum;
        }
        TOTAL = sum; // should be 1.0
    }

    /**
     * @param n Should be [0,1]
     * @return Select a Tile by weighted probability
     */
    public static TileType select(double n) {
        if (!Double.isFinite(n)) {
            throw new IllegalArgumentException("n must be a real number for TileType.select");
        }
        if (n < 0 || n > 1) {
            throw new IllegalArgumentException("n is out of range for TileType.select");
        }
        double r = n * TOTAL; // n is in [0,1)

        for (TileType t : values()) {
            if (r >= t.start && r < t.end) {
                return t;
            }
        }

        return values()[values().length - 1];
    }

    public double getValue() {
        return (start + end) * 0.5 / TOTAL;
    }

    private static StaticSprite getSprite(int column, int row) {
        return new StaticSprite("assets\\PostApocalypse_AssetPack\\Tiles\\Background_Dark-Green_TileSet.png", 24, 17, 3, column, row);
    }

    public static void preLoadSprite() {
        StaticSprite sprite = getSprite(0, 0);
        Chunk.TILE_SIZE = sprite.getDrawWidth();
    }

    public static StaticSprite TileToSprite(TileType c, TileType t, TileType tr, TileType r, TileType br, TileType b, TileType bl, TileType l, TileType tl) {
        try {
            // int bitMask = calcBitMask(c, t, tr, r, br, b, bl, l, tl);

            int col;
            int row;

            if (c == GRASS && t == DIRT && r == DIRT && b == GRASS && l == GRASS && (tl == GRASS || br == GRASS)) {
                // GRASS diagonal top right DIRT
                col = 18;
                row = 5;
            } else if (c == GRASS && t == GRASS && r == DIRT && b == DIRT && l == GRASS && (bl == GRASS || tr == GRASS)) {
                // GRASS diagonal bottom right DIRT
                col = 18;
                row = 6;
            } else if (c == GRASS && t == GRASS && r == GRASS && b == DIRT && l == DIRT && (tl == GRASS || br == GRASS)) {
                // GRASS diagonal bottom left DIRT
                col = 17;
                row = 6;
            } else if (c == GRASS && t == DIRT && r == GRASS && b == GRASS && l == DIRT && (tr == GRASS || bl == GRASS)) {
                // GRASS diagonal top left DIRT
                col = 17;
                row = 5;
            } else if (c == GRASS && t == DIRT && r == DIRT && b == GRASS && l == GRASS && bl == GRASS) {
                // GRASS curved top right DIRT
                col = 14;
                row = 5;
            } else if (c == GRASS && t == GRASS && r == DIRT && b == DIRT && l == GRASS && tl == GRASS) {
                // GRASS curved bottom right DIRT
                col = 14;
                row = 6;
            } else if (c == GRASS && t == GRASS && r == GRASS && b == DIRT && l == DIRT && tr == GRASS) {
                // GRASS curved bottom left DIRT
                col = 13;
                row = 6;
            } else if (c == GRASS && t == DIRT && r == GRASS && b == GRASS && l == DIRT && br == GRASS) {
                // GRASS curved top left DIRT
                col = 13;
                row = 5;
            } else if (c == GRASS && t == GRASS && r == GRASS && b == GRASS && l == GRASS && tr == DIRT) {
                // GRASS top right DIRT
                col = 14;
                row = 2;
            } else if (c == GRASS && t == GRASS && r == GRASS && b == GRASS && l == GRASS && br == DIRT) {
                // GRASS bottom right DIRT
                col = 14;
                row = 0;
            } else if (c == GRASS && t == GRASS && r == GRASS && b == GRASS && l == GRASS && bl == DIRT) {
                // GRASS bottom left DIRT
                col = 16;
                row = 0;
            } else if (c == GRASS && t == GRASS && r == GRASS && b == GRASS && l == GRASS && tl == DIRT) {
                // GRASS top left DIRT
                col = 16;
                row = 2;
            } else if (c == GRASS && t == DIRT && r == GRASS && b == GRASS && l == GRASS) {
                // GRASS top DIRT
                col = 15;
                row = 2;
            } else if (c == GRASS && t == GRASS && r == DIRT && b == GRASS && l == GRASS) {
                // GRASS right DIRT
                col = 14;
                row = 1;
            } else if (c == GRASS && t == GRASS && r == GRASS && b == DIRT && l == GRASS) {
                // GRASS bottom DIRT
                col = 15;
                row = 0;
            } else if (c == GRASS && t == GRASS && r == GRASS && b == GRASS && l == DIRT) {
                // GRASS left DIRT
                col = 16;
                row = 1;
            } else if (c == GRASS) {
                // GRASS full
                col = 5;
                row = 0;
            }
            // else if (c == DIRT && t == DIRT && r == GRASS && b == GRASS && l == DIRT && br == GRASS) {
            // // DIRT curved bottom right GRASS
            // col = 16;
            // row = 6;
            // } else if (c == DIRT && t == GRASS && r == GRASS && b == DIRT && l == DIRT && tr == GRASS) {
            // // DIRT curved top right GRASS
            // col = 16;
            // row = 5;
            // } else if (c == DIRT && t == GRASS && r == DIRT && b == DIRT && l == GRASS && tl == GRASS) {
            // // DIRT curved top left GRASS
            // col = 15;
            // row = 5;
            // } else if (c == DIRT && t == DIRT && r == DIRT && b == GRASS && l == GRASS && bl == GRASS) {
            // // DIRT curved bottom left GRASS
            // col = 15;
            // row = 6;
            // }
            // else if (c == DIRT && t == GRASS && r == GRASS && b == GRASS && l == GRASS) {
            // // DIRT surrounded GRASS
            // col = 18;
            // row = 0;
            // }
            else if (c == DIRT) {
                // DIRT full
                col = 15;
                row = 1;
            } else {
                throw new Exception("Failed tile selection");
            }

            StaticSprite sprite = getSprite(col, row);
            Chunk.TILE_SIZE = sprite.getDrawWidth();
            return sprite;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            // Missing Texture
            return new StaticSprite("", 1, 1, 3, 0, 0);
        }
    }

    public static void testTileGrid(int gridSize) {
        try {
            int tilesPerGrid = gridSize;
            int cells = tilesPerGrid * tilesPerGrid;
            int variants = 1 << cells; // 2^(gridSize*gridSize)

            int padding = 4; // space BETWEEN cells, not tiles

            // reference sprite to get sheet + tile size
            StaticSprite ref = TileToSprite(
                    TileType.GRASS,
                    TileType.GRASS, TileType.GRASS, TileType.GRASS,
                    TileType.GRASS, TileType.GRASS, TileType.GRASS,
                    TileType.GRASS, TileType.GRASS);

            BufferedImage sheet = ref.getSprite();
            int tileW = ref.getTileWidth();
            int tileH = ref.getTileHeight();

            // Each grid is gridSize × gridSize tiles
            int cellPixelW = tilesPerGrid * tileW;
            int cellPixelH = tilesPerGrid * tileH;

            // Pack grids into a square-ish atlas
            int cellPerRow = (int) Math.ceil(Math.sqrt(variants));
            int cellRows = (int) Math.ceil(variants / (double) cellPerRow);

            int imgW = cellPerRow * cellPixelW + (cellPerRow - 1) * padding;
            int imgH = cellRows * cellPixelH + (cellRows - 1) * padding;

            BufferedImage out = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = out.createGraphics();

            for (int mask = 0; mask < variants; mask++) {
                int gridX = mask % cellPerRow;
                int gridY = mask / cellPerRow;

                int baseX = gridX * (cellPixelW + padding);
                int baseY = gridY * (cellPixelH + padding);

                // build one grid
                for (int y = 0; y < tilesPerGrid; y++) {
                    for (int x = 0; x < tilesPerGrid; x++) {

                        int bit = y * tilesPerGrid + x;
                        TileType c = ((mask & (1 << bit)) != 0)
                                ? TileType.DIRT
                                : TileType.GRASS;

                        // build its neighbors (clamped to itself at edges)
                        TileType t = getFromMask(mask, tilesPerGrid, x, y - 1, c);
                        TileType r = getFromMask(mask, tilesPerGrid, x + 1, y, c);
                        TileType b = getFromMask(mask, tilesPerGrid, x, y + 1, c);
                        TileType l = getFromMask(mask, tilesPerGrid, x - 1, y, c);
                        TileType tr = getFromMask(mask, tilesPerGrid, x + 1, y - 1, c);
                        TileType br = getFromMask(mask, tilesPerGrid, x + 1, y + 1, c);
                        TileType bl = getFromMask(mask, tilesPerGrid, x - 1, y + 1, c);
                        TileType tl = getFromMask(mask, tilesPerGrid, x - 1, y - 1, c);

                        StaticSprite s = TileToSprite(c, t, tr, r, br, b, bl, l, tl);

                        int sx = s.getColumnIndex() * tileW;
                        int sy = s.getRowIndex() * tileH;

                        int dx = baseX + x * tileW;
                        int dy = baseY + y * tileH;

                        // Draw the tile to image
                        g.drawImage(sheet,
                                dx, dy, dx + tileW, dy + tileH,
                                sx, sy, sx + tileW, sy + tileH,
                                null);

                        // draw tile type letter on top (G or D)
                        g.setFont(new Font("ARIAL", Font.PLAIN, (int) (tileH * 0.6)));
                        g.setColor(c == TileType.DIRT ? Color.RED : Color.GREEN);

                        String label;
                        label = c == TileType.DIRT ? "D" : "G";

                        FontMetrics fm = g.getFontMetrics();
                        int tx = dx + (tileW - fm.stringWidth(label)) / 2;
                        int ty = dy + (tileH + fm.getAscent()) / 2 - 2;

                        g.drawString(label, tx, ty);
                    }
                }
            }

            g.dispose();
            ImageIO.write(out, "png", new File("ChunkGeneration/tile_grid_test_" + gridSize + ".png"));
            System.out.println("tile_grid_test_" + gridSize + ".png generated");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static TileType getFromMask(int mask, int size, int x, int y, TileType fallback) {
        if (x < 0 || y < 0 || x >= size || y >= size)
            return fallback;
        int bit = y * size + x;
        return ((mask & (1 << bit)) != 0) ? TileType.DIRT : TileType.GRASS;
    }
}
