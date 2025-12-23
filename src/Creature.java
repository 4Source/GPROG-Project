import java.util.function.Function;

public abstract class Creature extends Entity {
    private VisualComponent visualComponent;
    private DynamicPhysicsComponent physicsComponent;
    private MovementComponent movementComponent;
    private LifeComponent lifeComponent;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param visualFactory A Factory method to create the component
     * @param hitBox The hit box of the Physics component
     * @param movementFactory A Factory method to create the component
     * @param lifeFactory A Factory method to create the component
     */
    public Creature(double posX, double posY, Function<Entity, VisualComponent> visualFactory, HitBox hitBox, Function<Entity, MovementComponent> movementFactory, Function<Entity, LifeComponent> lifeFactory) {
        super(posX, posY);
        this.visualComponent = this.add(visualFactory.apply(this));
        this.physicsComponent = this.add(new DynamicPhysicsComponent(this, hitBox, collision -> onCollisionStart(collision), collision -> onCollisionEnd(collision)));
        this.movementComponent = this.add(movementFactory.apply(this));
        this.lifeComponent = this.add(lifeFactory.apply(this));
    }

    public VisualComponent getVisualComponent() {
        return this.visualComponent;
    }

    public DynamicPhysicsComponent getPhysicsComponent() {
        return this.physicsComponent;
    }

    public MovementComponent getMovementComponent() {
        return this.movementComponent;
    }

    public LifeComponent getLifeComponent() {
        return this.lifeComponent;
    }

    /**
     * The Callback function which gets executed if a collision with another entity starts
     * 
     * @param collision The collision which started
     */
    protected abstract void onCollisionStart(Collision collision);

    /**
     * The Callback function which gets executed if a collision with another entity ends
     * 
     * @param collision The collision which ended
     */
    protected abstract void onCollisionEnd(Collision collision);
}
