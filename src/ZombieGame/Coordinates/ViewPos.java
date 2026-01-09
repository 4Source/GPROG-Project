package ZombieGame.Coordinates;

import ZombieGame.World;

/**
 * Represents a position in the view or screen coordinates.
 */
public record ViewPos(int x, int y) {

    public ViewPos() {
        this(0, 0);
    }

    public ViewPos add(int x, int y) {
        return new ViewPos(this.x + x, this.y + y);
    }

    public ViewPos add(ViewPos pos) {
        return new ViewPos(this.x + pos.x, this.y + pos.y);
    }

    public ViewPos add(Offset offset) {
        return new ViewPos((int) (x + offset.x()), (int) (y + offset.y()));
    }

    public ViewPos sub(int x, int y) {
        return new ViewPos(this.x - x, this.y - y);
    }

    public ViewPos sub(ViewPos pos) {
        return new ViewPos(this.x - pos.x, this.y - pos.y);
    }

    public ViewPos sub(Offset offset) {
        return new ViewPos((int) (x - offset.x()), (int) (y - offset.y()));
    }

    /**
     * Converts a view position to absolute world coordinates.
     *
     * @param world The world containing the viewport/camera offset.
     * @return Corresponding world position.
     */
    public WorldPos toWorldPos(World world) {
        return new WorldPos(this.x + world.getWorldPartX(), this.y + world.getWorldPartY());
    }
}
