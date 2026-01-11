package ZombieGame.Entities;

import java.util.function.Function;

import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Components.StaticPhysicsComponent;
import ZombieGame.Components.VisualComponent;
import ZombieGame.Components.WorldPositionComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.HitBox;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;

public abstract class Obstacle extends Entity {
    private final VisualComponent visualComponent;
    private final StaticPhysicsComponent physicsComponent;

    /**
     * @param <P> The type of the component extending a PhysicsComponent to create with factory method
     * @param <V> The type of the component extending a VisualComponent to create with factory method
     * @param pos The position in the world
     * @param hitBox The hit box of the Physics component
     * @param visualFactory A Factory method to create the component
     */
    public <P extends PhysicsComponent, V extends VisualComponent> Obstacle(WorldPos pos, HitBox hitBox, Function<Entity, V> visualFactory) {
        super(e -> new WorldPositionComponent(e, pos));
        this.visualComponent = this.add(visualFactory.apply(this));
        this.physicsComponent = this.add(new StaticPhysicsComponent(this, hitBox, PhysicsCollisionLayer.OBSTACLES, PhysicsCollisionMask.ALL()));
    }

    public VisualComponent getVisualComponent() {
        return this.visualComponent;
    }

    public StaticPhysicsComponent getPhysicsComponent() {
        return this.physicsComponent;
    }
}
