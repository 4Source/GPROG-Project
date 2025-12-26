import java.awt.Color;

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
    CircleComponent(Entity entity, int radius, Color color) {
        super(entity);
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void draw() {
        int x = (int) (this.getEntity().posX - Entity.world.worldPartX);
        int y = (int) (this.getEntity().posY - Entity.world.worldPartY);
        int d = (int) (this.radius * 2);

        GraphicSystem.getInstance().drawFillOval(x, y, d, d, new DrawStyle().color(this.color));
        GraphicSystem.getInstance().drawOval(x, y, d, d, new DrawStyle().color(Color.DARK_GRAY));
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.GAME;
    }
}
