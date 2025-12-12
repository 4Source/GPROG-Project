
// (c) Thorsten Hasbargen

import java.awt.Color;

class ZombieAI extends GameObject {
	private static final int HUNTING = 1;
	private static final int STUCK = 2;
	private static final int CLEARING = 3;

	private int state;
	private double alphaClear;
	private double secondsClear;

	// life of a zombie
	private double life = 1.0;

	/**
	 * Spawns a zombie
	 * 
	 * @param startX The position in x of the zombie where is should be at game start
	 * @param startY The position in y of the zombie where is should be at game start
	 */
	public ZombieAI(double startX, double startY) {
		super(startX, startY, 0, 60, 15, new Color(160, 80, 40));
		this.isMoving = false;

		this.state = HUNTING;

		// turn left or right to clear
		this.alphaClear = Math.PI;
		if (Math.random() < 0.5) {
			this.alphaClear = -this.alphaClear;
		}
	}

	public void update(double deltaTime) {
		// if avatar is too far away: stop
		double dist = GameObject.world.getPhysicsSystem().distance(this.posX, this.posY, GameObject.world.avatar.posX, GameObject.world.avatar.posY);

		if (dist > 800) {
			this.isMoving = false;
			return;
		} else {
			this.isMoving = true;
		}

		// TODO: Switch case ?
		// state HUNTING
		if (this.state == HUNTING) {
			this.setDestination(GameObject.world.avatar);

			super.update(deltaTime);

			// handle collisions of the zombie
			GameObjectList collisions = GameObject.world.getPhysicsSystem().getCollisions(this);
			for (int i = 0; i < collisions.size(); i++) {
				GameObject obj = collisions.get(i);

				int type = obj.type();

				// if object is avatar, game over
				if (type == Constants.TYPE_AVATAR) {
					this.moveBack();
					GameObject.world.gameOver = true;
				}

				// if object is zombie, step back
				if (type == Constants.TYPE_ZOMBIE) {
					this.moveBack();
					this.state = STUCK;
					return;
				}

				// if Object is a tree, move back one step
				if (obj.type() == Constants.TYPE_TREE) {
					this.moveBack();
					this.state = STUCK;
					return;
				}
			}
		}

		// state STUCK
		else if (this.state == STUCK) {
			// seconds left for clearing
			this.secondsClear = 1.0 + Math.random() * 0.5;
			// turn and hope to get clear
			this.alpha += this.alphaClear * deltaTime;

			// try to clear
			this.state = CLEARING;
		}

		// state CLEARING
		else if (this.state == CLEARING) {
			// check, if the clearing time has ended
			this.secondsClear -= deltaTime;
			if (this.secondsClear < 0) {
				this.state = HUNTING;
				return;
			}

			// try step in this direction
			super.update(deltaTime);

			// check if path was unblocked
			GameObjectList collisions = GameObject.world.getPhysicsSystem().getCollisions(this);
			if (collisions.size() > 0) {
				this.moveBack();

				// stuck again
				this.state = STUCK;
				return;
			}

		}
	}

	/**
	 * Inform zombie it is hit
	 */
	public void hasBeenShot() {
		// every shot decreases life
		this.life -= 0.21;

		// if Zombie is dead (haha), delete it
		if (this.life <= 0) {
			this.isLiving = false;
			return;
		}
	}

	public int type() {
		return Constants.TYPE_ZOMBIE;
	}
}
