
import java.awt.Color;

class Gunshot extends Entity {
	private CircleComponent circleComponent;
	private LifetimeComponent lifetimeComponent;
	private MovementComponent movementComponent;
	private PhysicsComponent physicsComponent;

	/**
	 * @param posX The initial position in x of the gunshot
	 * @param posY The initial position in y of the gunshot
	 * @param destX The target direction in x of the gunshot
	 * @param destY The target direction in y of the gunshot
	 */
	public Gunshot(double posX, double posY, double destX, double destY) {
		super(posX, posY);
		this.circleComponent = this.add(new CircleComponent(this, 4, Color.YELLOW));
		this.lifetimeComponent = this.add(new LifetimeComponent(this, 1.2));
		this.movementComponent = this.add(new StaticMovementComponent(this, Math.atan2(destY - posY, destX - posX), 500));
		this.physicsComponent = this.add(new DynamicPhysicsComponent(this, new CircleHitBox(HitBoxType.Block, 4), c -> onCollision(c), c -> {}));
	}

	/**
	 * @param posX The initial position in x of the gunshot
	 * @param posY The initial position in y of the gunshot
	 * @param alpha The angle of rotation in radian
	 * @param speed The speed how fast to move
	 * @param lifetime The lifetime of the gunshot how long before the gunshot despawns
	 */
	public Gunshot(double posX, double posY, double alpha, double speed, double lifetime) {
		super(posX, posY);
		this.circleComponent = this.add(new CircleComponent(this, 4, Color.YELLOW));
		this.lifetimeComponent = this.add(new LifetimeComponent(this, lifetime));
		this.movementComponent = this.add(new StaticMovementComponent(this, alpha, speed));
		this.physicsComponent = this.add(new DynamicPhysicsComponent(this, new CircleHitBox(HitBoxType.Block, 4), c -> onCollision(c), c -> {}));
	}

	public CircleComponent getCircleComponent() {
		return this.circleComponent;
	}

	public LifetimeComponent getLifetimeComponent() {
		return this.lifetimeComponent;
	}

	public MovementComponent getMovementComponent() {
		return this.movementComponent;
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
				zombie.getLifeComponent().takeDamage(0.21);
				this.lifetimeComponent.kill();
			}
		}
	}

	@Override
	public EntityType getType() {
		return EntityType.SHOT;
	}
}
