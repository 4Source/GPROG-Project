package ZombieGame.Entities;

import java.util.function.Function;

import ZombieGame.EntityType;
import ZombieGame.Components.UIComponent;
import ZombieGame.Components.ViewPositionComponent;
import ZombieGame.Coordinates.ViewPos;

public abstract class UIElement extends Entity {
    private final UIComponent uiComponent;

    /**
     * @param <U> The type of the component extending a UIComponent to create with factory method
     * @param pos Position on screen
     * @param uiFactory A Factory method to create the component
     */
    public <U extends UIComponent> UIElement(ViewPos pos, Function<Entity, U> uiFactory) {
        super(e -> new ViewPositionComponent(e, pos));
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
