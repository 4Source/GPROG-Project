package ZombieGame.Systems.Physic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import ZombieGame.Capabilities.DebuggableText;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Components.StaticPhysicsComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;
import ZombieGame.Systems.Debug.DebugSystem;

enum EventType {
	ENTER, STAY, EXIT
}

record CollisionEvent(DynamicPhysicsComponent component, PhysicsComponent other, CollisionResponse response, EventType type) {
}

public class PhysicsSystem implements DebuggableText {
	private static PhysicsSystem instance;
	private HashMap<PhysicsComponent, HashMap<PhysicsComponent, CollisionResponse>> collisionBuffer;
	private Set<PhysicsComponent> invalidComponents;
	private long lastUpdateDuration = -1;
	private long lastEventDispatchDuration = -1;

	private PhysicsSystem() {
		this.collisionBuffer = new HashMap<>();
		this.invalidComponents = new HashSet<>();
		if (!DebugSystem.getInstance().registerDebuggable(this)) {
			System.err.println("Failed to register PhysicsSystem to debug system");
		}
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
	 * @return {@code true} if the registration was successful or if it was already registered
	 */
	public boolean registerComponent(PhysicsComponent component) {
		AtomicBoolean changed = new AtomicBoolean(false);

		// Component is already registered
		if (this.collisionBuffer.containsKey(component)) {
			changed.set(true);
		}
		// Add unregistered component
		else {
			this.collisionBuffer.computeIfAbsent(component, c -> {
				changed.set(true);
				return new HashMap<>();
			});
		}

		// Invalidate the buffer for this component
		invalidateBufferFor(component);

		return changed.get();
	}

	/**
	 * Unregister a physics component from the physics calculation
	 * 
	 * @param component The component to unregister
	 * @return {@code true} if unregistering was successful, {@code false} if not successful or not contained
	 */
	public boolean unregisterComponent(PhysicsComponent component) {
		boolean changed = false;

		// Remove the component form invalid components if existed
		this.invalidComponents.remove(component);

		// Remove the main entry of the component
		if (this.collisionBuffer.remove(component) != null) {
			changed = true;
		}

		// Remove secondary entries from other component which have an entry for this component
		for (HashMap<PhysicsComponent, CollisionResponse> entriesForComponents : this.collisionBuffer.values()) {
			if (entriesForComponents.remove(component) != null) {
				changed = true;
			}
		}

		return changed;
	}

	/**
	 * Invalidate the buffered collisions of the component.
	 * This also invalidates the collisions of the components which had a collision with the component.
	 * <p>
	 * The collision states are stored pairwise {@code A} ↔ {@code B}, {@code A} ↔ {@code C}, {@code C} ↔ {@code D}, when than {@code A} should be invalidated than all components that have a collision with this component also have to be invalidated.
	 * Here {@code A}, {@code B}, and {@code C} need to be invalidated because as it is here if only {@code A} gets invalidated {@code C} and {@code B} still will contain the old information that they have a collision with {@code A}.
	 * {@code D} should not be invalidated even if {@code C} is invalidated because of {@code A} the reason is that for {@code C} only the collision with {@code A} is not valid anymore but the collision with {@code D} still could be valid.
	 * </p>
	 * 
	 * @param component The component to invalidate the collisions for
	 */
	public void invalidateBufferFor(PhysicsComponent component) {
		HashMap<PhysicsComponent, CollisionResponse> entriesForInvalid = this.collisionBuffer.get(component);
		if (entriesForInvalid != null) {
			// Add component to the invalidated components list
			this.invalidComponents.add(component);

			// Invalidate all components which had a collision with this component
			Iterator<Entry<PhysicsComponent, CollisionResponse>> entriesIterator = entriesForInvalid.entrySet().iterator();
			while (entriesIterator.hasNext()) {
				Entry<PhysicsComponent, CollisionResponse> entry = entriesIterator.next();
				switch (entry.getValue()) {
					case None:
						// Remove components with no collision but an entry to reduce iteration time for future calls
						entriesIterator.remove();
						break;
					case Block:
					case Overlap:
						// Add components with collision to the invalidation
						this.invalidComponents.add(entry.getKey());
						break;

					default:
						System.err.println("Invalid CollisionResponse.");
						break;
				}
			}
		} else {
			System.err.println("Can not invalidate buffer for a component which is not registered");
		}
	}

	/**
	 * Calculates the distance between two points
	 * 
	 * @param pos1 The position of point 1
	 * @param pos2 The position of point 2
	 * @return The distance between point 1 and point 2
	 */
	public static double distance(WorldPos pos1, WorldPos pos2) {
		WorldPos d = pos1.sub(pos2).pow2();
		return Math.sqrt(d.x() + d.y());
	}

	/**
	 * Update the collisions of the {@link PhysicsComponent physic components} with each other
	 */
	public void update() {
		try {
			ArrayDeque<CollisionEvent> eventQueue = new ArrayDeque<>();

			// Update invalid components
			long start = System.currentTimeMillis();
			Iterator<PhysicsComponent> invalidIt = this.invalidComponents.iterator();
			while (invalidIt.hasNext()) {
				// When invalid component is a dynamic physics component check for collisions
				PhysicsComponent invalidComponent = invalidIt.next();
				if (invalidComponent instanceof DynamicPhysicsComponent) {
					// Check for every other registered component if the invalid component has a collision with it
					for (PhysicsComponent otherComponent : this.collisionBuffer.keySet()) {
						// Can not have collision with it self
						if (otherComponent.equals(invalidComponent)) {
							continue;
						}

						// Check for collisions between invalid component and other component
						CollisionResponse response = invalidComponent.checkCollision(otherComponent);

						// Look up the previous collision state to avoid firing onEnter every time
						// the physics buffer is invalidated (e.g., when a character is moving).
						CollisionResponse previous = this.collisionBuffer.getOrDefault(invalidComponent, new HashMap<>()).getOrDefault(otherComponent, CollisionResponse.None);

						// There is a collision between invalid component and other component
						if (response != CollisionResponse.None) {
							// Try getting the entries for the invalid component
							HashMap<PhysicsComponent, CollisionResponse> entriesForInvalid = this.collisionBuffer.get(invalidComponent);
							if (entriesForInvalid != null) {
								// Add an collision entry for invalid component with other component
								entriesForInvalid.put(otherComponent, response);
							} else {
								throw new Exception("This should not be possible. Tried to add to component which is not registered");
							}

							// Try getting the entries for the other component
							HashMap<PhysicsComponent, CollisionResponse> entriesForOther = this.collisionBuffer.get(otherComponent);
							if (entriesForOther != null) {
								// Add an collision entry for other component with invalid component
								entriesForOther.put(invalidComponent, response);
							} else {
								throw new Exception("This should not be possible. Tried to add to component which is not registered");
							}

							// Previously there was no collision but now is -> onEnter
							if (previous == CollisionResponse.None) {
								// Save the event to process it later
								eventQueue.add(new CollisionEvent((DynamicPhysicsComponent) invalidComponent, otherComponent, response, EventType.ENTER));
							}
							// There was previously already a collision an still is -> onStay
							else {
								// Save the event to process it later
								eventQueue.add(new CollisionEvent((DynamicPhysicsComponent) invalidComponent, otherComponent, response, EventType.STAY));
							}
						}
						// There is no collision anymore but was previously
						else if (response == CollisionResponse.None && previous != CollisionResponse.None) {
							// Try getting the entries for the invalid component
							HashMap<PhysicsComponent, CollisionResponse> entriesForInvalid = this.collisionBuffer.get(invalidComponent);
							if (entriesForInvalid != null) {
								// Remove the previous collision entry for invalid component with other component
								entriesForInvalid.remove(otherComponent);
							} else {
								throw new Exception("This should not be possible. Tried to remove from a component which is not registered");
							}

							// Try getting the entries for the other component
							HashMap<PhysicsComponent, CollisionResponse> entriesForOther = this.collisionBuffer.get(otherComponent);
							if (entriesForOther != null) {
								// Remove the previous collision entry for other component with invalid component
								entriesForOther.remove(invalidComponent);
							} else {
								throw new Exception("This should not be possible. Tried to remove from a component which is not registered");
							}

							// There was previously already a collision but is not anymore -> onExit
							// Save the event to process it later
							eventQueue.add(new CollisionEvent((DynamicPhysicsComponent) invalidComponent, otherComponent, response, EventType.EXIT));
						}
					}
				}

				// Remove component from invalid components list
				invalidIt.remove();
			}
			this.lastUpdateDuration = (System.currentTimeMillis() - start);

			// Dispatch callbacks
			Iterator<CollisionEvent> eventIt = eventQueue.iterator();
			while (eventIt.hasNext()) {
				CollisionEvent event = eventIt.next();
				switch (event.type()) {
					case ENTER:
						event.component().onEnter.accept(new Collision(event.other().getEntity(), event.response()));
						break;
					case STAY:
						event.component().onStay.accept(new Collision(event.other().getEntity(), event.response()));
						break;
					case EXIT:
						event.component().onExit.accept(new Collision(event.other().getEntity(), event.response()));
						break;
					default:
						throw new Exception("Unknown Event type");
				}

				eventIt.remove();
			}
			this.lastEventDispatchDuration = (System.currentTimeMillis() - start - this.lastUpdateDuration);

		} catch (Exception e) {
			System.err.println("Failed updating Physics component with following error:");
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Get collisions of the entity with another registered entity.
	 * 
	 * @param entity The entity for which the collisions should be checked
	 * @return A list of {@link Collision collisions} the entities has collisions with
	 */
	public ArrayList<Collision> getCollisions(Entity entity) {
		ArrayList<Collision> result = new ArrayList<>();

		for (PhysicsComponent component : entity.getComponents(PhysicsComponent.class)) {
			result.addAll(getCollisions(component));
		}

		return result;
	}

	/**
	 * Get collisions of the component with another registered physics component.
	 * 
	 * @param component The component for which the collisions should be checked
	 * @return A list of {@link Collision collisions} the component has collisions with
	 */
	public ArrayList<Collision> getCollisions(PhysicsComponent component) {
		ArrayList<Collision> result = new ArrayList<>();

		// Physics component is not registered in the PhysicsSystem
		if (!this.collisionBuffer.containsKey(component)) {
			System.err.println("PhysicsComponent of Entity is not registered!");
			return result;
		}

		// Physics component not up to date
		if (this.invalidComponents.contains(component)) {
			System.out.println("Unplanned update of PhysicsSystem!");
			update();
		}

		HashMap<PhysicsComponent, CollisionResponse> entriesForComponent = this.collisionBuffer.get(component);
		if (entriesForComponent != null) {
			for (Entry<PhysicsComponent, CollisionResponse> entry : entriesForComponent.entrySet()) {
				if (entry.getValue() != CollisionResponse.None) {
					result.add(new Collision(entry.getKey().getEntity(), entry.getValue()));
				}
			}
		}

		return result;
	}

	/**
	 * Check if entity has a collision with another registered entity. Returns early if collision found.
	 * 
	 * @param entity The entity for which the collisions should be checked
	 * @return {@code true} when the first collision was found
	 */
	public boolean hasCollision(Entity entity) {
		AtomicBoolean result = new AtomicBoolean(false);

		for (PhysicsComponent component : entity.getComponents(PhysicsComponent.class)) {
			if (hasCollision(component)) {
				result.set(true);
			}
		}

		return result.get();
	}

	/**
	 * Check if component has a collision with another registered physics component. Returns early if collision found.
	 * 
	 * @param component The component for which the collisions should be checked
	 * @return {@code true} when the first collision was found
	 */
	public boolean hasCollision(PhysicsComponent component) {
		AtomicBoolean result = new AtomicBoolean(false);

		// Physics component is not registered in the PhysicsSystem
		if (!this.collisionBuffer.containsKey(component)) {
			System.err.println("PhysicsComponent of Entity is not registered!");
			return false;
		}

		// Physics component not up to date
		if (this.invalidComponents.contains(component)) {
			System.out.println("Unplanned update of PhysicsSystem!");
			update();
		}

		HashMap<PhysicsComponent, CollisionResponse> entriesForComponent = this.collisionBuffer.get(component);
		if (entriesForComponent != null) {
			for (CollisionResponse response : entriesForComponent.values()) {
				if (response != CollisionResponse.None) {
					result.set(true);
					break;
				}
			}
		}

		return result.get();
	}

	/**
	 * Check if entity has a collision with the other entity. Returns early if collision found.
	 * 
	 * @param entity The entity for which the collisions should be checked
	 * @param otherEntity The entity against which collisions should be checked
	 * @return {@code true} when the first collision was found
	 */
	public static boolean hasCollisionWith(Entity entity, Entity otherEntity) {
		AtomicBoolean result = new AtomicBoolean(false);

		for (PhysicsComponent component : entity.getComponents(PhysicsComponent.class)) {
			for (PhysicsComponent otherComponent : otherEntity.getComponents(PhysicsComponent.class)) {
				if (hasCollisionWith(component, otherComponent)) {
					result.set(true);
				}
			}
		}

		return result.get();
	}

	/**
	 * Check if component has a collision with the other physics component. Returns early if collision found.
	 * 
	 * @param component The component for which the collisions should be checked
	 * @param otherComponent The component against which collisions should be checked
	 * @return {@code true} when the first collision was found
	 */
	public static boolean hasCollisionWith(PhysicsComponent component, PhysicsComponent otherComponent) {
		AtomicBoolean result = new AtomicBoolean(false);

		if (otherComponent.equals(component)) {
			System.err.println("Tried to check collision with it self");
			return false;
		}

		CollisionResponse response = component.checkCollision(otherComponent);
		if (response != CollisionResponse.None) {
			result.set(true);
		}

		return result.get();

	}

	/**
	 * Check for an entity which is not registered in the physics system if it has a collision with another entity. Returns early if collision found.
	 * This is useful to test placement of new entities in world.
	 * 
	 * @param entity The entity for which the collisions should be checked
	 * @return {@code true} when the first collision was found
	 */
	public boolean testCollision(Entity entity) {
		boolean result = false;

		for (PhysicsComponent component : entity.getComponents(PhysicsComponent.class)) {
			if (testCollision(component)) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Check for an entity which is not registered in the physics system if it has a collision with another entity. Returns early if collision found.
	 * This is useful to test placement of new entities in world.
	 * 
	 * @param component The component for which the collisions should be checked
	 * @return {@code true} when the first collision was found
	 */
	public boolean testCollision(PhysicsComponent component) {
		AtomicBoolean result = new AtomicBoolean(false);

		// Physics component is not registered in the PhysicsSystem
		if (this.collisionBuffer.containsKey(component)) {
			System.err.println("PhysicsComponent of Entity is registered, use 'hasCollision' instead if this is expected!");

			// Physics component not up to date
			if (this.invalidComponents.contains(component)) {
				System.out.println("Unplanned update of PhysicsSystem!");
				update();
			}
			HashMap<PhysicsComponent, CollisionResponse> entriesForComponent = this.collisionBuffer.get(component);
			if (entriesForComponent != null) {
				for (CollisionResponse response : entriesForComponent.values()) {
					if (response != CollisionResponse.None) {
						result.set(true);
						break;
					}
				}
			}
		}

		for (PhysicsComponent otherComponent : this.collisionBuffer.keySet()) {
			if (otherComponent.equals(component)) {
				continue;
			}

			CollisionResponse response = component.checkCollision(otherComponent);
			if (response != CollisionResponse.None) {
				result.set(true);
				break;
			}
		}

		return result.get();
	}

	@Override
	public DebugCategoryMask getCategoryMask() {
		return new DebugCategoryMask(DebugCategory.PERFORMANCE, DebugCategory.PHYSICS);
	}

	@Override
	public ArrayList<String> getTextElements() {
		ArrayList<String> elements = new ArrayList<>();

		int staticCount = 0;
		int dynamicCount = 0;
		int coll = 0;
		for (PhysicsComponent c : this.collisionBuffer.keySet()) {
			coll += this.getCollisions(c).size();
			if (c instanceof DynamicPhysicsComponent) {
				dynamicCount++;
			}
			if (c instanceof StaticPhysicsComponent) {
				staticCount++;
			}
		}
		elements.add(String.format("Physics Components: %d", this.collisionBuffer.size()));
		elements.add(String.format("Physics Static Components: %d", staticCount));
		elements.add(String.format("Physics Dynamic Components: %d", dynamicCount));
		elements.add(String.format("Physics Collisions: %d", coll));
		elements.add(String.format("Physics invalid Components: %d", this.invalidComponents.size()));
		elements.add(String.format("Physics update time: %d", this.lastUpdateDuration));
		elements.add(String.format("Physics event dispatch time: %d", this.lastEventDispatchDuration));
		return elements;
	}
}
