package ZombieGame.World;

import java.awt.Color;
import java.awt.Font;
import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.UUID;
import ZombieGame.Capabilities.DebuggableGeometry;
import ZombieGame.Capabilities.Drawable;
import ZombieGame.Coordinates.ChunkIndex;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicLayer;
import ZombieGame.Systems.Graphic.GraphicSystem;

public class Chunk implements Drawable, DebuggableGeometry {
    private final UUID uuid = UUID.randomUUID();
    public static double TILE_SIZE = 0;
    public static final int CHUNK_SIZE = 9;
    public static final int DATA_SIZE = (CHUNK_SIZE / 3) * 2 + 1;

    // BUG: For CHUNK_SIZE=15
    // Exception in thread "main" java.security.InvalidParameterException: Current tile of chunk is not inside boundaries
    //     at ZombieGame.World.Chunk.getTileToTopLeft(Chunk.java:275)
    //     at ZombieGame.World.Chunk.<init>(Chunk.java:73)
    //     at ZombieGame.World.ZombieWorld.generateChunk(ZombieWorld.java:214)
    //     at ZombieGame.World.World.processGenerationQueue(World.java:767)   
    //     at ZombieGame.World.ZombieWorld.<init>(ZombieWorld.java:53)        
    //     at ZombieGame.Game.<init>(Game.java:47)
    //     at ZombieGame.Game.main(Game.java:327)

    /**
     * Additional chunks around the visible viewport to load
     */
    public static final int CHUNK_LOADING = 1;
    /**
     * Additional chunks around the visible viewport to generate
     */
    public static final int CHUNK_GENERATING = 2;
    /**
     * Additional chunks around the visible viewport to keep the entities (with LifeComponent). Outside entities (with LifeComponent) will despawn
     */
    public static final int CHUNK_DESPAWN = 5;

    private final TileType[][] tiles;
    private final StaticSprite[][] sprites;
    private final World world;
    private final ChunkIndex index;

    /**
     * @param world
     * @param index
     * @param tiles The edges and corner types of the tiles. Needs to chunk size + 1
     */
    public Chunk(World world, ChunkIndex index, TileType[][] tiles) {
        if (Chunk.CHUNK_SIZE < 3) {
            // Because of the Tile selection it requires a corner for each tile
            throw new IllegalArgumentException("Size of chunk have to be greater or equal 3");
        }
        if (Chunk.CHUNK_SIZE % 3 != 0) {
            // Because of the Tile selection it requires a corner for each tile
            throw new IllegalArgumentException("Size of chunk have to be multiple of 3");
        }
        this.world = world;
        this.index = index;
        this.tiles = tiles;

        StaticSprite[][] sprites = new StaticSprite[CHUNK_SIZE][CHUNK_SIZE];

        if (tiles == null || tiles.length != DATA_SIZE) {
            throw new IllegalArgumentException("tiles must have chunk size + 1 numbers of rows");
        }

        for (int y = 0; y < CHUNK_SIZE; y += 3) {
            TileType[] tileRows = tiles[y / 3 * 2];
            if (tileRows == null || tileRows.length != DATA_SIZE) {
                throw new IllegalArgumentException("tiles must have chunk size + 1 numbers of columns");
            }
            for (int x = 0; x < CHUNK_SIZE; x += 3) {
                TileType tile = tileRows[x / 3 * 2];
                StaticSprite[][] temp = TileType.ClusterToSprites(getTileToTopLeft(x, y).orElse(tile), getTileToTop(x, y).orElse(tile), getTileToTopRight(x, y).orElse(tile), getTileToLeft(x, y).orElse(tile), tile, getTileToRight(x, y).orElse(tile), getTileToBottomLeft(x, y).orElse(tile), getTileToBottom(x, y).orElse(tile), getTileToBottomRight(x, y).orElse(tile));

                sprites[y + 0][x + 0] = temp[0][0];
                sprites[y + 0][x + 1] = temp[0][1];
                sprites[y + 0][x + 2] = temp[0][2];

                sprites[y + 1][x + 0] = temp[1][0];
                sprites[y + 1][x + 1] = temp[1][1];
                sprites[y + 1][x + 2] = temp[1][2];

                sprites[y + 2][x + 0] = temp[2][0];
                sprites[y + 2][x + 1] = temp[2][1];
                sprites[y + 2][x + 2] = temp[2][2];

            }
        }

        this.sprites = sprites;
    }

    public Chunk(World world, ChunkIndex index) {
        this.world = world;
        this.index = index;

        this.tiles = new TileType[DATA_SIZE][DATA_SIZE];
        this.sprites = new StaticSprite[CHUNK_SIZE][CHUNK_SIZE];
        for (int y = 0; y < CHUNK_SIZE; y++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                this.sprites[y][x] = new StaticSprite();
            }
        }
    }

    @Override
    public void draw() {
        ViewPos viewPos = this.index.toWorldPos().toViewPos(world);

        for (int y = 0; y < spritesCountY(); y++) {
            StaticSprite[] spritesRows = this.sprites[y];
            for (int x = 0; x < spritesCountX(); x++) {
                StaticSprite sprite = spritesRows[x];
                Offset offset = new Offset((x + 0.5) * sprite.getDrawWidth(), (y + 0.5) * sprite.getDrawHeight());
                sprite.draw(viewPos.add(offset));
            }
        }
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.BACKGROUND;
    }

    @Override
    public int getDepth() {
        return this.index.y();
    }

    @Override
    public void drawDebug() {
        ViewPos viewPos = this.index.toWorldPos().toViewPos(world);
        StaticSprite ref = this.sprites[0][0];

        for (int y = 0; y < spritesCountY(); y++) {
            for (int x = 0; x < spritesCountX(); x++) {
                Offset offset = new Offset((x + 0.5) * ref.getDrawWidth(), (y + 0.5) * ref.getDrawHeight());

                GraphicSystem.getInstance().drawRect(viewPos.add(offset), (int) ref.getDrawWidth(), (int) ref.getDrawHeight(), new DrawStyle().color(Color.RED));
            }
        }
        
        for (int y = 0; y < tilesCountY(); y++) {
            TileType[] tilesRows = this.tiles[y];
            for (int x = 0; x < tilesCountX(); x++) {
                TileType tile = tilesRows[x];
                String label;
                label = tile == TileType.DIRT ? "D" : "G";
                Color c = tile == TileType.DIRT ? Color.RED : Color.GREEN;

                int fontSize = (int) (ref.getDrawHeight() * 0.6);
                Offset offset = new Offset(x * 1.5 * ref.getDrawWidth(), y * 1.5 * ref.getDrawHeight()).add(ref.getDrawWidth() * -0.2, ref.getDrawHeight() * 0.2);

                GraphicSystem.getInstance().drawString(label, viewPos.add(offset), new DrawStyle().font(new Font("ARIAL", Font.BOLD, fontSize)).color(c));
            }
        }
    }

    @Override
    public DebugCategoryMask getCategoryMask() {
        return new DebugCategoryMask(DebugCategory.WORLD);
    }

    public ChunkIndex getIndex() {
        return this.index;
    }

    public int tilesCountX() {
        return this.tiles[0].length;
    }

    public int tilesCountY() {
        return this.tiles.length;
    }

    public int spritesCountX() {
        return this.sprites[0].length;
    }

    public int spritesCountY() {
        return this.sprites.length;    }

    public static double getChunkSize() {
        if (TILE_SIZE <= 0) {
            TileType.preLoadSprite();
            if (TILE_SIZE <= 0) {
                throw new InvalidParameterException("TILE_SIZE is not set jet");
            }
        }
        return TILE_SIZE * CHUNK_SIZE;
    }

    public Optional<Chunk> getChunkToTop() {
        return this.world.getChunk(this.index.atTop());
    }

    public Optional<Chunk> getChunkToTopRight() {
        return this.world.getChunk(this.index.atTopRight());
    }

    public Optional<Chunk> getChunkToRight() {
        return this.world.getChunk(this.index.atRight());
    }

    public Optional<Chunk> getChunkToBottomRight() {
        return this.world.getChunk(this.index.atBottomRight());
    }

    public Optional<Chunk> getChunkToBottom() {
        return this.world.getChunk(this.index.atBottom());
    }

    public Optional<Chunk> getChunkToBottomLeft() {
        return this.world.getChunk(this.index.atBottomLeft());
    }

    public Optional<Chunk> getChunkToLeft() {
        return this.world.getChunk(this.index.atLeft());
    }

    public Optional<Chunk> getChunkToTopLeft() {
        return this.world.getChunk(this.index.atTopLeft());
    }

    public Optional<TileType> getTileToTop(int currentX, int currentY) {
        if (currentX < 0 || currentX >= this.tilesCountX() || currentY < 0 || currentY >= this.tilesCountY()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        return getTile(currentX, currentY - 1);
    }

    public Optional<TileType> getTileToTopRight(int currentX, int currentY) {
        if (currentX < 0 || currentX >= this.tilesCountX() || currentY < 0 || currentY >= this.tilesCountY()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        return getTile(currentX + 1, currentY - 1);
    }

    public Optional<TileType> getTileToRight(int currentX, int currentY) {
        if (currentX < 0 || currentX >= this.tilesCountX() || currentY < 0 || currentY >= this.tilesCountY()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        return getTile(currentX + 1, currentY);
    }

    public Optional<TileType> getTileToBottomRight(int currentX, int currentY) {
        if (currentX < 0 || currentX >= this.tilesCountX() || currentY < 0 || currentY >= this.tilesCountY()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        return getTile(currentX + 1, currentY + 1);
    }

    public Optional<TileType> getTileToBottom(int currentX, int currentY) {
        if (currentX < 0 || currentX >= this.tilesCountX() || currentY < 0 || currentY >= this.tilesCountY()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        return getTile(currentX, currentY + 1);
    }

    public Optional<TileType> getTileToBottomLeft(int currentX, int currentY) {
        if (currentX < 0 || currentX >= this.tilesCountX() || currentY < 0 || currentY >= this.tilesCountY()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        return getTile(currentX - 1, currentY + 1);
    }

    public Optional<TileType> getTileToLeft(int currentX, int currentY) {
        if (currentX < 0 || currentX >= this.tilesCountX() || currentY < 0 || currentY >= this.tilesCountY()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        return getTile(currentX - 1, currentY);
    }

    public Optional<TileType> getTileToTopLeft(int currentX, int currentY) {
        if (currentX < 0 || currentX >= this.tilesCountX() || currentY < 0 || currentY >= this.tilesCountY()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        return getTile(currentX - 1, currentY - 1);
    }

    public Optional<TileType> getTile(int indexX, int indexY) {
        boolean isTopChunk = indexY < 0;
        boolean isBottomChunk = indexY >= this.tilesCountY();
        boolean isLeftChunk = indexX < 0;
        boolean isRightChunk = indexX >= this.tilesCountX();

        int x = indexX;
        int y = indexY;

        // Update positions to new chunk when not in this chunk
        if (isTopChunk) {
            y = this.tilesCountY() - 1;
        } else if (isBottomChunk) {
            y = 0;
        }
        if (isLeftChunk) {
            x = this.tilesCountX() - 1;
        } else if (isRightChunk) {
            x = 0;
        }

        // Select the chunk where the tile is in
        Chunk chunk = this;
        if (isTopChunk && isLeftChunk) {
            chunk = this.getChunkToTopLeft().orElse(null);
        } else if (isTopChunk && isRightChunk) {
            chunk = this.getChunkToTopRight().orElse(null);
        } else if (isBottomChunk && isLeftChunk) {
            chunk = this.getChunkToBottomLeft().orElse(null);
        } else if (isBottomChunk && isRightChunk) {
            chunk = this.getChunkToBottomRight().orElse(null);
        } else if (isTopChunk) {
            chunk = this.getChunkToTop().orElse(null);
        } else if (isBottomChunk) {
            chunk = this.getChunkToBottom().orElse(null);
        } else if (isLeftChunk) {
            chunk = this.getChunkToLeft().orElse(null);
        } else if (isRightChunk) {
            chunk = this.getChunkToRight().orElse(null);
        }

        // Chunk not found
        if (chunk == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(chunk.tiles[y][x]);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Chunk chunk = (Chunk) object;
        return this.uuid.equals(chunk.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
