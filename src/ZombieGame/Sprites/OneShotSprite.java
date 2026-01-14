package ZombieGame.Sprites;

import java.awt.Color;

import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.Rotation;

public class OneShotSprite extends AnimatedSprite {
    public final Runnable onAnimationEnd;
    private boolean animationFinished;

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
    public OneShotSprite(String spritePath, int columnCount, int rowCount, int columnIndex, int rowIndex, double scale, Rotation rotation, Offset rotationCenter, Offset offset, Color tint, double frameTime, Runnable onAnimationEnd) {
        super(spritePath, columnCount, rowCount, columnIndex, rowIndex, scale, rotation, rotationCenter, offset, tint, frameTime);

        this.animationFinished = false;
        this.onAnimationEnd = onAnimationEnd;
    }

    /**
     * An animated sprite which will go over the sprites once and will than stay at the last
     * 
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     * @param offset A positioning offset which gets added to the position were to draw the sprite
     * @param onAnimationEnd Callback which get called when the animation finished
     */
    public OneShotSprite(String spritePath, int columnCount, int rowCount, double scale, double frameTime, Offset offset, Runnable onAnimationEnd) {
        this(spritePath, columnCount, rowCount, 0, 0, scale, Rotation.ZERO, new Offset(), offset, null, frameTime, onAnimationEnd);
    }

    /**
     * An animated sprite which will go over the sprites once and will than stay at the last
     * 
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     */
    public OneShotSprite(String spritePath, int columnCount, int rowCount, double scale, double frameTime) {
        this(spritePath, columnCount, rowCount, scale, frameTime, new Offset(), () -> {});
    }

    /**
     * An animated sprite which will go over the sprites once and will than stay at the last
     * 
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     * @param offset A positioning offset which gets added to the position were to draw the sprite
     */
    public OneShotSprite(String spritePath, int columnCount, int rowCount, double scale, double frameTime, Offset offset) {
        this(spritePath, columnCount, rowCount, scale, frameTime, offset, () -> {});
    }

    /**
     * An animated sprite which will go over the sprites once and will than stay at the last
     * 
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param frameTime The time how long one sprite should be displayed before switching to the next
     * @param onAnimationEnd Callback which get called when the animation finished
     */
    public OneShotSprite(String spritePath, int columnCount, int rowCount, double scale, double frameTime, Runnable onAnimationEnd) {
        this(spritePath, columnCount, rowCount, scale, frameTime, new Offset(), onAnimationEnd);
    }

    /**
     * Sprite with missing texture
     */
    public OneShotSprite() {
        this("", 1, 1, 1, 0);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if (this.columnIndex >= this.columnCount) {
            this.columnIndex = this.columnCount - 1;
            if (!this.animationFinished) {
                onAnimationEnd.run();
            }
            this.animationFinished = true;
        }
    }

    @Override
    public boolean isAnimationFinished() {
        return this.animationFinished;
    }

    /**
     * Resets the animation to start
     */
    public void resetAnimation() {
        this.columnIndex = 0;
        this.animationFinished = false;
    }
}
