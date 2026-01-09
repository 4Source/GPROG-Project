package ZombieGame;

import ZombieGame.Sprites.StaticSprite;

public enum TileType {
    GRASS(0.5), DIRT(0.5);

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

    public static StaticSprite TileToSprite(TileType c, TileType t, TileType tr, TileType r, TileType br, TileType b, TileType bl, TileType l, TileType tl) {
        int col;
        int row;

        if (c == GRASS) {
            col = 5;
            row = 0;
        } else if (c == DIRT) {
            col = 15;
            row = 1;
        } else {
            // Missing Texture
            return new StaticSprite("", 1, 1, 3, 0, 0);
        }

        return new StaticSprite("assets\\PostApocalypse_AssetPack\\Tiles\\Background_Dark-Green_TileSet.png", 24, 17, 3, col, row);
    }
}
