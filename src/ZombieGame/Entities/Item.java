package ZombieGame.Entities;

import java.awt.Color;
import java.util.function.Function;

import ZombieGame.CircleHitBox;
import ZombieGame.HitBoxType;
import ZombieGame.PhysicsCollisionLayer;
import ZombieGame.PhysicsCollisionMask;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Components.StaticPhysicsComponent;
import ZombieGame.Components.VisualComponent;

public abstract class Item extends Entity {
    private VisualComponent visualComponent;
    private PhysicsComponent physicsComponent;

    /**
     * @param <T> The type of the component to create with factory method
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param radius The size of the Item
     * @param color The color of the Item
     * @param visualFactory A Factory method to create the component
     */
    public <T extends VisualComponent> Item(double posX, double posY, int radius, Color color, Function<Entity, T> visualFactory) {
        super(posX, posY);
        this.visualComponent = this.add(visualFactory.apply(this));
        this.physicsComponent = this.add(new StaticPhysicsComponent(this, new CircleHitBox(HitBoxType.Overlap, radius), PhysicsCollisionLayer.ITEM, new PhysicsCollisionMask(PhysicsCollisionLayer.PLAYER)));
    }

    public VisualComponent getVisualComponent() {
        return this.visualComponent;
    }

    public PhysicsComponent getPhysicsComponent() {
        return this.physicsComponent;
    }
}