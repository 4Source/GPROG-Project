import java.awt.Color;
import java.util.function.Function;

public abstract class Obstacle extends Entity {
    private CircleComponent circleComponent;
    private PhysicsComponent physicsComponent;

    /**
     * @param <T> The type of the component to create with factory method
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param radius The size of the Obstacle
     * @param color The color of the Obstacle
     * @param physicsFactory A Factory method to create the component
     */
    public <T extends PhysicsComponent> Obstacle(double posX, double posY, int radius, Color color, Function<Entity, T> physicsFactory) {
        super(posX, posY);
        this.circleComponent = this.add(new CircleComponent(this, radius, color));
        this.physicsComponent = this.add(physicsFactory.apply(this));
    }

    public CircleComponent getCircleComponent() {
        return this.circleComponent;
    }

    public PhysicsComponent getPhysicsComponent() {
        return this.physicsComponent;
    }
}
