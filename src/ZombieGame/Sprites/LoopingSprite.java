package ZombieGame.Sprites;

import java.awt.Color;

import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.Rotation;

public class LoopingSprite extends AnimatedSprite {

    /**
     * An animated sprite which will loop over the sprites and start from first sprite if come to the end.
     * 
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
    public LoopingSprite(String spritePath, int columnCount, int rowCount, int columnIndex, int rowIndex, double scale, Rotation rotation, Offset rotationCenter, Offset offset, Color tint, double frameTime) {
        super(spritePath, columnCount, rowCount, columnIndex, rowIndex, scale, rotation, rotationCenter, offset, tint, frameTime);
    }

    /**
     * An animated sprite which will loop over the sprites and start from first sprite if come to the end.
     *
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param rotation Rotation around the rotationCenter
     * @param rotationCenter The center to rotate around
     * @param offset A positioning offset which gets added to the position were to draw the sprite
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     */
    public LoopingSprite(String spritePath, int columnCount, int rowCount, double scale, Rotation rotation, Offset rotationCenter, double frameTime, Offset offset) {
        this(spritePath, columnCount, rowCount, 0, 0, scale, rotation, rotationCenter, offset, null, frameTime);
    }

    /**
     * An animated sprite which will loop over the sprites and start from first sprite if come to the end.
     * 
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     * @param offset A positioning offset which gets added to the position were to draw the sprite
     */
    public LoopingSprite(String spritePath, int columnCount, int rowCount, double scale, double frameTime, Offset offset) {
        this(spritePath, columnCount, rowCount, 0, 0, scale, Rotation.ZERO, new Offset(), offset, null, frameTime);
    }

    /**
     * An animated sprite which will loop over the sprites and start from first sprite if come to the end.
     * 
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     */
    public LoopingSprite(String spritePath, int columnCount, int rowCount, double scale, double frameTime) {
        this(spritePath, columnCount, rowCount, scale, frameTime, new Offset());
    }

    /**
     * Sprite with missing texture
     */
    public LoopingSprite() {
        this("", 1, 1, 1, 0);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if (this.columnIndex >= this.columnCount) {
            this.columnIndex = 0;
        }
    }

    /**
     * @return always {@code true} because animation never finish for looping
     */
    @Override
    public boolean isAnimationFinished() {
        return true;
    }
}
