import java.awt.Color;

public abstract class UIObject extends GameObject {
    protected Color color;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param color The color of the ui object
     */
    public UIObject(double posX, double posY, Color color) {
        super(posX, posY);
        this.color = color;
    }
}
