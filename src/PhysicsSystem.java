
// (c) Thorsten Hasbargen

import java.util.ArrayList;

public class PhysicsSystem {
	private static PhysicsSystem instance;
	private static World world;

	private PhysicsSystem() {
	}

	/**
	 * @return The instance of the singleton or newly created if first access.
	 */
	public static synchronized PhysicsSystem getInstance() {
		if (world == null) {
			throw new Error("PhysicsSystem needs a World to work with but no world set!");
		}
		if (instance == null) {
			instance = new PhysicsSystem();
		}

		return instance;
	}

	/**
	 * Calculates the distance between two points
	 * 
	 * @param x1 The x position of point 1
	 * @param y1 The y position of point 1
	 * @param x2 The x position of point 2
	 * @param y2 The y position of point 2
	 * @return The distance between point 1 and point 2
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		double xd = x1 - x2;
		double yd = y1 - y2;
		return Math.sqrt(xd * xd + yd * yd);
	}

	// TODO: There is possible a smarter way to do it
	/**
	 * Move object "back" reverse alpha until it just does not collide
	 * 
	 * @param entity The object to move
	 */
	public void moveBackToUncollide(Entity entity) {
		if (entity instanceof Creature) {
			double dx = Math.cos(((Creature) entity).alpha);
			double dy = Math.sin(((Creature) entity).alpha);

			while (true) {
				entity.posX -= dx;
				entity.posY -= dy;

				ArrayList<Entity> collisions = getCollisions(entity);
				if (collisions.size() == 0) {
					break;
				}
			}
		}
	}

	/**
	 * Checks for collisions of the game object
	 * 
	 * @param gameObject The game object for which the collisions should be checked
	 * @return A list of objects the game objects has collisions with
	 */
	public ArrayList<Entity> getCollisions(Entity gameObject) {
		ArrayList<Entity> result = new ArrayList<Entity>();

		int length = PhysicsSystem.world.entities.size();
		for (int i = 0; i < length; i++) {
			if (PhysicsSystem.world.entities.get(i) instanceof Entity) {
				Entity collisionTestObject = (Entity) PhysicsSystem.world.entities.get(i);

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
		}

		return result;
	}

	/**
	 * Set the world where the physics system should control
	 * 
	 * @param world The world to which it should be set
	 */
	static void setWorld(World world) {
		PhysicsSystem.world = world;
	}
}
