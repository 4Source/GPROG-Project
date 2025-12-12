
// (c) Thorsten Hasbargen

class CirclePhysicsSystem extends PhysicsSystem {

	CirclePhysicsSystem(World world) {
		super(world);
	}

	/**
	 * Checks for collisions of the game object. Only support circle collision detection
	 */
	public GameObjectList getCollisions(GameObject gameObject) {
		GameObjectList result = new GameObjectList();

		int length = this.world.gameObjects.size();
		for (int i = 0; i < length; i++) {
			GameObject collisionTestObject = this.world.gameObjects.get(i);

			// an object doesn't collide with itself
			if (collisionTestObject == gameObject) {
				continue;
			}

			// check if they touch each other
			double dist = gameObject.radius + collisionTestObject.radius;
			double dx = gameObject.posX - collisionTestObject.posX;
			double dy = gameObject.posY - collisionTestObject.posY;

			if (dx * dx + dy * dy < dist * dist) {
				result.add(collisionTestObject);
			}
		}

		return result;
	}
}