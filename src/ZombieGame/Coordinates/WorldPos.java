package ZombieGame.Coordinates;

import ZombieGame.Chunk;
import ZombieGame.World;

/**
 * Represents the absolute position of point in the world.
 */
public record WorldPos(double x, double y) {

    public WorldPos() {
        this(0, 0);
    }

    public WorldPos add(double x, double y) {
        return new WorldPos(this.x + x, this.y + y);
    }

    public WorldPos add(WorldPos pos) {
        return new WorldPos(this.x + pos.x, this.y + pos.y);
    }

    public WorldPos add(Offset offset) {
        return new WorldPos(x + offset.x(), y + offset.y());
    }

    public WorldPos sub(double x, double y) {
        return new WorldPos(this.x - x, this.y - y);
    }

    public WorldPos sub(WorldPos pos) {
        return new WorldPos(this.x - pos.x, this.y - pos.y);
    }

    public WorldPos sub(Offset offset) {
        return new WorldPos(x - offset.x(), y - offset.y());
    }

    /**
     * @return The chunk index containing this world position.
     */
    public ChunkIndex toChunkIndex() {
        return new ChunkIndex((int) Math.floor(this.x / Chunk.SIZE), (int) Math.floor(this.y / Chunk.SIZE));
    }

    /**
     * Converts this world position to a view/screen position relative to the camera or viewport.
     *
     * @param world The world containing the current viewport/camera offset.
     * @return The position in view/screen coordinates.
     */
    public ViewPos toViewPos(World world) {
        return new ViewPos((int) (this.x - world.getWorldPartX()), (int) (this.y - world.getWorldPartY()));
    }

    /**
     * @param chunk The chunk this world position belongs to.
     * @return The local position within the given chunk.
     */
    public ChunkLocalPos toLocalPos(ChunkIndex chunk) {
        return new ChunkLocalPos(this.x - chunk.x() * Chunk.SIZE, this.y - chunk.y() * Chunk.SIZE);
    }
}
