package ZombieGame.Coordinates;

import ZombieGame.World.Chunk;

/**
 * Represents a position inside a chunk.
 * Coordinates range from 0 to Chunk.SIZE (exclusive) in both axes.
 */
public record ChunkLocalPos(double x, double y) {

    public ChunkLocalPos() {
        this(0, 0);
    }

    /**
     * @return Returns a new pos where the summands are added to it
     */
    public ChunkLocalPos add(double summandX, double summandY) {
        return new ChunkLocalPos(this.x + summandX, this.y + summandY);
    }

    /**
     * @return Returns a new pos where the summand is added to it
     */
    public ChunkLocalPos add(ChunkLocalPos summand) {
        return new ChunkLocalPos(this.x + summand.x, this.y + summand.y);
    }

    /**
     * @return Returns a new pos where the offset is added to it
     */
    public ChunkLocalPos add(Offset offset) {
        return new ChunkLocalPos(x + offset.x(), y + offset.y());
    }

    /**
     * @return Returns a new pos where the subtrahends are subtracted from this pos
     */
    public ChunkLocalPos sub(double subtrahendX, double subtrahendY) {
        return new ChunkLocalPos(this.x - subtrahendX, this.y - subtrahendY);
    }

    /**
     * @return Returns a new pos where the subtrahend is subtracted from this pos
     */
    public ChunkLocalPos sub(ChunkLocalPos subtrahend) {
        return new ChunkLocalPos(this.x - subtrahend.x, this.y - subtrahend.y);
    }

    /**
     * @return Returns a new pos where the offset is subtracted from this pos
     */
    public ChunkLocalPos sub(Offset offset) {
        return new ChunkLocalPos(x - offset.x(), y - offset.y());
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public ChunkLocalPos mul(double factor) {
        return new ChunkLocalPos(this.x * factor, this.y * factor);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public ChunkLocalPos mul(double factorX, double factorY) {
        return new ChunkLocalPos(this.x * factorX, this.y * factorY);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public ChunkLocalPos mul(Offset factor) {
        return new ChunkLocalPos(this.x * factor.x(), this.y * factor.y());
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public ChunkLocalPos div(double divisor) {
        return new ChunkLocalPos(this.x / divisor, this.y / divisor);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public ChunkLocalPos div(double divisorX, double divisorY) {
        return new ChunkLocalPos(this.x / divisorX, this.y / divisorY);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public ChunkLocalPos div(Offset divisor) {
        return new ChunkLocalPos(this.x / divisor.x(), this.y / divisor.y());
    }

    /**
     * @param exponent The exponent (for exponents of 2 prefer {@link #pow2()})
     * @return Returns a new pos where each part is separately raised to the power of exponent
     */
    public ChunkLocalPos pow(double exponent) {
        return new ChunkLocalPos(Math.pow(this.x, exponent), Math.pow(this.y, exponent));
    }

    /**
     * @return Returns a new pos where each part is separately raised to the power of 2
     */
    public ChunkLocalPos pow2() {
        return new ChunkLocalPos(this.x * this.x, this.y * this.y);
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
    public ChunkLocalPos abs() {
        return new ChunkLocalPos(Math.abs(this.x), Math.abs(this.y));
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
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

    @Override
    public final String toString() {
        return String.format("x: %.2f y: %.2f", this.x, this.y);
    }
}
