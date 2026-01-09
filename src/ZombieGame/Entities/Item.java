package ZombieGame.Entities;

import java.awt.Color;
import java.util.function.Function;

import ZombieGame.EntityType;
import ZombieGame.HitBox;
import ZombieGame.PhysicsCollisionLayer;
import ZombieGame.PhysicsCollisionMask;
import ZombieGame.PhysicsSystem;
import ZombieGame.Components.LivingComponent;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Components.StaticPhysicsComponent;
import ZombieGame.Components.VisualComponent;
import ZombieGame.Components.WorldPositionComponent;
import ZombieGame.Coordinates.WorldPos;

public abstract class Item extends Entity {
    private VisualComponent visualComponent;
    private PhysicsComponent physicsComponent;
    private LivingComponent livingComponent;

    /**
     * @param <T> The type of the component to create with factory method
     * @param pos The position in the world
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     * @param color The color of the Item
     * @param visualFactory A Factory method to create the component
     */
    public <T extends VisualComponent> Item(WorldPos pos, HitBox hitBox, Color color, Function<Entity, T> visualFactory) {
        super(e -> new WorldPositionComponent(e, pos));
        this.visualComponent = this.add(visualFactory.apply(this));
        this.physicsComponent = this.add(new StaticPhysicsComponent(this, hitBox, PhysicsCollisionLayer.ITEM, new PhysicsCollisionMask(PhysicsCollisionLayer.PLAYER)));
        this.livingComponent = this.add(new LivingComponent(this));
    }

    public VisualComponent getVisualComponent() {
        return this.visualComponent;
    }

    public PhysicsComponent getPhysicsComponent() {
        return this.physicsComponent;
    }

    public LivingComponent getLivingComponent() {
        return this.livingComponent;
    }

    @Override
    public EntityType getType() {
        return EntityType.ITEM;
    }

    /**
     * Called when picking up this item
     * 
     * @param entity The entity which picked this item up
     */
    public void pickUp(Entity entity) {
        this.getLivingComponent().kill();
    }
}