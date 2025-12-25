import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class PhysicsSystem {
	private static PhysicsSystem instance;
	public static boolean enableDebug = false;
	private Map<PhysicsComponent, Map<PhysicsComponent, CollisionResponse>> collisionBuffer;
	private Set<PhysicsComponent> invalidEntries;

	private PhysicsSystem() {
		this.collisionBuffer = new HashMap<>();
		this.invalidEntries = new HashSet<>();
	}

	/**
	 * @return The instance of the singleton or newly created if first access.
	 */
	public static synchronized PhysicsSystem getInstance() {
		if (PhysicsSystem.instance == null) {
			PhysicsSystem.instance = new PhysicsSystem();
		}

		return PhysicsSystem.instance;
	}

	/**
	 * Register a physics component for the physics calculation
	 * 
	 * @param component The component to register
	 */
	public void registerComponent(PhysicsComponent component) {
		collisionBuffer.putIfAbsent(component, new HashMap<>());
		invalidateBufferFor(component);
	}

	/**
	 * Unregister a physics component from the physics calculation
	 * 
	 * @param component The component to unregister
	 */
	public void unregisterComponent(PhysicsComponent component) {
		invalidEntries.remove(component);
		collisionBuffer.remove(component);

		for (Map<PhysicsComponent, CollisionResponse> inner : collisionBuffer.values()) {
			inner.remove(component);
		}
	}

	/**
	 * Invalidate the buffered collisions of the component.
	 * This also invalidates the collisions of the components which had a collision with the component.
	 * 
	 * @param component The component to invalidate the collisions for
	 */
	public void invalidateBufferFor(PhysicsComponent component) {
		invalidEntries.add(component);

		Map<PhysicsComponent, CollisionResponse> colliding = collisionBuffer.get(component);
		if (colliding != null) {
			for (Map.Entry<PhysicsComponent, CollisionResponse> entry : colliding.entrySet()) {
				switch (entry.getValue()) {
					case None:
						// Do nothing
						break;
					case Block:
					case Overlap:
						invalidEntries.add(entry.getKey());
						break;

					default:
						System.err.println("Invalid CollisionResponse.");
						break;
				}
			}
		}
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

	/**
	 * Update the collisions of the {@link PhysicsComponent physic components} with each other
	 */
	public void update() {
		if (InputSystem.getInstance().isPressed(Action.SHOW_HIT_BOXES)) {
			PhysicsSystem.enableDebug = !PhysicsSystem.enableDebug;
		}

		Iterator<PhysicsComponent> it = invalidEntries.iterator();
		while (it.hasNext()) {
			PhysicsComponent component = it.next();

			if (component instanceof DynamicPhysicsComponent) {
				this.collisionBuffer.keySet().forEach(otherComponent -> {
					if (otherComponent == component) {
						return;
					}

					CollisionResponse response = component.checkCollision(otherComponent);
					if (response != CollisionResponse.None) {
						this.collisionBuffer.get(component).put(otherComponent, response);
						this.collisionBuffer.get(otherComponent).put(component, response);

						// Trigger collision onEnter
						((DynamicPhysicsComponent) component).onEnter.accept(new Collision(otherComponent.getEntity(), response));
					} else if (response == CollisionResponse.None && this.collisionBuffer.getOrDefault(component, new HashMap<>()).getOrDefault(otherComponent, CollisionResponse.None) != CollisionResponse.None) {
						this.collisionBuffer.get(component).remove(otherComponent);
						this.collisionBuffer.get(otherComponent).remove(component);

						// Trigger collision onExit
						((DynamicPhysicsComponent) component).onExit.accept(new Collision(otherComponent.getEntity(), response));
					}
				});
			}

			it.remove();
		}
	}

	/**
	 * Checks for collisions of the game object
	 * 
	 * @param entity The game object for which the collisions should be checked
	 * @return A list of {@link Collision collisions} the game objects has collisions with
	 */
	public ArrayList<Collision> getCollisions(Entity entity) {
		Optional<PhysicsComponent> opt = entity.getComponent(PhysicsComponent.class);
		ArrayList<Collision> result = new ArrayList<>();

		// No physics component for entity
		if (opt.isEmpty()) {
			return result;
		}

		PhysicsComponent component = opt.get();

		// Physics component is not registered in the PhysicsSystem
		if (!collisionBuffer.containsKey(component)) {
			System.err.println("PhysicsComponent of Entity is not registered!");
			return result;
		}

		// Physics component not up to date
		if (invalidEntries.contains(component)) {
			System.out.println("Unplanned update of PhysicsSystem!");
			update();
		}

		this.collisionBuffer.get(component).forEach((otherComponent, response) -> {
			if (response != CollisionResponse.None) {
				result.add(new Collision(otherComponent.getEntity(), response));
			}
		});

		return result;
	}

	/**
	 * Check if entity has a collision with another entity. Returns early if collision found.
	 * 
	 * @param entity The entity to check if it has collision
	 * @return True when the first collision was found
	 */
	public boolean hasCollision(Entity entity) {
		Optional<PhysicsComponent> opt = entity.getComponent(PhysicsComponent.class);
		AtomicBoolean result = new AtomicBoolean(false);

		// No physics component for entity
		if (opt.isEmpty()) {
			return false;
		}

		PhysicsComponent component = opt.get();

		// Physics component is not registered in the PhysicsSystem
		if (!collisionBuffer.containsKey(component)) {
			System.err.println("PhysicsComponent of Entity is not registered!");
			return false;
		}

		// Physics component not up to date
		if (invalidEntries.contains(component)) {
			System.out.println("Unplanned update of PhysicsSystem!");
			update();
		}

		this.collisionBuffer.get(component).forEach((otherComponent, response) -> {
			if (response != CollisionResponse.None) {
				result.set(true);
				return;
			}
		});

		return result.get();
	}

	/**
	 * Check for an entity which is not registered in the physics system if it has a collision with another entity. Returns early if collision found.
	 * This is useful to test placement of new entities in world.
	 * 
	 * @param entity The entity to check if it has collision
	 * @return True when the first collision was found
	 */
	public boolean testCollision(Entity entity) {
		Optional<PhysicsComponent> opt = entity.getComponent(PhysicsComponent.class);
		AtomicBoolean result = new AtomicBoolean(false);

		// No physics component for entity
		if (opt.isEmpty()) {
			return false;
		}

		PhysicsComponent component = opt.get();

		// Physics component is not registered in the PhysicsSystem
		if (collisionBuffer.containsKey(component)) {
			System.err.println("PhysicsComponent of Entity is registered, use 'hasCollision' instead if this is expected!");

			// Physics component not up to date
			if (invalidEntries.contains(component)) {
				System.out.println("Unplanned update of PhysicsSystem!");
				update();
			}

			this.collisionBuffer.get(component).forEach((otherComponent, response) -> {
				if (response != CollisionResponse.None) {
					result.set(true);
					return;
				}
			});
		}

		this.collisionBuffer.keySet().forEach(otherComponent -> {
			if (otherComponent == component) {
				return;
			}

			CollisionResponse response = component.checkCollision(otherComponent);
			if (response != CollisionResponse.None) {
				result.set(true);
			}
		});

		return result.get();
	}
}
