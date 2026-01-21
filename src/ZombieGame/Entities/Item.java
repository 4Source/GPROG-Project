package ZombieGame.Entities;

import java.awt.Color;
import java.util.function.Function;

import ZombieGame.EntityType;
import ZombieGame.Components.LivingComponent;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Components.StaticPhysicsComponent;
import ZombieGame.Components.VisualComponent;
import ZombieGame.Components.WorldPositionComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.HitBox;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Systems.Physic.PhysicsSystem;

public abstract class Item extends Entity {
    private final VisualComponent visualComponent;
    private final PhysicsComponent physicsComponent;
    private final LivingComponent livingComponent;

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
        this.physicsComponent = this.add(new StaticPhysicsComponent(this, hitBox, PhysicsCollisionLayer.ITEM, new PhysicsCollisionMask(PhysicsCollisionLayer.BODY)));
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