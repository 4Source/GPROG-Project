package ZombieGame.Entities;

import java.awt.Color;

import ZombieGame.EntityType;
import ZombieGame.Components.CircleComponent;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifetimeComponent;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Components.StaticMovementComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.CollisionResponse;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;

public class Gunshot extends Entity {
	private CircleComponent circleComponent;
	private LifetimeComponent lifetimeComponent;
	private PhysicsComponent physicsComponent;

	/**
	 * @param pos The position in the world
	 * @param dest The target direction of the gunshot
	 */
	public Gunshot(WorldPos pos, WorldPos dest) {
		super(e -> new StaticMovementComponent(e, pos, Math.atan2(dest.y() - pos.y(), dest.x() - pos.x()), 500));
		this.circleComponent = this.add(new CircleComponent(this, 4, Color.YELLOW));
		this.lifetimeComponent = this.add(new LifetimeComponent(this, 1.2));
		this.physicsComponent = this.add(new DynamicPhysicsComponent(this, new CircleHitBox(HitBoxType.Block, 4), PhysicsCollisionLayer.PROJECTILE, new PhysicsCollisionMask(PhysicsCollisionLayer.OBSTACLES, PhysicsCollisionLayer.ZOMBIE), c -> onCollision(c), c -> {}));
	}

	/**
	 * @param pos The position in the world
	 * @param alpha The angle of rotation in radian
	 * @param speed The speed how fast to move
	 * @param lifetime The lifetime of the gunshot how long before the gunshot despawns
	 */
	public Gunshot(WorldPos pos, double alpha, double speed, double lifetime) {
		super(e -> new StaticMovementComponent(e, pos, alpha, speed));
		this.circleComponent = this.add(new CircleComponent(this, 4, Color.YELLOW));
		this.lifetimeComponent = this.add(new LifetimeComponent(this, lifetime));
		this.physicsComponent = this.add(new DynamicPhysicsComponent(this, new CircleHitBox(HitBoxType.Block, 4), PhysicsCollisionLayer.PROJECTILE, new PhysicsCollisionMask(PhysicsCollisionLayer.OBSTACLES, PhysicsCollisionLayer.ZOMBIE), c -> onCollision(c), c -> {}));
	}

	public CircleComponent getCircleComponent() {
		return this.circleComponent;
	}

	public LifetimeComponent getLifetimeComponent() {
		return this.lifetimeComponent;
	}

	@Override
	public StaticMovementComponent getPositionComponent() {
		return (StaticMovementComponent) super.getPositionComponent();
	}

	public PhysicsComponent getPhysicsComponent() {
		return this.physicsComponent;
	}

	/**
	 * The Callback function which gets executed if a collision with another entity starts
	 * 
	 * @param collision The collision which started
	 */
	protected void onCollision(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Block) {
			EntityType type = collision.entity().getType();
			// tree: shot is deleted
			if (type == EntityType.TREE) {
				this.lifetimeComponent.kill();
			}
			// Zombie: inform Zombie it is hit
			else if (type == EntityType.ZOMBIE) {
				Zombie zombie = (Zombie) collision.entity();
				zombie.getLifeComponent().takeDamage(21);
				this.lifetimeComponent.kill();
			}
		}
	}

	@Override
	public EntityType getType() {
		return EntityType.SHOT;
	}
}
