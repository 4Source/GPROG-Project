package ZombieGame.Systems.Physic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private final HashSet<PhysicsComponent> registeredComponents;
	private HashMap<PhysicsComponent, HashMap<PhysicsComponent, CollisionResponse>> previousBuffer;
	private HashMap<PhysicsComponent, HashMap<PhysicsComponent, CollisionResponse>> currentBuffer;

	private long lastUpdateDuration = -1;
	private long lastEventDispatchDuration = -1;

	private PhysicsSystem() {
		this.registeredComponents = new HashSet<>();
		this.previousBuffer = new HashMap<>();
		this.currentBuffer = new HashMap<>();
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
		// Component is already registered
		if (this.registeredComponents.contains(component)) {
			return true;
		}
		// Add unregistered component
		else {
			return this.registeredComponents.add(component);
		}
	}

	/**
	 * Unregister a physics component from the physics calculation
	 * 
	 * @param component The component to unregister
	 * @return {@code true} if unregistering was successful, {@code false} if not successful or not contained
	 */
	public boolean unregisterComponent(PhysicsComponent component) {
		return this.registeredComponents.remove(component);
	}

	/**
	 * Update the collisions of the {@link PhysicsComponent physic components} with each other
	 */
	public void update() {
		try {
			ArrayDeque<CollisionEvent> eventQueue = new ArrayDeque<>();

			// Swap the previous buffer with current buffer
			this.previousBuffer = this.currentBuffer;
			this.currentBuffer = new HashMap<>();
			for (PhysicsComponent entry : this.registeredComponents) {
				currentBuffer.put(entry, new HashMap<>());
			}

			// Update all components
			long start = System.currentTimeMillis();
			Iterator<PhysicsComponent> componentsIt = this.currentBuffer.keySet().iterator();
			while (componentsIt.hasNext()) {
				PhysicsComponent component = componentsIt.next();

				// When component is a dynamic physics component check for collisions
				if (component instanceof DynamicPhysicsComponent) {
					// Check for every other registered component if the invalid component has a collision with it
					for (PhysicsComponent otherComponent : this.currentBuffer.keySet()) {
						// Can not have collision with it self
						if (otherComponent.equals(component)) {
							continue;
						}

						// Check for collisions between invalid component and other component
						CollisionResponse response = component.checkCollision(otherComponent);

						// Look up the previous collision state to avoid firing onEnter every time
						// the physics buffer is invalidated (e.g., when a character is moving).
						CollisionResponse previous = this.previousBuffer.getOrDefault(component, new HashMap<>()).getOrDefault(otherComponent, CollisionResponse.None);

						// There is a collision between invalid component and other component
						if (response != CollisionResponse.None) {
							// Try getting the entries for the invalid component
							HashMap<PhysicsComponent, CollisionResponse> entriesForInvalid = this.currentBuffer.get(component);
							if (entriesForInvalid != null) {
								// Add an collision entry for invalid component with other component
								entriesForInvalid.put(otherComponent, response);
							} else {
								throw new Exception("This should not be possible. Tried to add to component which is not registered");
							}

							// Try getting the entries for the other component
							HashMap<PhysicsComponent, CollisionResponse> entriesForOther = this.currentBuffer.get(otherComponent);
							if (entriesForOther != null) {
								// Add an collision entry for other component with invalid component
								entriesForOther.put(component, response);
							} else {
								throw new Exception("This should not be possible. Tried to add to component which is not registered");
							}

							// Previously there was no collision but now is -> onEnter
							if (previous == CollisionResponse.None) {
								// Save the event to process it later
								eventQueue.add(new CollisionEvent((DynamicPhysicsComponent) component, otherComponent, response, EventType.ENTER));
							}
							// There was previously already a collision an still is -> onStay
							else {
								// Save the event to process it later
								eventQueue.add(new CollisionEvent((DynamicPhysicsComponent) component, otherComponent, response, EventType.STAY));
							}
						}
						// There is no collision anymore but was previously
						else if (response == CollisionResponse.None && previous != CollisionResponse.None) {
							// Try getting the entries for the invalid component
							HashMap<PhysicsComponent, CollisionResponse> entriesForInvalid = this.currentBuffer.get(component);
							if (entriesForInvalid != null) {
								// Remove the previous collision entry for invalid component with other component
								entriesForInvalid.remove(otherComponent);
							} else {
								throw new Exception("This should not be possible. Tried to remove from a component which is not registered");
							}

							// Try getting the entries for the other component
							HashMap<PhysicsComponent, CollisionResponse> entriesForOther = this.currentBuffer.get(otherComponent);
							if (entriesForOther != null) {
								// Remove the previous collision entry for other component with invalid component
								entriesForOther.remove(component);
							} else {
								throw new Exception("This should not be possible. Tried to remove from a component which is not registered");
							}

							// There was previously already a collision but is not anymore -> onExit
							// Save the event to process it later
							eventQueue.add(new CollisionEvent((DynamicPhysicsComponent) component, otherComponent, response, EventType.EXIT));
						}
					}
				}
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
		if (!this.registeredComponents.contains(component)) {
			System.err.println("PhysicsComponent of Entity is not registered!");
			return result;
		}

		HashMap<PhysicsComponent, CollisionResponse> entriesForComponent = this.currentBuffer.get(component);
		if (entriesForComponent == null) {
			System.err.println("getCollisions: Could not find entries for PhysicsComponent of Entity!");
			return result;
		}

		for (Entry<PhysicsComponent, CollisionResponse> entry : entriesForComponent.entrySet()) {
			if (entry.getValue() != CollisionResponse.None) {
				result.add(new Collision(entry.getKey().getEntity(), entry.getValue()));
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
		if (!this.registeredComponents.contains(component)) {
			System.err.println("PhysicsComponent of Entity is not registered!");
			return false;
		}

		HashMap<PhysicsComponent, CollisionResponse> entriesForComponent = this.currentBuffer.get(component);
		if (entriesForComponent == null) {
			System.err.println("hasCollision: Could not find entries for PhysicsComponent of Entity!");
			return false;
		}

		for (CollisionResponse response : entriesForComponent.values()) {
			if (response == CollisionResponse.Block) {
				result.set(true);
				break;
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
		if (response == CollisionResponse.Block) {
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
		if (this.registeredComponents.contains(component)) {
			throw new IllegalArgumentException("PhysicsComponent of Entity is registered, use 'hasCollision' instead if this is expected!");
		}

		for (PhysicsComponent otherComponent : this.registeredComponents) {
			if (otherComponent.equals(component)) {
				continue;
			}

			CollisionResponse response = component.checkCollision(otherComponent);
			if (response == CollisionResponse.Block) {
				result.set(true);
				break;
			}
		}

		return result.get();
	}

	/**
	 * Checks whether a registered entity would have a blocking collision with any other registered entity,
	 * excluding collisions against {@code except}.
	 * <p>
	 * This is intentionally independent from the current collision buffer and uses direct hitbox checks.
	 * It is primarily used for "push" interactions where an entity is displaced outside its own update().
	 */
	public boolean hasBlockingCollisionExcept(Entity entity, Entity except) {
		for (PhysicsComponent component : entity.getComponents(PhysicsComponent.class)) {
			if (hasBlockingCollisionExcept(component, entity, except)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasBlockingCollisionExcept(PhysicsComponent component, Entity owner, Entity except) {
		for (PhysicsComponent otherComponent : this.registeredComponents) {
			// skip self
			if (otherComponent.equals(component)) {
				continue;
			}
			Entity otherEntity = otherComponent.getEntity();
			if (otherEntity.equals(owner)) {
				continue;
			}
			if (except != null && otherEntity.equals(except)) {
				continue;
			}
			CollisionResponse response = component.checkCollision(otherComponent);
			if (response == CollisionResponse.Block) {
				return true;
			}
		}
		return false;
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
		for (PhysicsComponent c : this.registeredComponents) {
			coll += this.getCollisions(c).size();
			if (c instanceof DynamicPhysicsComponent) {
				dynamicCount++;
			}
			if (c instanceof StaticPhysicsComponent) {
				staticCount++;
			}
		}
		elements.add(String.format("Physics Components: %d", this.registeredComponents.size()));
		elements.add(String.format("Physics Static Components: %d", staticCount));
		elements.add(String.format("Physics Dynamic Components: %d", dynamicCount));
		elements.add(String.format("Physics Collisions: %d", coll));
		elements.add(String.format("Physics update time: %d", this.lastUpdateDuration));
		elements.add(String.format("Physics event dispatch time: %d", this.lastEventDispatchDuration));
		return elements;
	}
}
