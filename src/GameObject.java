
// (c) Thorsten Hasbargen

import java.awt.Color;

abstract class GameObject {
	protected double posX, posY;
	protected double alpha = 0;
	protected double speed = 0;
	protected int radius = 7;
	protected Color color;

	// if the object is existing, moving etc
	protected boolean isLiving = true;
	protected boolean isMoving = true;

	// destination the object shall move to,
	// old position etc
	private double destX, destY;
	private boolean hasDestination = false;
	private double oldX, oldY;

	// GameObjects sometimes call physics methods
	protected static World world;

	/**
	 * @param posX The position in x direction
	 * @param posY The position in y direction
	 * @param alpha The angle of rotation in radian
	 * @param speed The speed how fast to move
	 * @param radius The size of the game object
	 * @param color The color of the game object
	 */
	public GameObject(double posX, double posY, double alpha, double speed, int radius, Color color) {
		this.posX = posX;
		this.posY = posY;
		this.oldX = posX;
		this.oldY = posY;
		this.alpha = alpha;
		this.speed = speed;
		this.radius = radius;
		this.color = color;
	}

	/**
	 * Update the game object using delta time to get constant change with varying fps
	 * 
	 * @param deltaTime The time since last frame
	 */
	public void update(double deltaTime) {
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

	/**
	 * Check if collision with window borders and reflect
	 */
	protected void reflectOnBorders() {
		// TODO: Add comments
		if (this.posX < this.radius && (this.alpha > Math.PI / 2 && this.alpha < Math.PI * 3 / 2)) {
			this.alpha = Math.PI - this.alpha;
		}
		if (this.posY < this.radius && this.alpha > Math.PI) {
			this.alpha = Math.PI * 2 - this.alpha;
		}
		if (this.posX > Constants.WORLD_WIDTH - this.radius) {
			this.alpha = Math.PI - this.alpha;
		}
		if (this.posY > Constants.WORLD_HEIGHT - this.radius) {
			this.alpha = Math.PI * 2 - this.alpha;
		}

		if (this.alpha < 0) {
			this.alpha += Math.PI * 2;
		}
		if (this.alpha > Math.PI * 2) {
			this.alpha -= Math.PI * 2;
		}
	}

	/**
	 * Set a point in the world as destination
	 * 
	 * @param destinationX The world position x where to move
	 * @param destinationY The world position y where to move
	 */
	public final void setDestination(double destinationX, double destinationY) {
		this.isMoving = true;
		this.hasDestination = true;
		this.destX = destinationX;
		this.destY = destinationY;

		this.alpha = Math.atan2(destinationY - this.posY, destinationX - this.posX);
	}

	/**
	 * Set the location of an object as destination
	 * 
	 * @param destination The object where to move to
	 */
	public void setDestination(GameObject destination) {
		setDestination(destination.posX, destination.posY);
	}

	/**
	 * Move back to the position before the move Method was called
	 */
	protected void moveBack() {
		this.posX = this.oldX;
		this.posY = this.oldY;
	}

	/**
	 * Returns the type of the game object
	 * 
	 * @return
	 */
	abstract int type();

	/**
	 * Set the world where the game objects are belonging to
	 * 
	 * @param world The world to which it should be set
	 */
	static void setWorld(World world) {
		GameObject.world = world;
	}
}
