package ZombieGame.Systems.Physic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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

public class PhysicsSystem implements DebuggableText {
	private static PhysicsSystem instance;
	private Map<PhysicsComponent, Map<PhysicsComponent, CollisionResponse>> collisionBuffer;
	private Set<PhysicsComponent> invalidComponents;
	private long lastUpdateDuration = -1;

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
		if (this.collisionBuffer.containsKey(component)) {
			changed.set(true);
		} else {
			this.collisionBuffer.computeIfAbsent(component, c -> {
				changed.set(true);
				return new HashMap<>();
			});
		}
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
		this.invalidComponents.remove(component);
		if (this.collisionBuffer.remove(component) != null) {
			changed = true;
		}

		for (Map<PhysicsComponent, CollisionResponse> inner : collisionBuffer.values()) {
			if (inner.remove(component) != null) {
				changed = true;
			}
		}

		return changed;
	}

	/**
	 * Invalidate the buffered collisions of the component.
	 * This also invalidates the collisions of the components which had a collision with the component.
	 * 
	 * @param component The component to invalidate the collisions for
	 */
	public void invalidateBufferFor(PhysicsComponent component) {
		invalidComponents.add(component);

		Map<PhysicsComponent, CollisionResponse> colliding = collisionBuffer.get(component);
		if (colliding != null) {
			for (Map.Entry<PhysicsComponent, CollisionResponse> entry : colliding.entrySet()) {
				switch (entry.getValue()) {
					case None:
						// Do nothing
						break;
					case Block:
					case Overlap:
						invalidComponents.add(entry.getKey());
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
		long start = System.currentTimeMillis();

		// Ensure dynamic components keep their collisions updated even when they are
		// "stuck" and no longer moving. This enables reliable collision callbacks
		// like onStay (e.g., melee enemies attacking a standing player).
		for (PhysicsComponent c : collisionBuffer.keySet()) {
			if (c instanceof DynamicPhysicsComponent) {
				invalidComponents.add(c);
			}
		}

		Iterator<PhysicsComponent> it = invalidComponents.iterator();
		while (it.hasNext()) {
			PhysicsComponent component = it.next();

			if (component instanceof DynamicPhysicsComponent) {
				this.collisionBuffer.keySet().forEach(otherComponent -> {
					if (otherComponent == component) {
						return;
					}

					CollisionResponse response = component.checkCollision(otherComponent);

					// Look up the previous collision state to avoid firing onEnter every time
					// the physics buffer is invalidated (e.g., when a character is moving).
					CollisionResponse previous = this.collisionBuffer
							.getOrDefault(component, new HashMap<>())
							.getOrDefault(otherComponent, CollisionResponse.None);

					if (response != CollisionResponse.None) {
						this.collisionBuffer.get(component).put(otherComponent, response);
						this.collisionBuffer.get(otherComponent).put(component, response);

						// Trigger collision onEnter only when the collision really starts.
						if (previous == CollisionResponse.None) {
							((DynamicPhysicsComponent) component).onEnter.accept(new Collision(otherComponent.getEntity(), response));
						} else {
							// Collision continues: fire onStay so game logic can react (e.g. melee attacks)
							((DynamicPhysicsComponent) component).onStay.accept(new Collision(otherComponent.getEntity(), response));
						}
					} else if (response == CollisionResponse.None && previous != CollisionResponse.None) {
						this.collisionBuffer.get(component).remove(otherComponent);
						this.collisionBuffer.get(otherComponent).remove(component);

						// Trigger collision onExit
						((DynamicPhysicsComponent) component).onExit.accept(new Collision(otherComponent.getEntity(), response));
					}
				});
			}

			it.remove();
		}
		lastUpdateDuration = (System.currentTimeMillis() - start);
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
		if (!collisionBuffer.containsKey(component)) {
			System.err.println("PhysicsComponent of Entity is not registered!");
			return result;
		}

		// Physics component not up to date
		if (invalidComponents.contains(component)) {
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
		if (!collisionBuffer.containsKey(component)) {
			System.err.println("PhysicsComponent of Entity is not registered!");
			return false;
		}

		// Physics component not up to date
		if (invalidComponents.contains(component)) {
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

		if (otherComponent == component) {
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
		if (collisionBuffer.containsKey(component)) {
			System.err.println("PhysicsComponent of Entity is registered, use 'hasCollision' instead if this is expected!");

			// Physics component not up to date
			if (invalidComponents.contains(component)) {
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
				return;
			}
		});

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
		elements.add(String.format("Physics invalid Components: %d", invalidComponents.size()));
		elements.add(String.format("Physics update time: %d", lastUpdateDuration));
		return elements;
	}
}
