package ZombieGame.Sprites;

public class OneShotSprite extends AnimatedSprite {
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
        super(spritePath, columnCount, rowCount, scale, frameTime);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if (this.columnIndex >= this.columnCount) {
            this.columnIndex = this.columnCount - 1;
        }
    }
}
