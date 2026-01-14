package ZombieGame.Sprites;

import java.awt.Color;

import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.Rotation;

public class StaticSprite extends Sprite {

    /**
     * A sprite which will not change
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
     */
    public StaticSprite(String spritePath, int columnCount, int rowCount, int columnIndex, int rowIndex, double scale, Rotation rotation, Offset rotationCenter, Offset offset, Color tint) {
        super(spritePath, columnCount, rowCount, columnIndex, rowIndex, scale, rotation, rotationCenter, offset, tint);
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
     * @param offset A positioning offset which gets added to the position were to draw the sprite
     */
    public StaticSprite(String spritePath, int columnCount, int rowCount, double scale, int columnIndex, int rowIndex, Offset offset) {
        this(spritePath, columnCount, rowCount, columnIndex, rowIndex, scale, Rotation.ZERO, new Offset(), offset, null);
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
        this(spritePath, columnCount, rowCount, scale, columnIndex, rowIndex, new Offset());
    }

    /**
     * Sprite with missing texture
     */
    public StaticSprite() {
        this("", 1, 1, 1, 0, 0);
    }

    @Override
    public void update(double deltaTime) {
    }
}
