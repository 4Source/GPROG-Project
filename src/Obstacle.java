import java.util.function.Function;

public abstract class Obstacle extends Entity {
    private VisualComponent visualComponent;
    private PhysicsComponent physicsComponent;

    /**
     * @param <P> The type of the component extending a PhysicsComponent to create with factory method
     * @param <V> The type of the component extending a VisualComponent to create with factory method
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param physicsFactory A Factory method to create the component
     * @param visualFactory A Factory method to create the component
     */
    public <P extends PhysicsComponent, V extends VisualComponent> Obstacle(double posX, double posY, Function<Entity, P> physicsFactory, Function<Entity, V> visualFactory) {
        super(posX, posY);
        this.visualComponent = this.add(visualFactory.apply(this));
        this.physicsComponent = this.add(physicsFactory.apply(this));
    }

    public VisualComponent getVisualComponent() {
        return this.visualComponent;
    }

    public PhysicsComponent getPhysicsComponent() {
        return this.physicsComponent;
    }
}
