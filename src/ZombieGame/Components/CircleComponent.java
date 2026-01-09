package ZombieGame.Components;

import java.awt.Color;

import ZombieGame.DrawStyle;
import ZombieGame.GraphicLayer;
import ZombieGame.GraphicSystem;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;

public class CircleComponent extends VisualComponent {
    protected int radius;
    protected Color color;

    /**
     * A visual component which is represented by a circle
     * 
     * @param entity The entity to which the components belongs to
     * @param radius The size of the entity
     * @param color The color of the entity
     */
    public CircleComponent(Entity entity, int radius, Color color) {
        super(entity);
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void draw() {
        ViewPos view = this.getEntity().getPositionComponent().getViewPos();
        int d = (int) (this.radius * 2);

        GraphicSystem.getInstance().drawFillOval(view, d, d, new DrawStyle().color(this.color));
        GraphicSystem.getInstance().drawOval(view, d, d, new DrawStyle().color(Color.DARK_GRAY));
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.GAME;
    }
}
