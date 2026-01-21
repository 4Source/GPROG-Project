package ZombieGame.Entities;

import java.util.ArrayList;
import java.util.function.Function;

import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Components.LifetimeComponent;
import ZombieGame.Components.StaticMovementComponent;
import ZombieGame.Components.VisualComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.CollisionResponse;
import ZombieGame.Systems.Physic.HitBox;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Systems.Physic.PhysicsSystem;

public abstract class Projectile extends Entity {
    private final LifetimeComponent lifetimeComponent;
    private final DynamicPhysicsComponent physicsComponent;
    private final VisualComponent visualComponent;
    private final Entity owner;
    private final int damage;

    /**
     * @param owner The entity which spawned this Projectile, the projectile will not collide with owner.
     * @param pos The position in the world
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     * @param lifetime The duration the component live before being destroyed
     * @param damage The damage it makes in half-hearts (1 = 1/2 Heart, 2 = 1 Heart)
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     * @param mask The layers which the PhysicsComponent could interact with
     * @param visualFactory A Factory method to create the component
     */
    public Projectile(Entity owner, WorldPos pos, double alpha, double speed, double lifetime, int damage, HitBox hitBox, PhysicsCollisionMask mask, Function<Entity, VisualComponent> visualFactory) {
        super(e -> new StaticMovementComponent(e, pos, alpha, speed));

        this.owner = owner;
        this.damage = damage;

        this.visualComponent = this.add(visualFactory.apply(this));
        this.lifetimeComponent = this.add(new LifetimeComponent(this, lifetime));
        this.physicsComponent = this.add(new DynamicPhysicsComponent(this, hitBox, PhysicsCollisionLayer.PROJECTILE, mask, c -> onCollisionEnter(c), c -> onCollisionStay(c), c -> onCollisionExit(c)));
    }

    /**
     * @param owner The entity which spawned this Projectile, the projectile will not collide with owner.
     * @param pos The position in the world
     * @param dest The target direction of the gunshot
     * @param speed The speed how fast to move
     * @param lifetime The duration the component live before being destroyed
     * @param damage The damage it makes in half-hearts (1 = 1/2 Heart, 2 = 1 Heart)
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     * @param mask The layers which the PhysicsComponent could interact with
     * @param visualFactory A Factory method to create the component
     */
    public Projectile(Entity owner, WorldPos pos, WorldPos dest, double speed, double lifetime, int damage, HitBox hitBox, PhysicsCollisionMask mask, Function<Entity, VisualComponent> visualFactory) {
        this(owner, pos, Math.atan2(dest.y() - pos.y(), dest.x() - pos.x()), speed, lifetime, damage, hitBox, mask, visualFactory);
    }

    @Override
    public StaticMovementComponent getPositionComponent() {
        return (StaticMovementComponent) super.getPositionComponent();
    }

    public DynamicPhysicsComponent getPhysicsComponent() {
        return this.physicsComponent;
    }

    public LifetimeComponent getLifetimeComponent() {
        return this.lifetimeComponent;
    }

    public VisualComponent getVisualComponent() {
        return this.visualComponent;
    }

    /**
     * The Callback function which gets executed if a collision with another entity starts
     * 
     * @param collision The collision which started
     */
    protected void onCollisionEnter(Collision collision) {
        if (collision.collisionResponse() == CollisionResponse.Block) {
            if (collision.entity().equals(this.owner)) {
                // Cancel collision with owner of projectile
                return;
            }

            // Make damage to all the lifeComponents of the hitted entity
            ArrayList<LifeComponent> lifeComponents = collision.entity().getComponents(LifeComponent.class);
            for (LifeComponent lifeComponent : lifeComponents) {
                lifeComponent.takeDamage(this.damage);
            }

            // Blocking collision therefor destroy projectile
            this.lifetimeComponent.kill();
        }
    }

    protected void onCollisionStay(Collision collision) {
    }

    protected void onCollisionExit(Collision collision) {
    }
}
