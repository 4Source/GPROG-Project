package ZombieGame.Entities;

import java.util.function.Function;

import ZombieGame.HitBox;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Components.StaticPhysicsComponent;
import ZombieGame.Components.VisualComponent;

public abstract class Obstacle extends Entity {
    private VisualComponent visualComponent;
    private StaticPhysicsComponent physicsComponent;

    /**
     * @param <P> The type of the component extending a PhysicsComponent to create with factory method
     * @param <V> The type of the component extending a VisualComponent to create with factory method
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param hitBox The hit box of the Physics component
     * @param visualFactory A Factory method to create the component
     */
    public <P extends PhysicsComponent, V extends VisualComponent> Obstacle(double posX, double posY, HitBox hitBox, Function<Entity, V> visualFactory) {
        super(posX, posY);
        this.visualComponent = this.add(visualFactory.apply(this));
        this.physicsComponent = this.add(new StaticPhysicsComponent(this, hitBox));
    }

    public VisualComponent getVisualComponent() {
        return this.visualComponent;
    }

    public StaticPhysicsComponent getPhysicsComponent() {
        return this.physicsComponent;
    }
}
