
// (c) Thorsten Hasbargen

import java.awt.Color;

class Avatar extends Creature {

	/**
	 * Spawns an avatar
	 * 
	 * @param startX The position in x of the avatar where is should be at game start
	 * @param startY The position in y of the avatar where is should be at game start
	 */
	public Avatar(double startX, double startY) {
		super(startX, startY, 15, new Color(96, 96, 255));
		this.movementComponent = this.add(new PlayerMovementComponent(this, 0, 200));
		this.lifeComponent = this.add(new LifeComponent(this, 1));
	}

	@Override
	public EntityType getType() {
		return EntityType.AVATAR;
	}

	@Override
	protected void onCollisionStart(Collision collision) {
		// if Object is a tree, move back one step
		if (collision.collisionResponse() == CollisionResponse.Block) {
			this.movementComponent.moveBack();
		}

		// pick up Grenades
		else if (collision.collisionResponse() == CollisionResponse.Overlap) {
			if (collision.entity().getType() == EntityType.GRENADE_ITEM) {
				((ZombieWorld) GameObject.world).addGrenade();
				collision.entity().get(LivingComponent.class).ifPresent(component -> component.isLiving = false);
			}
		}
	}

	@Override
	protected void onCollisionEnd(Collision collision) {
	}
}
