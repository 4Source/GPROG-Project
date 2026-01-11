package ZombieGame.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import ZombieGame.Viewport;
import ZombieGame.Capabilities.Drawable;
import ZombieGame.Components.Component;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Coordinates.ChunkIndex;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.DataStructures.UniquePriorityQueue;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.UIElement;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Physic.PhysicsSystem;

public abstract class World {
	// if game is over
	public boolean gameOver = false;

	private Viewport viewport = new Viewport();

	// all objects in the game, including the Avatar
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private ArrayList<UIElement> uiElements = new ArrayList<UIElement>();
	private ArrayList<Entity> pendingAdditions = new ArrayList<>();
	private ArrayList<Entity> pendingRemovals = new ArrayList<>();
	private final Map<ChunkIndex, Chunk> generatedChunks = new HashMap<>();
	private final Map<ChunkIndex, Boolean> loadedChunks = new HashMap<>();
	private final UniquePriorityQueue<ChunkIndex> generationQueue = new UniquePriorityQueue<>(new ChunkDistanceComparator(this));

	protected World() {
		// // Ensure at least on chunk is generated + TILE_SIZE is set for queuing new chunks
		// registerChunk(generateChunk(new ChunkIndex()));
	}

	public final void spawnEntity(Entity entity) {
		pendingAdditions.add(entity);
	}

	public final void despawnEntity(Entity entity) {
		pendingRemovals.add(entity);
	}

	/**
	 * Update the world using delta time to get constant change with varying fps
	 * 
	 * @param deltaTime The time since last frame in seconds
	 */
	public final void update(double deltaTime) {
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
		entity.getComponentsByCapability(Drawable.class).forEach(c -> GraphicSystem.getInstance().registerDrawable(c));
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
		entity.getComponentsByCapability(Drawable.class).forEach(c -> GraphicSystem.getInstance().unregisterDrawable(c));
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
		uiElement.getComponentsByCapability(Drawable.class).forEach(c -> GraphicSystem.getInstance().registerDrawable(c));
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
		uiElement.getComponentsByCapability(Drawable.class).forEach(c -> GraphicSystem.getInstance().unregisterDrawable(c));
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

	/**
	 * Adjust the viewport to the world according to Avatar and Bounds
	 */
	public final void adjustWorldPart() {
		Optional<Avatar> opt = this.getEntity(Avatar.class);
		if (opt.isEmpty()) {
			System.err.println("No avatar found in world!");
			return;
		}
		Avatar avatar = opt.get();
		ViewPos avatarPos = avatar.getPositionComponent().getViewPos();

		// if avatar is too much right in display
		if (!avatarPos.isInsideScrollBoundMaxX()) {
			Offset o = new Offset(avatarPos.x(), 0);
			o = o.sub(Viewport.getBottomRight().x() - Viewport.SCROLL_BOUNDS, 0);

			viewport.move(o);
			updateLoadedChunks();
		}
		// if avatar is too much left in display
		else if (!avatarPos.isInsideScrollBoundMinX()) {
			Offset o = new Offset(avatarPos.x(), 0);
			o = o.sub(Viewport.SCROLL_BOUNDS, 0);

			viewport.move(o);
			updateLoadedChunks();
		}

		// if avatar is too much bottom in display
		if (!avatarPos.isInsideScrollBoundMaxY()) {
			Offset o = new Offset(0, avatarPos.y());
			o = o.sub(0, Viewport.getBottomRight().y() - Viewport.SCROLL_BOUNDS);

			viewport.move(o);
			updateLoadedChunks();
		}
		// if avatar is too much top in display
		else if (!avatarPos.isInsideScrollBoundMinY()) {
			Offset o = new Offset(0, avatarPos.y());
			o = o.sub(0, Viewport.SCROLL_BOUNDS);

			viewport.move(o);
			updateLoadedChunks();
		}
	}

	public final Viewport getViewport() {
		return this.viewport;
	}

	public abstract Chunk generateChunk(ChunkIndex index);

	public final Optional<Chunk> getChunk(ChunkIndex coord) {
		return Optional.ofNullable(this.generatedChunks.get(coord));
	}

	public final boolean isChunkGenerated(ChunkIndex index) {
		return this.generatedChunks.containsKey(index);
	}

	public final boolean isChunkLoaded(ChunkIndex index) {
		return this.loadedChunks.getOrDefault(index, false);
	}

	public final boolean isChunkQueuedForGeneration(ChunkIndex index) {
		return this.generationQueue.contains(index);
	}

	/**
	 * Schedule a chunk to be generated
	 * 
	 * @param index The pos for which a chunk should be generated
	 */
	public final void enqueueChunkForGeneration(ChunkIndex index) {
		this.generationQueue.add(index);
		// loadChunk(index);
	}

	/**
	 * Generate chunks from the queue.
	 * 
	 * @param maxChunks The maximum amount of chunks generated with this call
	 */
	public final void processGenerationQueue(int maxChunks) {
		int chunkCountX = (int) Math.ceil(Viewport.getScreenWidth() / Chunk.getChunkSize());
		int chunkCountY = (int) Math.ceil(Viewport.getScreenHeight() / Chunk.getChunkSize());
		int minLoadX = (int) Math.floor(-(chunkCountX + Chunk.CHUNK_LOADING) / 2.0) - 1;
		int maxLoadX = (int) Math.ceil((chunkCountX + Chunk.CHUNK_LOADING) / 2.0) + 1;
		int minLoadY = (int) Math.floor(-(chunkCountY + Chunk.CHUNK_LOADING) / 2.0) - 1;
		int maxLoadY = (int) Math.ceil((chunkCountY + Chunk.CHUNK_LOADING) / 2.0) + 1;

		for (int i = 0; i < maxChunks && !generationQueue.isEmpty(); i++) {
			ChunkIndex index = generationQueue.poll();
			if (index != null && !generatedChunks.containsKey(index)) {
				Chunk chunk = generateChunk(index);
				registerChunk(chunk);
				if (minLoadX < index.x() && index.x() <= maxLoadX && minLoadY < index.y() && index.y() <= maxLoadY) {
					loadChunk(index);
				} else {
					unloadChunk(index);
				}
			}
		}
	}

	private final void registerChunk(Chunk chunk) {
		this.generatedChunks.put(chunk.getIndex(), chunk);
	}

	private final void loadChunk(ChunkIndex index) {
		GraphicSystem.getInstance().registerDrawable(index);
		getChunk(index).ifPresent(chunk -> GraphicSystem.getInstance().registerDrawable(chunk));
		loadedChunks.put(index, true);
	}

	private final void unloadChunk(ChunkIndex index) {
		GraphicSystem.getInstance().unregisterDrawable(index);
		getChunk(index).ifPresent(chunk -> GraphicSystem.getInstance().unregisterDrawable(chunk));
		loadedChunks.put(index, false);
	}

	public final int getGeneratedChunksSize() {
		return this.generatedChunks.size();
	}

	public final int getLoadedChunksSize() {
		Collection<Boolean> values = this.loadedChunks.values();
		values.removeIf(v -> !v);
		return values.size();
	}

	public final int getGenerationQueueSize() {
		return this.generationQueue.size();
	}

	/**
	 * Updates which chunks are loaded and which chunks should be queued for generation based on the current viewport
	 */
	protected final void updateLoadedChunks() {
		ChunkIndex viewCenter = Viewport.getCenter().toWorldPos(this).toChunkIndex();

		int chunkCountX = (int) Math.ceil(Viewport.getScreenWidth() / Chunk.getChunkSize());
		int chunkCountY = (int) Math.ceil(Viewport.getScreenHeight() / Chunk.getChunkSize());

		loadedChunks.forEach((chunk, loaded) -> {
			if (loaded) {
				unloadChunk(chunk);
			}
		});

		int minGenX = (int) Math.floor(-(chunkCountX + Chunk.CHUNK_GENERATING) / 2.0) - 1;
		int maxGenX = (int) Math.ceil((chunkCountX + Chunk.CHUNK_GENERATING) / 2.0) + 1;
		int minGenY = (int) Math.floor(-(chunkCountY + Chunk.CHUNK_GENERATING) / 2.0) - 1;
		int maxGenY = (int) Math.ceil((chunkCountY + Chunk.CHUNK_GENERATING) / 2.0) + 1;

		int minLoadX = (int) Math.floor(-(chunkCountX + Chunk.CHUNK_LOADING) / 2.0) - 1;
		int maxLoadX = (int) Math.ceil((chunkCountX + Chunk.CHUNK_LOADING) / 2.0) + 1;
		int minLoadY = (int) Math.floor(-(chunkCountY + Chunk.CHUNK_LOADING) / 2.0) - 1;
		int maxLoadY = (int) Math.ceil((chunkCountY + Chunk.CHUNK_LOADING) / 2.0) + 1;

		for (int x = minGenX; x < maxGenX; x++) {
			for (int y = minGenY; y < maxGenY; y++) {
				ChunkIndex index = viewCenter.add(x, y);
				if (!generatedChunks.containsKey(index)) {
					enqueueChunkForGeneration(index);
				} else {
					if (minLoadX < x && x <= maxLoadX && minLoadY < y && y <= maxLoadY) {
						loadChunk(index);
					}
				}
			}
		}

		this.generationQueue.sort();
	}

	public abstract void init();

	/**
	 * @deprecated
	 */
	public abstract void UpdateEntityGeneration(double deltaTime);

}
