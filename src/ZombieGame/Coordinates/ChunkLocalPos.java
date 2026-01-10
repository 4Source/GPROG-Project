package ZombieGame.Coordinates;

import ZombieGame.Chunk;

/**
 * Represents a position inside a chunk.
 * Coordinates range from 0 to Chunk.SIZE (exclusive) in both axes.
 */
public record ChunkLocalPos(double x, double y) {

    public ChunkLocalPos() {
        this(0, 0);
    }

    public ChunkLocalPos add(int x, int y) {
        return new ChunkLocalPos(this.x + x, this.y + y);
    }

    public ChunkLocalPos add(ChunkLocalPos pos) {
        return new ChunkLocalPos(this.x + pos.x, this.y + pos.y);
    }

    public ChunkLocalPos add(Offset offset) {
        return new ChunkLocalPos((int) (x + offset.x()), (int) (y + offset.y()));
    }

    public ChunkLocalPos sub(int x, int y) {
        return new ChunkLocalPos(this.x - x, this.y - y);
    }

    public ChunkLocalPos sub(ChunkLocalPos pos) {
        return new ChunkLocalPos(this.x - pos.x, this.y - pos.y);
    }

    public ChunkLocalPos sub(Offset offset) {
        return new ChunkLocalPos((int) (x - offset.x()), (int) (y - offset.y()));
    }

    public boolean isInsideChunk() {
        return x >= 0 && x < Chunk.SIZE && y >= 0 && y < Chunk.SIZE;
    }

    /**
     * Converts this local position to a world position given the chunk it belongs to.
     *
     * @param chunk The chunk containing this local position.
     * @return Absolute world position.
     */
    public WorldPos toWorldPos(ChunkIndex chunk) {
        return new WorldPos(chunk.x() * Chunk.getChunkSize() + this.x, chunk.y() * Chunk.getChunkSize() + this.y);
    }
}
