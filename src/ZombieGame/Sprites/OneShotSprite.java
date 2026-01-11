package ZombieGame.Sprites;

import ZombieGame.Coordinates.Offset;

public class OneShotSprite extends AnimatedSprite {
    public final Runnable onAnimationEnd;
    private boolean animationFinished;

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
        super(spritePath, columnCount, rowCount, scale, frameTime, offset);

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
