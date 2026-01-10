package ZombieGame;

import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;

public final class Viewport {
    private static int screenWidth = 0, screenHeight = 0;
    public static final int SCROLL_BOUNDS = 300;

    private WorldPos worldPart;

    public Viewport() {
        this.worldPart = new WorldPos(-screenWidth / 2, -screenHeight / 2);
    }

    public Viewport(WorldPos pos) {
        this.worldPart = pos;
    }

    public WorldPos getWorldPart() {
        return this.worldPart;
    }

    /**
     * @param offset Offset added to the current world part
     */
    public void move(Offset offset) {
        this.worldPart = this.worldPart.add(offset);
    }

    /**
     * @return The center of the screen in view-space coordinates.
     */
    public static ViewPos getCenter() {
        return new ViewPos(Viewport.screenWidth / 2, Viewport.screenHeight / 2);
    }

    /**
     * @return The top-left corner of the screen in view-space coordinates.
     */
    public static ViewPos getTopLeft() {
        return new ViewPos();
    }

    /**
     * @return The top-right corner of the screen in view-space coordinates.
     */
    public static ViewPos getTopRight() {
        return new ViewPos(Viewport.screenWidth, 0);
    }

    /**
     * @return The bottom-right corner of the screen in view-space coordinates.
     */
    public static ViewPos getBottomRight() {
        return new ViewPos(Viewport.screenWidth, Viewport.screenHeight);
    }

    /**
     * @return The bottom-left corner of the screen in view-space coordinates.
     */
    public static ViewPos getBottomLeft() {
        return new ViewPos(0, Viewport.screenHeight);
    }

    /**
     * @return A scale factor relative to a 1920×1080 reference resolution.
     *         (1.0, 1.0) means the screen is exactly 1920×1080.
     *         Values >1 mean larger, values <1 mean smaller.
     */
    public static Offset getScreenScale() {
        return new Offset(Viewport.screenWidth / 1920.0, Viewport.screenHeight / 1080.0);
    }

    public static int getScreenWidth() {
        return Viewport.screenWidth;
    }

    public static void setScreenWidth(int screenWidth) {
        Viewport.screenWidth = screenWidth;
    }

    public static int getScreenHeight() {
        return Viewport.screenHeight;
    }

    public static void setScreenHeight(int screenHeight) {
        Viewport.screenHeight = screenHeight;
    }
}
