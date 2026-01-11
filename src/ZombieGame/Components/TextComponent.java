package ZombieGame.Components;

import java.awt.*;

import ZombieGame.Entities.TextElement;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicSystem;

public abstract class TextComponent extends UIComponent {
    protected Font font;

    /**
     * @param entity The entity to which the components belongs to
     * @param color The color of the ui object
     * @param font The font to use to print the text to the screen
     */
    public TextComponent(TextElement entity, Color color, Font font) {
        super(entity, color);
        this.font = font;
    }

    public void draw() {
        GraphicSystem.getInstance().drawString(this.toString(), this.getEntity().getPositionComponent().getViewPos(), new DrawStyle().color(this.color).font(this.font));
    }

    /**
     * Returns the string which should be displayed on the screen
     * 
     * @return The string to display
     */
    public abstract String toString();

    @Override
    public TextElement getEntity() {
        return (TextElement) super.getEntity();
    }
}
