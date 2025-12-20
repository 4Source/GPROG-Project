import java.awt.Color;
import java.awt.Font;
import java.util.function.Function;

public abstract class UIElement extends Entity {
    private UIComponent uiComponent;

    /**
     * @param <T> The type of the component to create with factory method
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param color The color of the ui object
     * @param font The font to use to print the text to the screen
     * @param uiFactory A Factory method to create the component
     */
    public <T extends UIComponent> UIElement(double posX, double posY, Color color, Font font, Function<Entity, T> uiFactory) {
        super(posX, posY);
        this.uiComponent = this.add(uiFactory.apply(this));
    }

    public UIComponent getUIComponent() {
        return this.uiComponent;
    }

    @Override
    public EntityType getType() {
        // TODO getType Should maybe return Class type
        return EntityType.UI;
    }
}
