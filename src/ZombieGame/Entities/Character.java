package ZombieGame.Entities;

import java.util.function.Function;

import ZombieGame.Collision;
import ZombieGame.HitBox;
import ZombieGame.PhysicsCollisionLayer;
import ZombieGame.PhysicsCollisionMask;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Components.MovementComponent;
import ZombieGame.Components.VisualComponent;

public abstract class Character extends Entity {
    private VisualComponent visualComponent;
    private DynamicPhysicsComponent movementPhysicsComponent;
    private DynamicPhysicsComponent damagePhysicsComponent;
    private MovementComponent movementComponent;
    private LifeComponent lifeComponent;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param visualFactory A Factory method to create the component
     * @param hitBox The hit box of the Movement Physics component
     * @param damagePhysicsFactory A Factory method to create the component
     * @param movementFactory A Factory method to create the component
     * @param lifeFactory A Factory method to create the component
     */
    public Character(double posX, double posY, Function<Entity, VisualComponent> visualFactory, HitBox hitBox, Function<Entity, DynamicPhysicsComponent> damagePhysicsFactory, Function<Entity, MovementComponent> movementFactory, Function<Entity, LifeComponent> lifeFactory) {
        super(posX, posY);
        this.visualComponent = this.add(visualFactory.apply(this));
        this.movementPhysicsComponent = this.add(new DynamicPhysicsComponent(
                this,
                hitBox,
                PhysicsCollisionLayer.PLAYER_CHARACTER,
                new PhysicsCollisionMask(PhysicsCollisionLayer.CHARACTER, PhysicsCollisionLayer.OBSTACLES),
                collision -> onMovementCollisionStart(collision),
                collision -> onMovementCollisionStay(collision),
                collision -> onMovementCollisionEnd(collision)));
        this.damagePhysicsComponent = this.add(damagePhysicsFactory.apply(this));
        this.movementComponent = this.add(movementFactory.apply(this));
        this.lifeComponent = this.add(lifeFactory.apply(this));
    }

    public VisualComponent getVisualComponent() {
        return this.visualComponent;
    }

    public DynamicPhysicsComponent getMovementPhysicsComponent() {
        return this.movementPhysicsComponent;
    }

    public DynamicPhysicsComponent getDamagePhysicsComponent() {
        return this.damagePhysicsComponent;
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
    protected abstract void onMovementCollisionStart(Collision collision);

    /**
     * Callback executed while a collision with another entity continues.
     * Default is no-op; override in subclasses when needed.
     */
    protected void onMovementCollisionStay(Collision collision) {
        // default: do nothing
    }

    /**
     * The Callback function which gets executed if a collision with another entity ends
     * 
     * @param collision The collision which ended
     */
    protected abstract void onMovementCollisionEnd(Collision collision);
}
