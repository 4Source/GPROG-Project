package ZombieGame.Coordinates;

import ZombieGame.Viewport;
import ZombieGame.World.World;

/**
 * Represents a position in the view or screen coordinates.
 */
public record ViewPos(int x, int y) {

    public ViewPos() {
        this(0, 0);
    }

    /**
     * @return Returns a new pos where the summands are added to it
     */
    public ViewPos add(int summandX, int summandY) {
        return new ViewPos(this.x + summandX, this.y + summandY);
    }

    /**
     * @return Returns a new pos where the summand is added to it
     */
    public ViewPos add(ViewPos summand) {
        return new ViewPos(this.x + summand.x, this.y + summand.y);
    }

    /**
     * @return Returns a new pos where the offset is added to it
     */
    public ViewPos add(Offset offset) {
        return new ViewPos((int) (x + offset.x()), (int) (y + offset.y()));
    }

    /**
     * @return Returns a new pos where the subtrahends are subtracted from this pos
     */
    public ViewPos sub(int subtrahendX, int subtrahendY) {
        return new ViewPos(this.x - subtrahendX, this.y - subtrahendY);
    }

    /**
     * @return Returns a new pos where the subtrahend is subtracted from this pos
     */
    public ViewPos sub(ViewPos subtrahend) {
        return new ViewPos(this.x - subtrahend.x, this.y - subtrahend.y);
    }

    /**
     * @return Returns a new pos where the offset is subtracted from this pos
     */
    public ViewPos sub(Offset offset) {
        return new ViewPos((int) (x - offset.x()), (int) (y - offset.y()));
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public ViewPos mul(int factor) {
        return new ViewPos(this.x * factor, this.y * factor);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public ViewPos mul(int factorX, int factorY) {
        return new ViewPos(this.x * factorX, this.y * factorY);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public ViewPos mul(Offset factor) {
        return new ViewPos((int) (this.x * factor.x()), (int) (this.y * factor.y()));
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public ViewPos div(int divisor) {
        return new ViewPos(this.x / divisor, this.y / divisor);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public ViewPos div(int divisorX, int divisorY) {
        return new ViewPos(this.x / divisorX, this.y / divisorY);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public ViewPos div(Offset divisor) {
        return new ViewPos((int) (this.x / divisor.x()), (int) (this.y / divisor.y()));
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Converts a view position to absolute world coordinates.
     *
     * @param world The world containing the viewport/camera offset.
     * @return Corresponding world position.
     */
    public WorldPos toWorldPos(World world) {
        return new WorldPos(this.x + world.getViewport().getWorldPart().x(), this.y + world.getViewport().getWorldPart().y());
    }

    public boolean isInsideViewport() {
        return x >= 0 && x < Viewport.getScreenWidth() && y >= 0 && y < Viewport.getScreenHeight();
    }

    public boolean isInsideScrollBounds() {
        return x >= Viewport.SCROLL_BOUNDS && x < Viewport.getScreenWidth() - Viewport.SCROLL_BOUNDS && y >= Viewport.SCROLL_BOUNDS && y < Viewport.getScreenHeight() - Viewport.SCROLL_BOUNDS;
    }

    public boolean isInsideScrollBoundMinX() {
        return x >= Viewport.SCROLL_BOUNDS;
    }

    public boolean isInsideScrollBoundMinY() {
        return y >= Viewport.SCROLL_BOUNDS;
    }

    public boolean isInsideScrollBoundMaxX() {
        return x < Viewport.getScreenWidth() - Viewport.SCROLL_BOUNDS;
    }

    public boolean isInsideScrollBoundMaxY() {
        return y < Viewport.getScreenHeight() - Viewport.SCROLL_BOUNDS;
    }

    @Override
    public final String toString() {
        return String.format("x: %d y: %d", this.x, this.y);
    }
}
