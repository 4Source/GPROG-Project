package ZombieGame.World;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.management.InvalidAttributeValueException;

import ZombieGame.Sprites.StaticSprite;

public enum TileType {
    GRASS(1), DIRT(1);

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

    public static StaticSprite[][] ClusterToSprites(TileType tl, TileType t, TileType tr, TileType l, TileType c, TileType r, TileType bl, TileType b, TileType br) {
        StaticSprite[][] sprites = new StaticSprite[3][3];
        try {
            int bitMask = calcBitMask(tl, t, tr, l, c, r, bl, b, br);

            TileCluster cluster = ClusterTileLookup.TABLE[bitMask];
            if (cluster == null) {
                throw new Exception(String.format("Failed tile selection for %d with ([[%s, %s, %s], [%s, %s, %s], [%s, %s, %s]]", bitMask, tl, t, tr, l, c, r, bl, b, br));
            }

            sprites[0][0] = getSprite(cluster.tl.column, cluster.tl.row);
            sprites[0][1] = getSprite(cluster.t.column, cluster.t.row);
            sprites[0][2] = getSprite(cluster.tr.column, cluster.tr.row);

            sprites[1][0] = getSprite(cluster.l.column, cluster.l.row);
            sprites[1][1] = getSprite(cluster.c.column, cluster.c.row);
            sprites[1][2] = getSprite(cluster.r.column, cluster.r.row);

            sprites[2][0] = getSprite(cluster.bl.column, cluster.bl.row);
            sprites[2][1] = getSprite(cluster.b.column, cluster.b.row);
            sprites[2][2] = getSprite(cluster.br.column, cluster.br.row);

        } catch (Exception e) {
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    sprites[y][x] = new StaticSprite("", 1, 1, 3, 0, 0);
                }
            }
            System.err.println(e.getMessage());
        }
        return sprites;
    }

    private static int calcBitMask(TileType tl, TileType t, TileType tr, TileType l, TileType c, TileType r, TileType bl, TileType b, TileType br) throws InvalidAttributeValueException {
        // Grass = 0
        // Dirt = 1
        int bitMask = 0;
        int significance;

        // Significance of center tile + center tile as fallback is tile is not set to specific type
        significance = 8;
        switch (c) {
            case GRASS:
                // TODO:
                // throw new InvalidAttributeValueException("The type grass in center is not supported");
            case DIRT:
                // bitMask |= (1 << significance);
                break;
            default:
                throw new InvalidAttributeValueException("The type of Center tile is required");
        }

        // Significance of top tile
        significance = 0;
        switch (t) {
            case GRASS:
                bitMask &= ~(1 << significance);
                break;
            case DIRT:
                bitMask |= (1 << significance);
                break;
            default:
                break;
        }

        // Significance of top right tile
        significance = 1;
        switch (tr) {
            case GRASS:
                bitMask &= ~(1 << significance);
                break;
            case DIRT:
                bitMask |= (1 << significance);
                break;
            default:
                break;
        }

        // Significance of right tile
        significance = 2;
        switch (r) {
            case GRASS:
                bitMask &= ~(1 << significance);
                break;
            case DIRT:
                bitMask |= (1 << significance);
                break;
            default:
                break;
        }

        // Significance of bottom right tile
        significance = 3;
        switch (br) {
            case GRASS:
                bitMask &= ~(1 << significance);
                break;
            case DIRT:
                bitMask |= (1 << significance);
                break;
            default:
                break;
        }

        // Significance of bottom tile
        significance = 4;
        switch (b) {
            case GRASS:
                bitMask &= ~(1 << significance);
                break;
            case DIRT:
                bitMask |= (1 << significance);
                break;
            default:
                break;
        }

        // Significance of bottom left tile
        significance = 5;
        switch (bl) {
            case GRASS:
                bitMask &= ~(1 << significance);
                break;
            case DIRT:
                bitMask |= (1 << significance);
                break;
            default:
                break;
        }

        // Significance of left tile
        significance = 6;
        switch (l) {
            case GRASS:
                bitMask &= ~(1 << significance);
                break;
            case DIRT:
                bitMask |= (1 << significance);
                break;
            default:
                break;
        }

        // Significance of top left tile
        significance = 7;
        switch (tl) {
            case GRASS:
                bitMask &= ~(1 << significance);
                break;
            case DIRT:
                bitMask |= (1 << significance);
                break;
            default:
                break;
        }

        return bitMask;
    }

    public static void testTileClusters() {
        int clusterPerGridColumn = 1;
        int clusterPerGridRow = 1;
        int dataPerGridColumn = clusterPerGridColumn * 2 + 1;
        int dataPerGridRow = clusterPerGridRow * 2 + 1;

        int variants = 1 << (dataPerGridColumn * dataPerGridRow); // 2^(gridSize*gridSize)

        int cellPadding = 4; // space BETWEEN cells, not tiles

        // reference sprite to get sheet + tile size
        StaticSprite ref = ClusterToSprites(TileType.GRASS, TileType.GRASS, TileType.GRASS, TileType.GRASS, TileType.GRASS, TileType.GRASS, TileType.GRASS, TileType.GRASS, TileType.GRASS)[0][0];

        int tileW = ref.getTileWidth();
        int tileH = ref.getTileHeight();

        // Each grid is gridSize × gridSize tiles
        int cellPixelW = dataPerGridColumn * tileW;
        int cellPixelH = dataPerGridRow * tileH;

        // Pack grids into a square-ish atlas
        int cellPerRow = (int) Math.ceil(Math.sqrt(variants));
        int cellRows = (int) Math.ceil(variants / (double) cellPerRow);

        int paddingFile = 10;
        int imgW = cellPerRow * cellPixelW + (cellPerRow - 1) * cellPadding + 2 * paddingFile;
        int imgH = cellRows * cellPixelH + (cellRows - 1) * cellPadding + 2 * paddingFile;

        BufferedImage out = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();

        try {
            for (int bitMask = 0; bitMask < variants; bitMask++) {
                int gridX = bitMask % cellPerRow;
                int gridY = bitMask / cellPerRow;

                int baseX = gridX * (cellPixelW + cellPadding) + paddingFile;
                int baseY = gridY * (cellPixelH + cellPadding) + paddingFile;

                TileType[][] tiles = new TileType[dataPerGridRow][dataPerGridColumn];
                tiles[0][0] = ((bitMask & (1 << 7)) != 0) ? TileType.DIRT : TileType.GRASS; // tl
                tiles[0][1] = ((bitMask & (1 << 0)) != 0) ? TileType.DIRT : TileType.GRASS; // t
                tiles[0][2] = ((bitMask & (1 << 1)) != 0) ? TileType.DIRT : TileType.GRASS; // tr

                tiles[1][0] = ((bitMask & (1 << 6)) != 0) ? TileType.DIRT : TileType.GRASS; // l
                tiles[1][1] = ((bitMask & (1 << 8)) != 0) ? TileType.DIRT : TileType.GRASS; // c
                tiles[1][2] = ((bitMask & (1 << 2)) != 0) ? TileType.DIRT : TileType.GRASS; // r

                tiles[2][0] = ((bitMask & (1 << 5)) != 0) ? TileType.DIRT : TileType.GRASS; // bl
                tiles[2][1] = ((bitMask & (1 << 4)) != 0) ? TileType.DIRT : TileType.GRASS; // b
                tiles[2][2] = ((bitMask & (1 << 3)) != 0) ? TileType.DIRT : TileType.GRASS; // br

                StaticSprite[][] cluster = ClusterToSprites(tiles[0][0], tiles[0][1], tiles[0][2], tiles[1][0], tiles[1][1], tiles[1][2], tiles[2][0], tiles[2][1], tiles[2][2]);

                // build one grid
                for (int y = 0; y < dataPerGridRow; y++) {
                    for (int x = 0; x < dataPerGridColumn; x++) {
                        StaticSprite s = cluster[y][x];
                        TileType t = tiles[y][x];

                        int sx = s.getColumnIndex() * tileW;
                        int sy = s.getRowIndex() * tileH;

                        int dx = baseX + x * tileW;
                        int dy = baseY + y * tileH;

                        // Draw the tile to image
                        g.drawImage(s.getSprite(),
                                dx, dy, dx + tileW, dy + tileH,
                                sx, sy, sx + tileW, sy + tileH,
                                null);

                        // draw tile type letter on top (G or D)
                        g.setFont(new Font("ARIAL", Font.PLAIN, (int) (tileH * 0.6)));
                        g.setColor(t == TileType.DIRT ? Color.RED : Color.GREEN);

                        String label;
                        label = "" + t.toString().charAt(0);

                        // Ignore center for now
                        if (x == 1 && y == 1) {
                            label = Integer.toString(bitMask);
                            if (ClusterTileLookup.TABLE[bitMask] != null) {
                                g.setColor(Color.WHITE);
                            } else {
                                g.setColor(Color.BLUE);
                            }
                        }

                        FontMetrics fm = g.getFontMetrics();
                        int tx = dx + (tileW - fm.stringWidth(label)) / 2;
                        int ty = dy + (tileH + fm.getAscent()) / 2 - 2;

                        g.drawString(label, tx, ty);
                    }
                }
            }

            g.dispose();
            ImageIO.write(out, "png", new File("ChunkGeneration/tile_grid_test_" + dataPerGridColumn + "_" + dataPerGridRow + ".png"));
            System.out.println("tile_grid_test_" + dataPerGridColumn + "_" + dataPerGridRow + ".png generated");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

enum TilePos {
    /**
     * Solid grass tile with no dirt edges.
     */
    GRASS_FULL(5, 0),
    /**
     * Grass tile with dirt exposed on the top right corner.
     */
    GRASS_TOP_RIGHT_DIRT(14, 2),
    /**
     * Grass tile with dirt exposed on the bottom right corner.
     */
    GRASS_BOTTOM_RIGHT_DIRT(14, 0),
    /**
     * Grass tile with dirt exposed on the bottom left corner.
     */
    GRASS_BOTTOM_LEFT_DIRT(16, 0),
    /**
     * Grass tile with dirt exposed on the top left corner.
     */
    GRASS_TOP_LEFT_DIRT(16, 2),
    /**
     * Grass tile with dirt exposed along the top edge.
     */
    GRASS_TOP_DIRT(15, 2),
    /**
     * Grass tile with dirt exposed along the right edge.
     */
    GRASS_RIGHT_DIRT(14, 1),
    /**
     * Grass tile with dirt exposed along the bottom edge.
     */
    GRASS_BOTTOM_DIRT(15, 0),
    /**
     * Grass tile with dirt exposed along the left edge.
     */
    GRASS_LEFT_DIRT(16, 1),
    /**
     * Grass tile with dirt exposed on the top and right edges forming a corner cut-in.
     */
    GRASS_TOP_AND_RIGHT_DIRT(14, 7),
    /**
     * Grass tile with dirt exposed on the bottom and right edges forming a corner cut-in.
     */
    GRASS_BOTTOM_AND_RIGHT_DIRT(14, 8),
    /**
     * Grass tile with dirt exposed on the bottom and left edges forming a corner cut-in.
     */
    GRASS_BOTTOM_AND_LEFT_DIRT(13, 8),
    /**
     * Grass tile with dirt exposed on the top and left edges forming a corner cut-in.
     */
    GRASS_TOP_AND_LEFT_DIRT(13, 7),
    /**
     * Solid dirt tile with no grass edges.
     */
    DIRT_FULL(15, 1);

    TilePos(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public final int column;
    public final int row;
}

class TileCluster {
    public final TilePos tl, t, tr;
    public final TilePos l, c, r;
    public final TilePos bl, b, br;

    TileCluster(TilePos tl, TilePos t, TilePos tr, TilePos l, TilePos c, TilePos r, TilePos bl, TilePos b, TilePos br) {
        this.tl = tl;
        this.t = t;
        this.tr = tr;
        this.l = l;
        this.c = c;
        this.r = r;
        this.bl = bl;
        this.b = b;
        this.br = br;
    }

    public static final int size() {
        return 9;
    }
}

/**
 * This should not be changed this table is derived from the blob tile set. if the textures should be changed this could be done in {@link TilePos} by changing the column and row values
 * 
 * @see https://www.boristhebrave.com/permanent/24/06/cr31/stagecast/wang/blob.html
 */
class ClusterTileLookup {
    public static final TileCluster[] TABLE = new TileCluster[(int) Math.pow(TileType.values().length, TileCluster.size())];

    static {
        TABLE[0] = new TileCluster(TilePos.GRASS_FULL, TilePos.GRASS_FULL, TilePos.GRASS_FULL, TilePos.GRASS_FULL, TilePos.GRASS_FULL, TilePos.GRASS_FULL, TilePos.GRASS_FULL, TilePos.GRASS_FULL, TilePos.GRASS_FULL);
        TABLE[1] = new TileCluster(TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_TOP_RIGHT_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_LEFT_DIRT);
        TABLE[4] = new TileCluster(TilePos.GRASS_BOTTOM_RIGHT_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_RIGHT_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT);
        TABLE[16] = new TileCluster(TilePos.GRASS_BOTTOM_RIGHT_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_LEFT_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT);
        TABLE[64] = new TileCluster(TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_LEFT_DIRT);
        TABLE[5] = new TileCluster(TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_RIGHT_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT);
        TABLE[20] = new TileCluster(TilePos.GRASS_BOTTOM_RIGHT_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[80] = new TileCluster(TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT);
        TABLE[65] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_LEFT_DIRT);
        TABLE[7] = new TileCluster(TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_RIGHT_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT);
        TABLE[28] = new TileCluster(TilePos.GRASS_BOTTOM_RIGHT_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[112] = new TileCluster(TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT);
        TABLE[193] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_LEFT_DIRT);
        TABLE[17] = new TileCluster(TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT);
        TABLE[68] = new TileCluster(TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT);
        TABLE[21] = new TileCluster(TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[84] = new TileCluster(TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[81] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT);
        TABLE[69] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT);
        TABLE[23] = new TileCluster(TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[92] = new TileCluster(TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[113] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT);
        TABLE[197] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT);
        TABLE[29] = new TileCluster(TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[116] = new TileCluster(TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[209] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT);
        TABLE[71] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT);
        TABLE[31] = new TileCluster(TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[124] = new TileCluster(TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.GRASS_BOTTOM_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[241] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_LEFT_DIRT);
        TABLE[199] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT, TilePos.GRASS_TOP_DIRT);
        TABLE[85] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[87] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[93] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[117] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[213] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[95] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[125] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[245] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[215] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[119] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[221] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[127] = new TileCluster(TilePos.GRASS_BOTTOM_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[253] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_BOTTOM_AND_LEFT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[247] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_LEFT_DIRT);
        TABLE[223] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.GRASS_TOP_AND_RIGHT_DIRT, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
        TABLE[255] = new TileCluster(TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL, TilePos.DIRT_FULL);
    }
}