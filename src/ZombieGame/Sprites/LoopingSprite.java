package ZombieGame.Sprites;

import ZombieGame.Coordinates.Offset;

public class LoopingSprite extends AnimatedSprite {

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
        super(spritePath, columnCount, rowCount, scale, frameTime, offset);
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
