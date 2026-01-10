package ZombieGame.Entities;

import java.util.function.Function;

import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Components.MovementComponent;
import ZombieGame.Components.VisualComponent;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.HitBox;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;

public abstract class Character extends Entity {
    private VisualComponent visualComponent;
    private DynamicPhysicsComponent movementPhysicsComponent;
    private DynamicPhysicsComponent damagePhysicsComponent;
    private LifeComponent lifeComponent;

    /**
     * @param pos The position in the world
     * @param visualFactory A Factory method to create the component
     * @param hitBox The hit box of the Movement Physics component
     * @param damagePhysicsFactory A Factory method to create the component
     * @param movementFactory A Factory method to create the component
     * @param lifeFactory A Factory method to create the component
     */
    public Character(Function<Entity, VisualComponent> visualFactory, HitBox hitBox, Function<Entity, DynamicPhysicsComponent> damagePhysicsFactory, Function<Entity, MovementComponent> movementFactory, Function<Entity, LifeComponent> lifeFactory) {
        super(movementFactory);
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
        this.lifeComponent = this.add(lifeFactory.apply(this));
    }

    @Override
    public MovementComponent getPositionComponent() {
        return (MovementComponent) super.getPositionComponent();
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
