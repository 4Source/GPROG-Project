package ZombieGame.Entities;

import java.util.function.Function;

import ZombieGame.EntityType;
import ZombieGame.Components.ViewPositionComponent;
import ZombieGame.Components.VisualComponent;
import ZombieGame.Coordinates.ViewPos;

public abstract class UIElement extends Entity {
    private final VisualComponent uiComponent;

    /**
     * @param <U> The type of the component extending a UIComponent to create with factory method
     * @param pos Position on screen
     * @param uiFactory A Factory method to create the component
     */
    public <U extends VisualComponent> UIElement(ViewPos pos, Function<Entity, U> uiFactory) {
        super(e -> new ViewPositionComponent(e, pos));
        this.uiComponent = this.add(uiFactory.apply(this));
    }

    public VisualComponent getUIComponent() {
        return this.uiComponent;
    }

    @Override
    public EntityType getType() {
        return EntityType.UI;
    }
}
