import java.awt.Color;
import java.util.function.Function;

public abstract class Creature extends Entity {
    private CircleComponent circleComponent;
    private PhysicsComponent physicsComponent;
    private MovementComponent movementComponent;
    private LifeComponent lifeComponent;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param radius The size of the game object
     * @param color The color of the game object
     * @param movementFactory A Factory method to create the component
     * @param lifeFactory A Factory method to create the component
     */
    public Creature(double posX, double posY, int radius, Color color, Function<Entity, MovementComponent> movementFactory, Function<Entity, LifeComponent> lifeFactory) {
        super(posX, posY);
        this.circleComponent = this.add(new CircleComponent(this, radius, color));
        this.physicsComponent = this.add(new DynamicPhysicsComponent(this, new CircleHitBox(HitBoxType.Block, radius), collision -> onCollisionStart(collision), collision -> onCollisionEnd(collision)));
        this.movementComponent = this.add(movementFactory.apply(this));
        this.lifeComponent = this.add(lifeFactory.apply(this));
    }

    public CircleComponent getCircleComponent() {
        return this.circleComponent;
    }

    public PhysicsComponent getPhysicsComponent() {
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
