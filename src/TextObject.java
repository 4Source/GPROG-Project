import java.awt.*;

public abstract class TextObject extends UIObject {
    private Font font;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param color The color of the ui object
     * @param font The font to use to print the text to the screen
     */
    public TextObject(double posX, double posY, Color color, Font font) {
        super(posX, posY, color);
        this.font = font;
    }

    public void draw() {
        GraphicSystem.getInstance().drawString(this.toString(), (int) this.posX, (int) this.posY, new DrawStyle().color(this.color).font(this.font));
    }

    /**
     * Returns the string which should be displayed on the screen
     * 
     * @return The string to display
     */
    public abstract String toString();
}
