package ZombieGame.Systems.Physic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import ZombieGame.Capabilities.Drawable;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicLayer;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;

public class PhysicsSystem implements Drawable {
	private static PhysicsSystem instance;
	public static boolean enableDebug = false;
	private Map<PhysicsComponent, Map<PhysicsComponent, CollisionResponse>> collisionBuffer;
	private Set<PhysicsComponent> invalidEntries;
	private long lastUpdateDuration = -1;

	private PhysicsSystem() {
		this.collisionBuffer = new HashMap<>();
		this.invalidEntries = new HashSet<>();
		GraphicSystem.getInstance().registerDrawable(this);
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
				invalidEntries.add(c);
			}
		}

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
	 * Checks for collisions of the entity
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
	 * Checks for collisions of the component
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
		AtomicBoolean result = new AtomicBoolean(false);

		for (PhysicsComponent component : entity.getComponents(PhysicsComponent.class)) {
			if (hasCollision(component)) {
				result.set(true);
			}
		}

		return result.get();
	}

	/**
	 * Check if entity has a collision with another entity. Returns early if collision found.
	 * 
	 * @param component The component for which the collisions should be checked
	 * @return True when the first collision was found
	 */
	public boolean hasCollision(PhysicsComponent component) {
		AtomicBoolean result = new AtomicBoolean(false);

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
		AtomicBoolean result = new AtomicBoolean(false);

		for (PhysicsComponent component : entity.getComponents(PhysicsComponent.class)) {
			if (testCollision(component)) {
				result.set(true);
			}
		}

		return result.get();
	}

	/**
	 * Check for an entity which is not registered in the physics system if it has a collision with another entity. Returns early if collision found.
	 * This is useful to test placement of new entities in world.
	 * 
	 * @param component The component for which the collisions should be checked
	 * @return True when the first collision was found
	 */
	public boolean testCollision(PhysicsComponent component) {
		AtomicBoolean result = new AtomicBoolean(false);

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

	@Override
	public void draw() {
		if (PhysicsSystem.enableDebug) {
			ViewPos pos = new ViewPos(20, 260);
			int comp = PhysicsSystem.getInstance().collisionBuffer.size();
			int coll = 0;
			for (PhysicsComponent c : PhysicsSystem.getInstance().collisionBuffer.keySet()) {
				coll += PhysicsSystem.getInstance().getCollisions(c).size();
			}

			GraphicSystem.getInstance().drawString("Components: " + comp, pos, new DrawStyle().color(Color.MAGENTA));
			GraphicSystem.getInstance().drawString("Collisions: " + coll, pos.add(0, 20), new DrawStyle().color(Color.MAGENTA));
			GraphicSystem.getInstance().drawString("Time: " + lastUpdateDuration, pos.add(0, 40), new DrawStyle().color(Color.MAGENTA));
		}
	}

	@Override
	public GraphicLayer getLayer() {
		return GraphicLayer.UI;
	}

	@Override
	public int getDepth() {
		return 0;
	}
}
