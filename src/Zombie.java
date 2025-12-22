
// (c) Thorsten Hasbargen

import java.awt.Color;

class Zombie extends Creature {
	/**
	 * Spawns a zombie
	 * 
	 * @param startX The position in x of the zombie where is should be at game start
	 * @param startY The position in y of the zombie where is should be at game start
	 */
	public Zombie(double startX, double startY) {
		super(startX, startY, 15, new Color(160, 80, 40), e -> new AIMovementComponent(e, 0, 60), e -> new LifeComponent(e, 100));
	}

	@Override
	public AIMovementComponent getMovementComponent() {
		return (AIMovementComponent) super.getMovementComponent();
	}

	@Override
	public EntityType getType() {
		return EntityType.ZOMBIE;
	}

	@Override
	protected void onCollisionStart(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Block) {
			EntityType type = collision.entity().getType();

			// if object is avatar, game over
			if (type == EntityType.AVATAR) {
				this.getMovementComponent().moveBack();
				Entity.world.gameOver = true;
			}

			// if object is zombie, step back
			if (type == EntityType.ZOMBIE) {
				this.getMovementComponent().moveBack();
				this.getMovementComponent().state = AIState.STUCK;
				return;
			}

			// if Object is a tree, move back one step
			if (type == EntityType.TREE) {
				this.getMovementComponent().moveBack();
				this.getMovementComponent().state = AIState.STUCK;
				return;
			}
		}

	}

	@Override
	protected void onCollisionEnd(Collision collision) {
	}
}
