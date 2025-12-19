
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
		super(startX, startY, 15, new Color(160, 80, 40));
		this.movementComponent = this.add(new AIMovementComponent(this, 0, 60));
		this.lifeComponent = this.add(new LifeComponent(this, 1));
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
				this.movementComponent.moveBack();
				GameObject.world.gameOver = true;
			}

			// if object is zombie, step back
			if (type == EntityType.ZOMBIE) {
				this.movementComponent.moveBack();
				((AIMovementComponent) this.movementComponent).state = AIState.STUCK;
				return;
			}

			// if Object is a tree, move back one step
			if (type == EntityType.TREE) {
				this.movementComponent.moveBack();
				((AIMovementComponent) this.movementComponent).state = AIState.STUCK;
				return;
			}
		}

	}

	@Override
	protected void onCollisionEnd(Collision collision) {
	}
}
