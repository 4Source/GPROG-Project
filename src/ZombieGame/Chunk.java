package ZombieGame;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Optional;

import ZombieGame.Capabilities.Drawable;
import ZombieGame.Coordinates.ChunkIndex;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.StaticSprite;

public class Chunk implements Drawable {
    public static int SIZE = 8;
    public static double TILE_SIZE = 0;
    private final TileType[][] tiles;
    private final StaticSprite[][] sprites;
    private final World world;
    private final ChunkIndex coord;
    private ArrayList<Entity> entities = new ArrayList<Entity>();
    // TODO: Debug Chunk borders

    protected Chunk(World world, ChunkIndex coord, TileType[][] tiles) {
        this.world = world;
        this.coord = coord;
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

    @Override
    public void draw() {
        ViewPos viewPos = this.coord.toWorldPos().toViewPos(Entity.world);

        for (int y = 0; y < tilesCountY(); y++) {
            StaticSprite[] spritesRows = this.sprites[y];
            for (int x = 0; x < tilesCountX(); x++) {
                StaticSprite sprite = spritesRows[x];
                Offset offset = new Offset(x * sprite.getDrawWidth(), y * sprite.getDrawHeight());
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
        return this.coord.y();
    }

    public ChunkIndex getCoord() {
        return this.coord;
    }

    public int tilesCountX() {
        return this.tiles[0].length;
    }

    public int tilesCountY() {
        return this.tiles.length;
    }

    public static double getChunkSize() {
        if (TILE_SIZE <= 0) {
            throw new InvalidParameterException("TILE_SIZE is not set jet");
        }
        return TILE_SIZE * SIZE;
    }

    public Optional<Chunk> getChunkToTop() {
        return this.world.getChunk(this.coord.ToTop());
    }

    public Optional<Chunk> getChunkToTopRight() {
        return this.world.getChunk(this.coord.ToTopRight());
    }

    public Optional<Chunk> getChunkToRight() {
        return this.world.getChunk(this.coord.ToRight());
    }

    public Optional<Chunk> getChunkToBottomRight() {
        return this.world.getChunk(this.coord.ToBottomRight());
    }

    public Optional<Chunk> getChunkToBottom() {
        return this.world.getChunk(this.coord.ToBottom());
    }

    public Optional<Chunk> getChunkToBottomLeft() {
        return this.world.getChunk(this.coord.ToBottomLeft());
    }

    public Optional<Chunk> getChunkToLeft() {
        return this.world.getChunk(this.coord.ToLeft());
    }

    public Optional<Chunk> getChunkToTopLeft() {
        return this.world.getChunk(this.coord.ToTopLeft());
    }

    private Optional<TileType> getTileToTop(int currentX, int currentY) {
        if (currentY + 1 < 0 || currentY - 1 >= this.tilesCountY() || currentX + 1 < 0 || currentX - 1 >= this.tilesCountX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX;
        int y = currentY - 1;
        if (y < 0) {
            y = this.tilesCountY();
            Optional<Chunk> chunk = this.getChunkToTop();
            if (chunk.isPresent()) {
                return chunk.get().getTileToTop(x, y);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(this.tiles[y][x]);
    }

    private Optional<TileType> getTileToTopRight(int currentX, int currentY) {
        if (currentY + 1 < 0 || currentY - 1 >= this.tilesCountY() || currentX + 1 < 0 || currentX - 1 >= this.tilesCountX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX + 1;
        int y = currentY - 1;
        if (y < 0 || x >= this.tilesCountX()) {
            if (y < 0) {
                y = this.tilesCountY();
            }
            if (x >= this.tilesCountX()) {
                x = -1;
            }
            Optional<Chunk> chunk = this.getChunkToTopRight();
            if (chunk.isPresent()) {
                return chunk.get().getTileToTopRight(x, y);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(this.tiles[y][x]);
    }

    private Optional<TileType> getTileToRight(int currentX, int currentY) {
        if (currentY + 1 < 0 || currentY - 1 >= this.tilesCountY() || currentX + 1 < 0 || currentX - 1 >= this.tilesCountX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX + 1;
        int y = currentY;
        if (x >= this.tilesCountX()) {
            x = -1;
            Optional<Chunk> chunk = this.getChunkToRight();
            if (chunk.isPresent()) {
                return chunk.get().getTileToRight(x, y);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(this.tiles[y][x]);
    }

    private Optional<TileType> getTileToBottomRight(int currentX, int currentY) {
        if (currentY + 1 < 0 || currentY - 1 >= this.tilesCountY() || currentX + 1 < 0 || currentX - 1 >= this.tilesCountX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX + 1;
        int y = currentY + 1;
        if (y >= this.tilesCountY() || x >= this.tilesCountX()) {
            if (y >= this.tilesCountY()) {
                y = -1;
            }
            if (x >= this.tilesCountX()) {
                x = -1;
            }
            Optional<Chunk> chunk = this.getChunkToBottomRight();
            if (chunk.isPresent()) {
                return chunk.get().getTileToBottomRight(x, y);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(this.tiles[y][x]);
    }

    private Optional<TileType> getTileToBottom(int currentX, int currentY) {
        if (currentY + 1 < 0 || currentY - 1 >= this.tilesCountY() || currentX + 1 < 0 || currentX - 1 >= this.tilesCountX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX;
        int y = currentY + 1;
        if (y >= this.tilesCountY()) {
            y = -1;
            Optional<Chunk> chunk = this.getChunkToBottom();
            if (chunk.isPresent()) {
                return chunk.get().getTileToBottom(x, y);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(this.tiles[y][x]);
    }

    private Optional<TileType> getTileToBottomLeft(int currentX, int currentY) {
        if (currentY + 1 < 0 || currentY - 1 >= this.tilesCountY() || currentX + 1 < 0 || currentX - 1 >= this.tilesCountX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX - 1;
        int y = currentY + 1;
        if (y >= this.tilesCountY() || x < 0) {
            if (y >= this.tilesCountY()) {
                y = -1;
            }
            if (x < 0) {
                x = this.tilesCountX();
            }
            Optional<Chunk> chunk = this.getChunkToBottomLeft();
            if (chunk.isPresent()) {
                return chunk.get().getTileToBottomLeft(x, y);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(this.tiles[y][x]);
    }

    private Optional<TileType> getTileToLeft(int currentX, int currentY) {
        if (currentY + 1 < 0 || currentY - 1 >= this.tilesCountY() || currentX + 1 < 0 || currentX - 1 >= this.tilesCountX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX - 1;
        int y = currentY;
        if (x < 0) {
            x = this.tilesCountX();
            Optional<Chunk> chunk = this.getChunkToLeft();
            if (chunk.isPresent()) {
                return chunk.get().getTileToLeft(x, y);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(this.tiles[y][x]);
    }

    private Optional<TileType> getTileToTopLeft(int currentX, int currentY) {
        if (currentY + 1 < 0 || currentY - 1 >= this.tilesCountY() || currentX + 1 < 0 || currentX - 1 >= this.tilesCountX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX - 1;
        int y = currentY - 1;
        if (y < 0 || x < 0) {
            if (y < 0) {
                y = this.tilesCountY();
            }
            if (x < 0) {
                x = this.tilesCountX();
            }
            Optional<Chunk> chunk = this.getChunkToTopLeft();
            if (chunk.isPresent()) {
                return chunk.get().getTileToTopLeft(x, y);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(this.tiles[y][x]);
    }
}
