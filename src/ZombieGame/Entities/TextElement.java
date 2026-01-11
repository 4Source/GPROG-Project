package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;

import ZombieGame.Components.TextComponent;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Systems.Graphic.GraphicLayer;

public abstract class TextElement extends UIElement {
    /**
     * @param pos The position in the world
     * @param color The color of the ui object
     * @param font The font to use to print the text to the screen
     */
    public TextElement(ViewPos pos, Color color, Font font) {
        super(pos, e -> new TextComponent((TextElement) e, color, font) {

            @Override
            public String toString() {
                return this.getEntity().toString();
            }

            @Override
            public void update(double deltaTime) {
            }

            @Override
            public GraphicLayer getLayer() {
                return GraphicLayer.UI;
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
