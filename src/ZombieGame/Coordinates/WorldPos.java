package ZombieGame.Coordinates;

import ZombieGame.World.Chunk;
import ZombieGame.World.World;

/**
 * Represents the absolute position of point in the world.
 */
public record WorldPos(double x, double y) {

    public WorldPos() {
        this(0, 0);
    }

    /**
     * @return Returns a new pos where the summands are added to it
     */
    public WorldPos add(double summandX, double summandY) {
        return new WorldPos(this.x + summandX, this.y + summandY);
    }

    /**
     * @return Returns a new pos where the summand is added to it
     */
    public WorldPos add(WorldPos summand) {
        return new WorldPos(this.x + summand.x, this.y + summand.y);
    }

    /**
     * @return Returns a new pos where the offset is added to it
     */
    public WorldPos add(Offset offset) {
        return new WorldPos(x + offset.x(), y + offset.y());
    }

    /**
     * @return Returns a new pos where the subtrahends are subtracted from this pos
     */
    public WorldPos sub(double subtrahendX, double subtrahendY) {
        return new WorldPos(this.x - subtrahendX, this.y - subtrahendY);
    }

    /**
     * @return Returns a new pos where the subtrahend is subtracted from this pos
     */
    public WorldPos sub(WorldPos subtrahend) {
        return new WorldPos(this.x - subtrahend.x, this.y - subtrahend.y);
    }

    /**
     * @return Returns a new pos where the offset is subtracted from this pos
     */
    public WorldPos sub(Offset offset) {
        return new WorldPos(x - offset.x(), y - offset.y());
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public WorldPos mul(double factor) {
        return new WorldPos(this.x * factor, this.y * factor);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public WorldPos mul(double factorX, double factorY) {
        return new WorldPos(this.x * factorX, this.y * factorY);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public WorldPos mul(Offset factor) {
        return new WorldPos(this.x * factor.x(), this.y * factor.y());
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public WorldPos div(double divisor) {
        return new WorldPos(this.x / divisor, this.y / divisor);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public WorldPos div(double divisorX, double divisorY) {
        return new WorldPos(this.x / divisorX, this.y / divisorY);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public WorldPos div(Offset divisor) {
        return new WorldPos(this.x / divisor.x(), this.y / divisor.y());
    }

    /**
     * @param exponent The exponent (for exponents of 2 prefer {@link #pow2()})
     * @return Returns a new pos where each part is separately raised to the power of exponent
     */
    public WorldPos pow(double exponent) {
        return new WorldPos(Math.pow(this.x, exponent), Math.pow(this.y, exponent));
    }

    /**
     * @return Returns a new pos where each part is separately raised to the power of 2
     */
    public WorldPos pow2() {
        return new WorldPos(this.x * this.x, this.y * this.y);
    }

    /**
     * @param exponent The exponent (for exponents of 2 prefer {@link #xPow2()})
     * @return Returns the x part raised to the power of exponent
     */
    public double xPow(double exponent) {
        return Math.pow(this.x, exponent);
    }

    /**
     * @return Returns the x part raised to the power of 2
     */
    public double xPow2() {
        return this.x * this.x;
    }

    /**
     * @param exponent The exponent (for exponents of 2 prefer {@link #yPow2()})
     * @return Returns the y part raised to the power of exponent
     */
    public double yPow(double exponent) {
        return Math.pow(this.y, exponent);
    }

    /**
     * @return Returns the x part raised to the power of 2
     */
    public double yPow2() {
        return this.y * this.y;
    }

    /**
     * @return Returns the absolute value. If the pos is not negative, the pos is returned. If the pos is negative, the negation of the pos is returned.
     */
    public WorldPos abs() {
        return new WorldPos(Math.abs(this.x), Math.abs(this.y));
    }

    /**
     * @return The chunk index containing this world position.
     */
    public ChunkIndex toChunkIndex() {
        return new ChunkIndex((int) Math.floor(this.x / Chunk.getChunkSize()), (int) Math.floor(this.y / Chunk.getChunkSize()));
    }

    /**
     * Converts this world position to a view/screen position relative to the camera or viewport.
     *
     * @param world The world containing the current viewport/camera offset.
     * @return The position in view/screen coordinates.s
     */
    public ViewPos toViewPos(World world) {
        return new ViewPos((int) (this.x - world.getViewport().getWorldPart().x()), (int) (this.y - world.getViewport().getWorldPart().y()));
    }

    /**
     * @param chunk The chunk this world position belongs to.
     * @return The local position within the given chunk.
     */
    public ChunkLocalPos toLocalPos(ChunkIndex chunk) {
        return new ChunkLocalPos(this.x - chunk.x() * Chunk.getChunkSize(), this.y - chunk.y() * Chunk.getChunkSize());
    }

    @Override
    public final String toString() {
        return String.format("x: %.2f y: %.2f", this.x, this.y);
    }
}
