package ZombieGame.Sprites;

import ZombieGame.Coordinates.Offset;

public abstract class AnimatedSprite extends Sprite {
    protected final double frameTime;
    protected double lastUpdate;

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     * @param offset A positioning offset which gets added to the position were to draw the sprite
     */
    public AnimatedSprite(String spritePath, int columnCount, int rowCount, double scale, double frameTime, Offset offset) {
        super(spritePath, columnCount, rowCount, scale, offset);

        this.frameTime = frameTime;
        this.lastUpdate = 0;
        this.columnIndex = 0;
    }

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     */
    public AnimatedSprite(String spritePath, int columnCount, int rowCount, double scale, double frameTime) {
        this(spritePath, columnCount, rowCount, scale, frameTime, new Offset());
    }

    @Override
    public void update(double deltaTime) {
        this.lastUpdate += deltaTime;
        if (this.lastUpdate > this.frameTime) {
            this.lastUpdate -= this.frameTime;

            this.columnIndex++;
        }
    }
}
