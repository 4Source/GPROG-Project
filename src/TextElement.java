import java.awt.Color;
import java.awt.Font;

public abstract class TextElement extends UIElement {

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param color The color of the ui object
     * @param font The font to use to print the text to the screen
     */
    public TextElement(double posX, double posY, Color color, Font font) {
        super(posX, posY, color, font, e -> new TextComponent(e, color, font) {

            @Override
            public String toString() {
                return this.entity.toString();
            }

            @Override
            public void update(double deltaTime) {
            }

        });
    }

    public TextComponent getUIComponent() {
        return (TextComponent) super.getUIComponent();
    }

    /**
     * Returns the string which should be displayed on the screen
     * 
     * @return The string to display
     */
    public abstract String toString();
}
