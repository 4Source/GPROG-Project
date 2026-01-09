package ZombieGame;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Optional;

import ZombieGame.Capabilities.Drawable;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.StaticSprite;

public class Chunk implements Drawable {
    private final TileType[][] tiles;
    private final StaticSprite[][] sprites;
    private final World world;
    private final ChunkCoord coord;
    private ArrayList<Entity> entities = new ArrayList<Entity>();

    protected Chunk(World world, ChunkCoord coord, TileType[][] tiles) {
        this.world = world;
        this.coord = coord;
        this.tiles = tiles;

        StaticSprite[][] sprites = new StaticSprite[tilesSizeY()][tilesSizeX()];
        for (int y = 0; y < tilesSizeY(); y++) {
            TileType[] tileRows = tiles[y];
            for (int x = 0; x < tilesSizeX(); x++) {
                TileType tile = tileRows[x];
                sprites[y][x] = TileType.TileToSprite(tile, getTileToTop(x, y).orElse(tile), getTileToTopRight(x, y).orElse(tile), getTileToRight(x, y).orElse(tile), getTileToBottomRight(x, y).orElse(tile), getTileToBottom(x, y).orElse(tile), getTileToBottomLeft(x, y).orElse(tile), getTileToLeft(x, y).orElse(tile), getTileToTopLeft(x, y).orElse(tile));
            }
        }
        this.sprites = sprites;
    }

    @Override
    public void draw() {
        double posX = Entity.world.worldToViewPosX(this.world.chunkCoordToWorldPosX(this.coord));
        double posY = Entity.world.worldToViewPosY(this.world.chunkCoordToWorldPosY(this.coord));

        for (int y = 0; y < tilesSizeY(); y++) {
            StaticSprite[] spritesRows = this.sprites[y];
            for (int x = 0; x < tilesSizeX(); x++) {
                StaticSprite sprite = spritesRows[x];
                double yOffset = y * sprite.getDrawHeight();
                double xOffset = x * sprite.getDrawWidth();
                sprite.draw(posX + xOffset, posY + yOffset);
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

    public ChunkCoord getCoord() {
        return this.coord;
    }

    public int tilesSizeX() {
        return this.tiles[0].length;
    }

    public int tilesSizeY() {
        return this.tiles.length;
    }

    public Optional<Chunk> getChunkToTop() {
        return this.world.getChunk(new ChunkCoord(this.coord.x(), this.coord.y() - 1));
    }

    public Optional<Chunk> getChunkToTopRight() {
        return this.world.getChunk(new ChunkCoord(this.coord.x() + 1, this.coord.y() - 1));
    }

    public Optional<Chunk> getChunkToRight() {
        return this.world.getChunk(new ChunkCoord(this.coord.x() + 1, this.coord.y()));
    }

    public Optional<Chunk> getChunkToBottomRight() {
        return this.world.getChunk(new ChunkCoord(this.coord.x() + 1, this.coord.y() + 1));
    }

    public Optional<Chunk> getChunkToBottom() {
        return this.world.getChunk(new ChunkCoord(this.coord.x(), this.coord.y() + 1));
    }

    public Optional<Chunk> getChunkToBottomLeft() {
        return this.world.getChunk(new ChunkCoord(this.coord.x() - 1, this.coord.y() + 1));
    }

    public Optional<Chunk> getChunkToLeft() {
        return this.world.getChunk(new ChunkCoord(this.coord.x() - 1, this.coord.y()));
    }

    public Optional<Chunk> getChunkToTopLeft() {
        return this.world.getChunk(new ChunkCoord(this.coord.x() - 1, this.coord.y() - 1));
    }

    private Optional<TileType> getTileToTop(int currentX, int currentY) {
        if (currentY < 0 || currentY >= this.tilesSizeY() || currentX < 0 || currentX >= this.tilesSizeX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX;
        int y = currentY - 1;
        if (y < 0) {
            y = this.tilesSizeY();
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
        if (currentY < 0 || currentY >= this.tilesSizeY() || currentX < 0 || currentX >= this.tilesSizeX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX + 1;
        int y = currentY - 1;
        if (y < 0 || x >= this.tilesSizeX()) {
            if (y < 0) {
                y = this.tilesSizeY();
            }
            if (x >= this.tilesSizeX()) {
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
        if (currentY < 0 || currentY >= this.tilesSizeY() || currentX < 0 || currentX >= this.tilesSizeX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX + 1;
        int y = currentY;
        if (x >= this.tilesSizeX()) {
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
        if (currentY < 0 || currentY >= this.tilesSizeY() || currentX < 0 || currentX >= this.tilesSizeX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX + 1;
        int y = currentY + 1;
        if (y >= this.tilesSizeY() || x >= this.tilesSizeX()) {
            if (y >= this.tilesSizeY()) {
                y = -1;
            }
            if (x >= this.tilesSizeX()) {
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
        if (currentY < 0 || currentY >= this.tilesSizeY() || currentX < 0 || currentX >= this.tilesSizeX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX;
        int y = currentY + 1;
        if (y >= this.tilesSizeY()) {
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
        if (currentY < 0 || currentY >= this.tilesSizeY() || currentX < 0 || currentX >= this.tilesSizeX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX - 1;
        int y = currentY + 1;
        if (y >= this.tilesSizeY() || x < 0) {
            if (y >= this.tilesSizeY()) {
                y = -1;
            }
            if (x < 0) {
                x = this.tilesSizeX();
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
        if (currentY < 0 || currentY >= this.tilesSizeY() || currentX < 0 || currentX >= this.tilesSizeX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX - 1;
        int y = currentY;
        if (x < 0) {
            x = this.tilesSizeX();
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
        if (currentY < 0 || currentY >= this.tilesSizeY() || currentX < 0 || currentX >= this.tilesSizeX()) {
            throw new InvalidParameterException("Current tile of chunk is not inside boundaries");
        }

        int x = currentX - 1;
        int y = currentY - 1;
        if (y < 0 || x < 0) {
            if (y < 0) {
                y = this.tilesSizeY();
            }
            if (x < 0) {
                x = this.tilesSizeX();
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
