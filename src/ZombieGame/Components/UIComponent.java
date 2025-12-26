package ZombieGame.Components;

import java.awt.Color;

import ZombieGame.Entities.Entity;

public abstract class UIComponent extends VisualComponent {
    protected Color color;

    /**
     * @param entity The entity to which the components belongs to
     * @param color The color of the ui object
     */
    public UIComponent(Entity entity, Color color) {
        super(entity);
        this.color = color;
    }
}
