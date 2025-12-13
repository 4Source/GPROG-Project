
import java.awt.Color;
import java.util.ArrayList;

class Gunshot extends Entity {
	protected double alpha = 0;
	protected double speed = 0;

	protected boolean isMoving = true;

	protected double destX, destY;
	protected boolean hasDestination = false;
	protected double oldX, oldY;

	private double lifetime = 1.2;

	/**
	 * @param posX The initial position in x of the gunshot
	 * @param posY The initial position in y of the gunshot
	 * @param destX The target direction in x of the gunshot
	 * @param destY The target direction in y of the gunshot
	 */
	public Gunshot(double posX, double posY, double destX, double destY) {
		super(posX, posY, 4, Color.YELLOW);
		this.alpha = Math.atan2(destY - posY, destX - posX);
		this.speed = 500;
		this.isMoving = true;
	}

	/**
	 * @param posX The initial position in x of the gunshot
	 * @param posY The initial position in y of the gunshot
	 * @param alpha The angle of rotation in radian
	 * @param speed The speed how fast to move
	 * @param lifetime The lifetime of the gunshot how long before the gunshot despawns
	 */
	public Gunshot(double posX, double posY, double alpha, double speed, double lifetime) {
		super(posX, posY, 4, Color.YELLOW);
		this.alpha = alpha;
		this.speed = speed;
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
		ArrayList<Entity> collisions = PhysicsSystem.getInstance().getCollisions(this);
		for (int i = 0; i < collisions.size(); i++) {
			Entity obj = collisions.get(i);

			int type = obj.getType();

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

		if (!this.isMoving) {
			return;
		}

		// move if object has a destination
		if (this.hasDestination) {
			// stop if destination is reached
			double diffX = Math.abs(this.posX - this.destX);
			double diffY = Math.abs(this.posY - this.destY);
			if (diffX < 3 && diffY < 3) {
				this.isMoving = false;
				return;
			}
		}

		// remember old position
		this.oldX = this.posX;
		this.oldY = this.posY;

		// move one step
		this.posX += Math.cos(this.alpha) * this.speed * deltaTime;
		this.posY += Math.sin(this.alpha) * this.speed * deltaTime;
	}

	@Override
	public int getType() {
		return Constants.TYPE_SHOT;
	}
}
