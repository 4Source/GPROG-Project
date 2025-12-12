
import java.awt.Color;

class Gunshot extends GameObject {
	private double lifetime = 1.2;

	/**
	 * @param posX The initial position in x of the gunshot
	 * @param posY The initial position in y of the gunshot
	 * @param destX The target direction in x of the gunshot
	 * @param destY The target direction in y of the gunshot
	 */
	public Gunshot(double posX, double posY, double destX, double destY) {
		super(posX, posY, Math.atan2(destY - posY, destX - posX), 500, 4, Color.YELLOW);
		this.isMoving = true;
	}

	public Gunshot(double posX, double posY, double alpha, double speed, double lifetime) {
		super(posX, posY, alpha, speed, 4, Color.YELLOW);
		this.lifetime = lifetime;
		this.isMoving = true;
	}

	public void update(double deltaTime) {
		this.lifetime -= deltaTime;
		if (this.lifetime <= 0) {
			this.isLiving = false;
			return;
		}

		// handle collisions of the zombie
		GameObjectList collisions = GameObject.world.getPhysicsSystem().getCollisions(this);
		for (int i = 0; i < collisions.size(); i++) {
			GameObject obj = collisions.get(i);

			int type = obj.type();

			// tree: shot is deleted
			if (type == Constants.TYPE_TREE) {
				this.isLiving = false;
			}
			// Zombie: inform Zombie it is hit
			else if (type == Constants.TYPE_ZOMBIE && obj.isLiving) {
				ZombieAI zombie = (ZombieAI) obj;
				zombie.hasBeenShot();
				this.isLiving = false;
			}
		}

		super.update(deltaTime);
	}

	public final int type() {
		return Constants.TYPE_SHOT;
	}
}
