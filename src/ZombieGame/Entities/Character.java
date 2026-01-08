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
    private DynamicPhysicsComponent movementPhysicsComponent; // TODO: Multiple physics components one for movement and one for damage
    private MovementComponent movementComponent;
    private LifeComponent lifeComponent;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param visualFactory A Factory method to create the component
     * @param hitBox The hit box of the Physics component
     * @param layer The layer on which the PhysicsComponent should belong
     * @param mask The layers which the PhysicsComponent could interact with
     * @param movementFactory A Factory method to create the component
     * @param lifeFactory A Factory method to create the component
     */
    public Character(double posX, double posY, Function<Entity, VisualComponent> visualFactory, HitBox hitBox, PhysicsCollisionLayer layer, PhysicsCollisionMask mask, Function<Entity, MovementComponent> movementFactory, Function<Entity, LifeComponent> lifeFactory) {
        super(posX, posY);
        this.visualComponent = this.add(visualFactory.apply(this));
        this.movementPhysicsComponent = this.add(new DynamicPhysicsComponent(this, hitBox, layer, mask, collision -> onCollisionStart(collision), collision -> onCollisionEnd(collision)));
        this.movementComponent = this.add(movementFactory.apply(this));
        this.lifeComponent = this.add(lifeFactory.apply(this));
    }

    public VisualComponent getVisualComponent() {
        return this.visualComponent;
    }

    public DynamicPhysicsComponent getMovementPhysicsComponent() {
        return this.movementPhysicsComponent;
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
