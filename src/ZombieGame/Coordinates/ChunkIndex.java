package ZombieGame.Coordinates;

import ZombieGame.Chunk;

/**
 * Represents the integer coordinates of a chunk in the world grid.
 * The origin of the chunk is at (0,0) top-left of chunk.
 */
public record ChunkIndex(int x, int y) {

    public ChunkIndex() {
        this(0, 0);
    }

    public ChunkIndex add(int x, int y) {
        return new ChunkIndex(this.x + x, this.y + y);
    }

    public ChunkIndex add(Offset offset) {
        return new ChunkIndex((int) (x + offset.x()), (int) (y + offset.y()));
    }

    public ChunkIndex sub(int x, int y) {
        return new ChunkIndex(this.x - x, this.y - y);
    }

    public ChunkIndex sub(Offset offset) {
        return new ChunkIndex((int) (x - offset.x()), (int) (y - offset.y()));
    }

    public ChunkIndex ToTop() {
        return this.add(0, -1);
    }

    public ChunkIndex ToTopRight() {
        return this.add(1, -1);
    }

    public ChunkIndex ToRight() {
        return this.add(1, 0);
    }

    public ChunkIndex ToBottomRight() {
        return this.add(1, 1);
    }

    public ChunkIndex ToBottom() {
        return this.add(0, 1);
    }

    public ChunkIndex ToBottomLeft() {
        return this.add(-1, 1);
    }

    public ChunkIndex ToLeft() {
        return this.add(-1, 0);
    }

    public ChunkIndex ToTopLeft() {
        return this.add(-1, -1);
    }

    /**
     * @return The top-left world position of this chunk.
     */
    public WorldPos toWorldPos() {
        return new WorldPos(x * Chunk.getChunkSize(), y * Chunk.getChunkSize());
    }

    /**
     * Converts a position local to this chunk into world coordinates.
     *
     * @param localPos The local position within this chunk.
     * @return World position corresponding to the local offset inside this chunk.
     */
    public WorldPos toWorldPos(ChunkLocalPos localPos) {
        return new WorldPos(this.x * Chunk.getChunkSize() + localPos.x(), this.y * Chunk.getChunkSize() + localPos.y());
    }
}
