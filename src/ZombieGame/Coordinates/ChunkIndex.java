package ZombieGame.Coordinates;

import java.awt.BasicStroke;
import java.awt.Color;

import ZombieGame.Game;
import ZombieGame.Capabilities.Drawable;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicLayer;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.World.Chunk;
import ZombieGame.World.World;

/**
 * Represents the integer coordinates of a chunk in the world grid.
 * The origin of the chunk is at (0,0) top-left of chunk.
 */
public record ChunkIndex(int x, int y) implements Drawable {
    private static boolean debugBorders = false;

    public ChunkIndex() {
        this(0, 0);
    }

    /**
     * @return Returns a new pos where the summands are added to it
     */
    public ChunkIndex add(int summandX, int summandY) {
        return new ChunkIndex(this.x + summandX, this.y + summandY);
    }

    /**
     * @return Returns a new pos where the offset is added to it
     */
    public ChunkIndex add(Offset offset) {
        return new ChunkIndex((int) (x + offset.x()), (int) (y + offset.y()));
    }

    /**
     * @return Returns a new pos where the subtrahends are subtracted from this pos
     */
    public ChunkIndex sub(int subtrahendX, int subtrahendY) {
        return new ChunkIndex(this.x - subtrahendX, this.y - subtrahendY);
    }

    /**
     * @return Returns a new pos where the offset is subtracted from this pos
     */
    public ChunkIndex sub(Offset offset) {
        return new ChunkIndex((int) (x - offset.x()), (int) (y - offset.y()));
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public ChunkIndex mul(int factor) {
        return new ChunkIndex(this.x * factor, this.y * factor);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public ChunkIndex mul(int factorX, int factorY) {
        return new ChunkIndex(this.x * factorX, this.y * factorY);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public ChunkIndex div(int divisor) {
        return new ChunkIndex(this.x / divisor, this.y / divisor);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public ChunkIndex div(int divisorX, int divisorY) {
        return new ChunkIndex(this.x / divisorX, this.y / divisorY);
    }

    public ChunkIndex atTop() {
        return this.add(0, -1);
    }

    public ChunkIndex atTopRight() {
        return this.add(1, -1);
    }

    public ChunkIndex atRight() {
        return this.add(1, 0);
    }

    public ChunkIndex atBottomRight() {
        return this.add(1, 1);
    }

    public ChunkIndex atBottom() {
        return this.add(0, 1);
    }

    public ChunkIndex atBottomLeft() {
        return this.add(-1, 1);
    }

    public ChunkIndex atLeft() {
        return this.add(-1, 0);
    }

    public ChunkIndex atTopLeft() {
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

    @Override
    public final String toString() {
        return String.format("x: %d y: %d", this.x, this.y);
    }

    @Override
    public void draw() {
        World world = Game.world;
        ViewPos viewPos = this.toWorldPos().toViewPos(world);

        if (debugBorders) {
            DrawStyle style = new DrawStyle();

            if (world.isChunkQueuedForGeneration(this)) {
                float[] dashPattern = { 2.0f, 8.0f };
                style.color(Color.YELLOW).stroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
            }

            if (world.isChunkLoaded(this)) {
                style.color(Color.GREEN);
            } else if (world.isChunkGenerated(this)) {
                style.color(Color.BLUE);
            }

            GraphicSystem.getInstance().drawRect(viewPos.add((int) (Chunk.getChunkSize() / 2.0), (int) (Chunk.getChunkSize() / 2.0)), (int) Chunk.getChunkSize(), (int) Chunk.getChunkSize(), style);
        }
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.UI;
    }

    @Override
    public int getDepth() {
        return 0;
    }
}
