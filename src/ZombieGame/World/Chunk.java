package ZombieGame.World;

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
    public static final int SIZE = 16;
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

    public Chunk(World world, ChunkIndex index, TileType[][] tiles) {
        this.world = world;
        this.index = index;
        this.tiles = tiles;

        StaticSprite[][] sprites = new StaticSprite[tilesCountY()][tilesCountX()];
        for (int y = 0; y < tilesCountY(); y++) {
            TileType[] tileRows = tiles[y];
            for (int x = 0; x < tilesCountX(); x++) {
                TileType tile = tileRows[x];
                sprites[y][x] = TileType.TileToSprite(tile, getTileToTop(x, y).orElse(tile), getTileToTopRight(x, y).orElse(tile), getTileToRight(x, y).orElse(tile), getTileToBottomRight(x, y).orElse(tile), getTileToBottom(x, y).orElse(tile), getTileToBottomLeft(x, y).orElse(tile), getTileToLeft(x, y).orElse(tile), getTileToTopLeft(x, y).orElse(tile));
            }
        }
        this.sprites = sprites;
    }

    public Chunk(World world, ChunkIndex index) {
        this.world = world;
        this.index = index;

        this.tiles = new TileType[SIZE][SIZE];
        this.sprites = new StaticSprite[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                this.sprites[y][x] = new StaticSprite();
            }
        }
    }

    @Override
    public void draw() {
        ViewPos viewPos = this.index.toWorldPos().toViewPos(world);

        for (int y = 0; y < tilesCountY(); y++) {
            StaticSprite[] spritesRows = this.sprites[y];
            for (int x = 0; x < tilesCountX(); x++) {
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

        for (int y = 0; y < tilesCountY(); y++) {
            StaticSprite[] spritesRows = this.sprites[y];
            TileType[] tilesRows = this.tiles[y];
            for (int x = 0; x < tilesCountX(); x++) {
                StaticSprite sprite = spritesRows[x];
                TileType tile = tilesRows[x];

                int fontSize = (int) (sprite.getDrawHeight() * 0.6);
                Offset offset = new Offset((x + 0.3) * sprite.getDrawWidth(), (y + 0.7) * sprite.getDrawHeight());
                String label;
                label = tile == TileType.DIRT ? "D" : "G";

                GraphicSystem.getInstance().drawString(label, viewPos.add(offset), new DrawStyle().font(new Font("ARIAL", Font.PLAIN, fontSize)));
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

    public static double getChunkSize() {
        if (TILE_SIZE <= 0) {
            TileType.preLoadSprite();
            if (TILE_SIZE <= 0) {
                throw new InvalidParameterException("TILE_SIZE is not set jet");
            }
        }
        return TILE_SIZE * SIZE;
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
