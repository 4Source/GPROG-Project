package ZombieGame.Sprites;

import java.awt.Color;
import java.awt.image.BufferedImage;

import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Systems.Graphic.GraphicSystem;

public abstract class Sprite {
    private BufferedImage sprite;
    private final int width;
    private final int height;
    private final Offset offset;
    private final double scale;
    private Color tint;

    protected final int columnCount;
    protected int columnIndex;
    protected final int rowCount;
    protected int rowIndex;

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param offsetX A positioning offset in x which gets added to the position were to draw the sprite
     * @param offsetY A positioning offset in y which gets added to the position were to draw the sprite
     * @param tint A color to tint he Sprite with
     */
    public Sprite(String spritePath, int columnCount, int rowCount, double scale, Offset offset, Color tint) {
        this.sprite = SpriteManager.getSprite(spritePath);
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        this.width = sprite.getWidth() / columnCount;
        this.height = sprite.getHeight() / rowCount;
        this.scale = scale;
        this.columnIndex = 0;
        this.rowIndex = 0;
        this.offset = offset;
        this.tint = tint;
    }

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param offsetX A positioning offset in x which gets added to the position were to draw the sprite
     * @param offsetY A positioning offset in y which gets added to the position were to draw the sprite
     */
    public Sprite(String spritePath, int columnCount, int rowCount, double scale, Offset offset) {
        this(spritePath, columnCount, rowCount, scale, offset, null);
    }

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     */
    public Sprite(String spritePath, int columnCount, int rowCount, double scale) {
        this(spritePath, columnCount, rowCount, scale, new Offset());
    }

    /**
     * Update the sprite using delta time to get constant change with varying fps
     * 
     * @param deltaTime The time since last frame in seconds
     */
    public abstract void update(double deltaTime);

    /**
     * Draw the sprite in the graphics system.
     */
    public void draw(ViewPos pos) {
        GraphicSystem.getInstance().drawSprite(this.sprite, pos.add(offset), this.columnIndex, this.rowIndex, this.scale, this.width, this.height, tint);
    }

    public void setColumnIndex(int index) {
        this.columnIndex = index;
    }

    public void setRowIndex(int index) {
        this.rowIndex = index;
    }

    public double getDrawHeight() {
        return this.scale * this.height;
    }

    public double getDrawWidth() {
        return this.scale * this.width;
    }

    public void setTint(Color color) {
        this.tint = color;
    }
}
