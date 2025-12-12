
// (c) Thorsten Hasbargen

abstract class PhysicsSystem {
	protected World world;

	/**
	 * @param world The world which the physics system should work with
	 */
	public PhysicsSystem(World world) {
		this.world = world;
	}

	/**
	 * Checks for collisions of the game object
	 * 
	 * @param gameObject The game object for which the collisions should be checked
	 * @return A list of objects the game objects has collisions with
	 */
	protected abstract GameObjectList getCollisions(GameObject gameObject);

	/**
	 * Calculates the distance between two points
	 * 
	 * @param x1 The x position of point 1
	 * @param y1 The y position of point 1
	 * @param x2 The x position of point 2
	 * @param y2 The y position of point 2
	 * @return The distance between point 1 and point 2
	 */
	protected double distance(double x1, double y1, double x2, double y2) {
		double xd = x1 - x2;
		double yd = y1 - y2;
		return Math.sqrt(xd * xd + yd * yd);
	}

	// TODO: There is possible a smarter way to do it
	/**
	 * Move object "back" reverse alpha until it just does not collide
	 * 
	 * @param gameObject The object to move
	 */
	public void moveBackToUncollide(GameObject gameObject) {
		double dx = Math.cos(gameObject.alpha);
		double dy = Math.sin(gameObject.alpha);

		while (true) {
			gameObject.posX -= dx;
			gameObject.posY -= dy;

			GameObjectList collisions = getCollisions(gameObject);
			if (collisions.size() == 0) {
				break;
			}
		}
	}

}
