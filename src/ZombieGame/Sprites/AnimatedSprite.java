package ZombieGame.Sprites;

import java.awt.Color;

import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.Rotation;

public abstract class AnimatedSprite extends Sprite {
    protected final double frameTime;
    protected double lastUpdate;

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param columnIndex Specifies the index of the sprite which should be displayed
     * @param rowIndex Specifies the index of the sprite which should be displayed
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param rotation Rotation around the rotationCenter
     * @param rotationCenter The center to rotate around
     * @param offset A positioning offset which gets added to the position were to draw the sprite
     * @param tint A color to tint he Sprite with
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     */
    public AnimatedSprite(String spritePath, int columnCount, int rowCount, int columnIndex, int rowIndex, double scale, Rotation rotation, Offset rotationCenter, Offset offset, Color tint, double frameTime) {
        super(spritePath, columnCount, rowCount, columnIndex, rowIndex, scale, rotation, rotationCenter, offset, tint);

        this.frameTime = frameTime;
        this.lastUpdate = 0;
    }

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     * @param offset A positioning offset which gets added to the position were to draw the sprite
     */
    public AnimatedSprite(String spritePath, int columnCount, int rowCount, double scale, Offset offset, double frameTime) {
        this(spritePath, columnCount, rowCount, 0, 0, scale, Rotation.ZERO, new Offset(), offset, null, frameTime);
    }

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     */
    public AnimatedSprite(String spritePath, int columnCount, int rowCount, double scale, double frameTime) {
        this(spritePath, columnCount, rowCount, scale, new Offset(), frameTime);
    }

    @Override
    public void update(double deltaTime) {
        this.lastUpdate += deltaTime;
        if (this.lastUpdate > this.frameTime) {
            this.lastUpdate -= this.frameTime;

            this.columnIndex++;
        }
    }

    /**
     * @return {@code true} if the animation is finished
     */
    public abstract boolean isAnimationFinished();
}
