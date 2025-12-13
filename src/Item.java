import java.awt.Color;

public abstract class Item extends Entity {

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param radius The size of the Item
     * @param color The color of the Item
     */
    public Item(double posX, double posY, int radius, Color color) {
        super(posX, posY, radius, color);
    }
}
