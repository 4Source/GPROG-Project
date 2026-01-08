package ZombieGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import ZombieGame.Capabilities.Drawable;
import ZombieGame.Components.Component;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.UIElement;

public abstract class World {
	// top left corner of the displayed pane of the world
	public double worldPartX = 0;
	public double worldPartY = 0;

	// if game is over
	public boolean gameOver = false;

	// all objects in the game, including the Avatar
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private ArrayList<UIElement> uiElements = new ArrayList<UIElement>();
	private ArrayList<Entity> pendingAdditions = new ArrayList<>();
	private ArrayList<Entity> pendingRemovals = new ArrayList<>();

	World() {
	}

	public final void spawnEntity(Entity entity) {
		pendingAdditions.add(entity);
	}

	public final void despawnEntity(Entity entity) {
		pendingRemovals.add(entity);
	}

	public final void update() {
		// Add new entities
		for (Entity e : pendingAdditions) {
			addEntity(e);
		}
		pendingAdditions.clear();

		// Remove entities
		for (Entity e : pendingRemovals) {
			removeEntity(e);
		}
		pendingRemovals.clear();
	}

	private final void registerEntityComponents(Entity entity) {
		entity.getComponents(PhysicsComponent.class).forEach(c -> PhysicsSystem.getInstance().registerComponent(c));
		entity.getComponentsByCapability(Drawable.class).forEach(c -> GraphicSystem.getInstance().registerComponent(c));
	}

	/**
	 * Add an entity to the world.
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#addUIElement(UIElement)}
	 * <ul>
	 * <li><b>Register components of entity to systems:</b>
	 * <ul>
	 * <li>PhysicsComponent → PhysicsSystem</li>
	 * <li>Drawable → GraphicSystem</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param entity The entity which should be added to the world
	 */
	private final void addEntity(Entity entity) {
		this.entities.add(entity);
		this.registerEntityComponents(entity);
	}

	private final void unregisterEntityComponents(Entity entity) {
		entity.getComponents(PhysicsComponent.class).forEach(c -> PhysicsSystem.getInstance().unregisterComponent(c));
		entity.getComponentsByCapability(Drawable.class).forEach(c -> GraphicSystem.getInstance().unregisterComponent(c));
	}

	/**
	 * Remove an entity from the world.
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#removeUIElement(UIElement)}
	 * <ul>
	 * <li><b>Unregister components of entity from systems:</b>
	 * <ul>
	 * <li>PhysicsComponent → PhysicsSystem</li>
	 * <li>Drawable → GraphicSystem</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param entity The entity which should be removed from the world
	 */
	private final void removeEntity(Entity entity) {
		this.entities.remove(entity);
		this.unregisterEntityComponents(entity);
	}

	/**
	 * Remove an entity from the world.
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#removeUIElement(int)}
	 * <ul>
	 * <li><b>Unregister components of entity from systems:</b>
	 * <ul>
	 * <li>PhysicsComponent → PhysicsSystem</li>
	 * <li>Drawable → GraphicSystem</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param index The index of the entity which should be removed from the world
	 */
	private final void removeEntity(int index) {
		Entity entity = this.getEntity(index);
		this.removeEntity(entity);
	}

	/**
	 * Returns all entities.
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#getUIElements()}
	 */
	public final Collection<Entity> getEntities() {
		return Collections.unmodifiableCollection(this.entities);
	}

	/**
	 * Returns all entities matching the Class.
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#getUIElements(Class)}
	 * 
	 * @param <T> A Class which extends the Entity Class
	 * @param type The type of Entity which should be returned
	 */
	public final <T extends Entity> Collection<Entity> getEntities(Class<T> type) {
		ArrayList<Entity> elements = new ArrayList<>();
		for (Entity e : this.entities) {
			if (type.isInstance(e)) {
				elements.add(e);
			}
		}

		return Collections.unmodifiableCollection(elements);
	}

	/**
	 * Returns all entities having the component.
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#getUIElementsWithComponent(Class)}
	 * 
	 * @param <T> A Class which extends the Component Class
	 * @param type The type of Component which should be returned
	 */
	public final <T extends Component> Collection<Entity> getEntitiesWithComponent(Class<T> type) {
		ArrayList<Entity> elements = new ArrayList<>();
		for (Entity e : this.entities) {
			if (!e.getComponents(type).isEmpty()) {
				elements.add(e);
			}
		}

		return Collections.unmodifiableCollection(elements);
	}

	/**
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#getUIElement(int)}
	 * 
	 * @param index The index of the entity.
	 */
	public final Entity getEntity(int index) {
		return this.entities.get(index);
	}

	/**
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#getUIElement(Class)}
	 * 
	 * @param <T> A Class which extends the Entity Class
	 * @param type The type of Entity which should be returned
	 */
	public final <T extends Entity> Optional<T> getEntity(Class<T> type) {
		for (Entity e : this.entities) {
			if (type.isInstance(e)) {
				return Optional.of(type.cast(e));
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns an iterator over the entities.
	 * <p>
	 * Ensure side effects.
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#uiElementIterator()}
	 */
	public Iterator<Entity> entityIterator() {
		return new Iterator<>() {
			private final Iterator<Entity> it = entities.iterator();
			private Entity last = null;

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public Entity next() {
				last = it.next();
				return last;
			}

			@Override
			public void remove() {
				if (last == null) {
					throw new IllegalStateException("remove() called before next()");
				}

				it.remove();
				unregisterEntityComponents(last);
				last = null;
			}
		};
	}

	private final void registerUIElementComponents(UIElement uiElement) {
		uiElement.getComponentsByCapability(Drawable.class).forEach(c -> GraphicSystem.getInstance().registerComponent(c));
	}

	/**
	 * Add an ui element to the world.
	 * <p>
	 * For {@link Entity Entities} use {@link World#addEntity(Entity)}
	 * <ul>
	 * <li><b>Register components of ui element to systems:</b>
	 * <ul>
	 * <li>Drawable → GraphicSystem</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param uiElement The ui element which should be added to the world
	 */
	public final void addUIElement(UIElement uiElement) {
		this.uiElements.add(uiElement);
		this.registerUIElementComponents(uiElement);
	}

	private final void unregisterUIElementComponents(UIElement uiElement) {
		uiElement.getComponentsByCapability(Drawable.class).forEach(c -> GraphicSystem.getInstance().unregisterComponent(c));
	}

	/**
	 * Remove an ui element from the world.
	 * <p>
	 * For {@link Entity Entities} use {@link World#removeEntity(Entity)}
	 * <ul>
	 * <li><b>Unregister components of ui element from systems:</b>
	 * <ul>
	 * <li>Drawable → GraphicSystem</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param uiElement The ui element which should be removed from the world
	 */
	public final void removeUIElement(UIElement uiElement) {
		this.uiElements.remove(uiElement);
		this.unregisterUIElementComponents(uiElement);
	}

	/**
	 * Remove an ui element from the world.
	 * <p>
	 * For {@link Entity Entities} use {@link World#removeEntity(int)}
	 * <ul>
	 * <li><b>Unregister components of ui element from systems:</b>
	 * <ul>
	 * <li>Drawable → GraphicSystem</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param index The index of the ui element which should be removed from the world
	 */
	public final void removeUIElement(int index) {
		UIElement uiElement = this.getUIElement(index);
		this.removeUIElement(uiElement);
	}

	/**
	 * Returns all ui elements.
	 * <p>
	 * For {@link Entity Entities} use {@link World#getEntities()}
	 */
	public final Collection<UIElement> getUIElements() {
		return Collections.unmodifiableCollection(this.uiElements);
	}

	/**
	 * Returns all ui elements matching the Class.
	 * <p>
	 * For {@link Entity Entities} use {@link World#getEntities(Class)}
	 * 
	 * @param <T> A Class which extends the UIElement Class
	 * @param type The type of ui element which should be returned
	 */
	public final <T extends UIElement> Collection<UIElement> getUIElements(Class<T> type) {
		ArrayList<UIElement> elements = new ArrayList<>();
		for (UIElement e : this.uiElements) {
			if (type.isInstance(e)) {
				elements.add(e);
			}
		}

		return Collections.unmodifiableCollection(elements);
	}

	/**
	 * Returns all ui elements having the component.
	 * <p>
	 * For {@link Entity Entities} use {@link World#getEntitiesWithComponent(Class)}
	 * 
	 * @param <T> A Class which extends the Component Class
	 * @param type The type of Component which should be returned
	 */
	public final <T extends Component> Collection<UIElement> getUIElementsWithComponent(Class<T> type) {
		ArrayList<UIElement> elements = new ArrayList<>();
		for (UIElement e : this.uiElements) {
			if (!e.getComponents(type).isEmpty()) {
				elements.add(e);
			}
		}

		return Collections.unmodifiableCollection(elements);
	}

	/**
	 * <p>
	 * For {@link Entity Entities} use {@link World#getEntity(int)}
	 * 
	 * @param index The index of the ui element.
	 */
	public final UIElement getUIElement(int index) {
		return this.uiElements.get(index);
	}

	/**
	 * <p>
	 * For {@link Entity Entities} use {@link World#getEntity(Class)}
	 * 
	 * @param <T> A Class which extends the UIElement Class
	 * @param type The type of ui element which should be returned
	 */
	public final <T extends UIElement> Optional<T> getUIElement(Class<T> type) {
		for (UIElement e : this.uiElements) {
			if (type.isInstance(e)) {
				return Optional.of(type.cast(e));
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns an iterator over the ui elements.
	 * <p>
	 * Ensure side effects.
	 * <p>
	 * For {@link Entity Entities} use {@link World#entityIterator()}
	 */
	public Iterator<UIElement> uiElementIterator() {
		return new Iterator<>() {
			private final Iterator<UIElement> it = uiElements.iterator();
			private UIElement last = null;

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public UIElement next() {
				last = it.next();
				return last;
			}

			@Override
			public void remove() {
				if (last == null) {
					throw new IllegalStateException("remove() called before next()");
				}

				it.remove();
				unregisterUIElementComponents(last);
				last = null;
			}
		};
	}

	// adjust the displayed pane of the world according to Avatar and Bounds
	public final void adjustWorldPart() {
		final int RIGHT_END = Constants.WORLD_WIDTH - Constants.WORLDPART_WIDTH;
		final int BOTTOM_END = Constants.WORLD_HEIGHT - Constants.WORLDPART_HEIGHT;

		Optional<Avatar> opt = this.getEntity(Avatar.class);
		if (opt.isEmpty()) {
			System.err.println("No avatar found in world!");
			return;
		}
		Avatar avatar = opt.get();

		// if avatar is too much right in display ...
		if (avatar.getPosX() > this.worldPartX + Constants.WORLDPART_WIDTH - Constants.SCROLL_BOUNDS) {
			// ... adjust display
			this.worldPartX = avatar.getPosX() + Constants.SCROLL_BOUNDS - Constants.WORLDPART_WIDTH;
			if (this.worldPartX >= RIGHT_END) {
				this.worldPartX = RIGHT_END;
			}
		}

		// same left
		else if (avatar.getPosX() < this.worldPartX + Constants.SCROLL_BOUNDS) {
			this.worldPartX = avatar.getPosX() - Constants.SCROLL_BOUNDS;
			if (this.worldPartX <= 0) {
				this.worldPartX = 0;
			}
		}

		// same bottom
		if (avatar.getPosY() > this.worldPartY + Constants.WORLDPART_HEIGHT - Constants.SCROLL_BOUNDS) {
			this.worldPartY = avatar.getPosY() + Constants.SCROLL_BOUNDS - Constants.WORLDPART_HEIGHT;
			if (this.worldPartY >= BOTTOM_END) {
				this.worldPartY = BOTTOM_END;
			}
		}

		// same top
		else if (avatar.getPosY() < this.worldPartY + Constants.SCROLL_BOUNDS) {
			this.worldPartY = avatar.getPosY() - Constants.SCROLL_BOUNDS;
			if (this.worldPartY <= 0) {
				this.worldPartY = 0;
			}
		}

	}

	protected abstract void init();

	protected abstract void createNewObjects(double deltaTime);

}
