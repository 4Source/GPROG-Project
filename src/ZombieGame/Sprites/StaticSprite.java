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
     * @param offsetX A positioning offset in x which gets added to the position were to draw the sprite
     * @param offsetY A positioning offset in y which gets added to the position were to draw the sprite
     */
    public StaticSprite(String spritePath, int columnCount, int rowCount, double scale, int columnIndex, int rowIndex, int offsetX, int offsetY) {
        super(spritePath, columnCount, rowCount, scale, offsetX, offsetY);

        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
    }

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
        this(spritePath, columnCount, rowCount, scale, columnIndex, rowIndex, 0, 0);
    }

    @Override
    public void update(double deltaTime) {
    }
}
