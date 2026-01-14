package ZombieGame.Sprites;

import java.awt.Color;
import java.awt.image.BufferedImage;

import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.Rotation;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Systems.Graphic.GraphicSystem;

public abstract class Sprite {
    private BufferedImage sprite;
    private final int width;
    private final int height;
    private final Offset offset;
    private final double scale;
    private Rotation rotation;
    private Offset rotationCenter;
    private Color tint;

    protected final int columnCount;
    protected int columnIndex;
    protected final int rowCount;
    protected int rowIndex;

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
     */
    public Sprite(String spritePath, int columnCount, int rowCount, int columnIndex, int rowIndex, double scale, Rotation rotation, Offset rotationCenter, Offset offset, Color tint) {
        this.sprite = SpriteManager.getSprite(spritePath);
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        this.width = sprite.getWidth() / columnCount;
        this.height = sprite.getHeight() / rowCount;
        this.scale = scale;
        this.rotation = rotation;
        this.rotationCenter = rotationCenter;
        this.offset = offset;
        this.tint = tint;
    }

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
     */
    public Sprite(String spritePath, int columnCount, int rowCount, int columnIndex, int rowIndex, double scale, Rotation rotation, Offset rotationCenter, Offset offset) {
        this(spritePath, columnCount, rowCount, columnIndex, rowIndex, scale, rotation, rotationCenter, offset, null);
    }

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param columnIndex Specifies the index of the sprite which should be displayed
     * @param rowIndex Specifies the index of the sprite which should be displayed
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param rotation Rotation around the rotationCenter
     * @param rotationCenter The center to rotate around
     */
    public Sprite(String spritePath, int columnCount, int rowCount, int columnIndex, int rowIndex, double scale, Rotation rotation, Offset rotationCenter) {
        this(spritePath, columnCount, rowCount, columnIndex, rowIndex, scale, rotation, rotationCenter, new Offset());
    }

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param columnIndex Specifies the index of the sprite which should be displayed
     * @param rowIndex Specifies the index of the sprite which should be displayed
     * @param scale The Factor about what the Sprite should be scaled to display it
     * @param rotation Rotation around the rotationCenter
     */
    public Sprite(String spritePath, int columnCount, int rowCount, int columnIndex, int rowIndex, double scale, Rotation rotation) {
        this(spritePath, columnCount, rowCount, columnIndex, rowIndex, scale, rotation, new Offset());
    }

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     */
    public Sprite(String spritePath, int columnCount, int rowCount, double scale) {
        this(spritePath, columnCount, rowCount, 0, 0, scale, new Rotation());
    }

    /**
     * Sprite with missing texture
     */
    public Sprite() {
        this("", 1, 1, 1);
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
        GraphicSystem.getInstance().drawSprite(this.sprite, pos.add(offset), this.columnIndex, this.rowIndex, this.scale, this.rotation, this.rotationCenter, this.width, this.height, tint);
    }

    public BufferedImage getSprite() {
        return this.sprite;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public int getColumnIndex() {
        return this.columnIndex;
    }

    public void setColumnIndex(int index) {
        this.columnIndex = index;
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getRowIndex() {
        return this.rowIndex;
    }

    public void setRowIndex(int index) {
        this.rowIndex = index;
    }

    public int getTileHeight() {
        return this.height;
    }

    public double getDrawHeight() {
        return this.scale * this.height;
    }

    public int getTileWidth() {
        return this.width;
    }

    public double getDrawWidth() {
        return this.scale * this.width;
    }

    public void setTint(Color color) {
        this.tint = color;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public void setRotationCenter(Offset rotationCenter) {
        this.rotationCenter = rotationCenter;
    }

}