package ZombieGame.Sprites;

public class StaticSprite extends Sprite {

    /**
     * A sprite which will not change
     * 
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param columnIndex Specifies the index of the sprite which should be displayed
     * @param rowIndex Specifies the index of the sprite which should be displayed
     */
    public StaticSprite(String spritePath, int columnCount, int rowCount, double scale, int columnIndex, int rowIndex) {
        super(spritePath, columnCount, rowCount, scale);

        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
    }

    @Override
    public void update(double deltaTime) {
    }
}
