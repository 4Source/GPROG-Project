package ZombieGame.Entities;

import java.awt.Color;

import ZombieGame.EntityType;
import ZombieGame.Components.CircleComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;

public class Gunshot extends Projectile {

	/**
	 * @param owner The entity which spawned this Projectile, the projectile will not collide with owner.
	 * @param pos The position in the world
	 * @param alpha The angle of rotation in radian
	 * @param speed The speed how fast to move
	 * @param lifetime The duration the component live before being destroyed
	 * @param damage The damage it makes in half-hearts (1 = 1/2 Heart, 2 = 1 Heart)
	 */
	public Gunshot(Entity owner, WorldPos pos, double alpha, double speed, double lifetime, int damage) {
		super(owner, pos, alpha, speed, lifetime, damage, new CircleHitBox(HitBoxType.Block, 4), new PhysicsCollisionMask(PhysicsCollisionLayer.OBSTACLES, PhysicsCollisionLayer.ZOMBIE_CHARACTER), e -> new CircleComponent(e, 4, Color.YELLOW));
	}

	/**
	 * @param owner The entity which spawned this Projectile, the projectile will not collide with owner.
	 * @param pos The position in the world
	 * @param dest The target direction of the gunshot
	 * @param speed The speed how fast to move
	 * @param lifetime The duration the component live before being destroyed
	 * @param damage The damage it makes in half-hearts (1 = 1/2 Heart, 2 = 1 Heart)
	 */
	public Gunshot(Entity owner, WorldPos pos, WorldPos dest, double speed, double lifetime, int damage) {
		this(owner, pos, Math.atan2(dest.y() - pos.y(), dest.x() - pos.x()), speed, lifetime, damage);
	}

	/**
	 * @param owner The entity which spawned this Projectile, the projectile will not collide with owner.
	 * @param pos The position in the world
	 * @param dest The target direction of the gunshot
	 * @deprecated This is only here for compatibility reasons speed, lifetime, damage should be specified
	 */
	public Gunshot(Entity owner, WorldPos pos, WorldPos dest) {
		this(owner, pos, Math.atan2(dest.y() - pos.y(), dest.x() - pos.x()), 500, 1.2, 4);
	}

	@Override
	public CircleComponent getVisualComponent() {
		return (CircleComponent) super.getVisualComponent();
	}

	@Override
	public EntityType getType() {
		return EntityType.SHOT;
	}
}
