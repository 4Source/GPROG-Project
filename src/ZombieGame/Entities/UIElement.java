package ZombieGame.Entities;

import java.util.function.Function;

import ZombieGame.EntityType;
import ZombieGame.Components.UIComponent;

public abstract class UIElement extends Entity {
    private UIComponent uiComponent;

    /**
     * @param <U> The type of the component extending a UIComponent to create with factory method
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param uiFactory A Factory method to create the component
     */
    public <U extends UIComponent> UIElement(double posX, double posY, Function<Entity, U> uiFactory) {
        super(posX, posY);
        this.uiComponent = this.add(uiFactory.apply(this));
    }

    public UIComponent getUIComponent() {
        return this.uiComponent;
    }

    @Override
    public EntityType getType() {
        return EntityType.UI;
    }
}
