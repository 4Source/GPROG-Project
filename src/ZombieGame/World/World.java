package ZombieGame.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import ZombieGame.Viewport;
import ZombieGame.Algorithms.PoissonSampling;
import ZombieGame.Capabilities.DebuggableGeometry;
import ZombieGame.Capabilities.DebuggableText;
import ZombieGame.Capabilities.Drawable;
import ZombieGame.Components.Component;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Coordinates.ChunkIndex;
import ZombieGame.Coordinates.ChunkLocalPos;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.DataStructures.UniquePriorityQueue;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.UIElement;
import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;
import ZombieGame.Systems.Debug.DebugSystem;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Physic.PhysicsSystem;

record ChunkMove(Entity entity, ChunkIndex oldChunk) {
}

public abstract class World implements DebuggableText {
	// if game is over
	public boolean gameOver;
	private long lastChunkGenerationTime = -1;
	private double worldTimeSeconds = 0;

	private Viewport viewport = new Viewport();

	// all objects in the game, including the Avatar
	private HashMap<ChunkIndex, Set<Entity>> entities = new HashMap<>();
	private ArrayList<UIElement> uiElements = new ArrayList<>();
	private ArrayList<Entity> pendingAdditions = new ArrayList<>();
	private ArrayList<Entity> pendingRemovals = new ArrayList<>();
	private ArrayList<ChunkMove> pendingChunkMoves = new ArrayList<>();
	private final HashMap<ChunkIndex, Chunk> generatedChunks = new HashMap<>();
	private final HashMap<ChunkIndex, Boolean> loadedChunks = new HashMap<>();
	private final UniquePriorityQueue<ChunkIndex> generationQueue = new UniquePriorityQueue<>(new ChunkDistanceComparator(this));

	protected World() {
		if (!DebugSystem.getInstance().registerDebuggable(this)) {
			System.err.println("Failed to register World to debug system");
		}
		this.gameOver = false;
	}

	/**
	 * Add an entity to the world.
	 * <ul>
	 * <li><b>Register components of entity to systems:</b>
	 * <ul>
	 * <li>PhysicsComponent → PhysicsSystem</li>
	 * <li>Drawable → GraphicSystem</li>
	 * <li>DebuggableGeometry → DebugSystem</li>
	 * <li>DebuggableText → DebugSystem</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param entity The entity which should be added to the world
	 */
	public final void spawnEntity(Entity entity) {
		pendingAdditions.add(entity);
	}

	/**
	 * Remove an entity from the world.
	 * <ul>
	 * <li><b>Unregister components of entity from systems:</b>
	 * <ul>
	 * <li>PhysicsComponent → PhysicsSystem</li>
	 * <li>Drawable → GraphicSystem</li>
	 * <li>DebuggableGeometry → DebugSystem</li>
	 * <li>DebuggableText → DebugSystem</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param entity The entity which should be removed from the world
	 */
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
		Iterator<Entity> additionsIterator = pendingAdditions.iterator();
		while (additionsIterator.hasNext()) {
			Entity e = additionsIterator.next();

			this.entities.computeIfAbsent(e.getPositionComponent().getWorldPos().toChunkIndex(), s -> new HashSet<>()).add(e);
			this.registerEntityComponents(e);
			additionsIterator.remove();
		}

		// Remove entities
		Iterator<Entity> removalsIterator = pendingRemovals.iterator();
		while (removalsIterator.hasNext()) {
			Entity e = removalsIterator.next();

			Set<Entity> set = this.entities.get(e.getPositionComponent().getWorldPos().toChunkIndex());
			if (set != null) {
				set.remove(e);
			}

			removalsIterator.remove();
		}

		// Chunk switches of entities
		Iterator<ChunkMove> chunkMovesIterator = pendingChunkMoves.iterator();
		while (chunkMovesIterator.hasNext()) {
			ChunkMove move = chunkMovesIterator.next();

			if (move.oldChunk() != null) {
				Set<Entity> oldSet = this.entities.get(move.oldChunk());
				if (oldSet != null) {
					oldSet.remove(move.entity());
				}
			}

			this.entities.computeIfAbsent(move.entity().getPositionComponent().getWorldPos().toChunkIndex(), s -> new HashSet<>()).add(move.entity());

			chunkMovesIterator.remove();
		}

		this.worldTimeSeconds += deltaTime;
	}

	public final double getWorldTimeSeconds() {
		return this.worldTimeSeconds;
	}

	private final void registerEntityComponents(Entity entity) {
		entity.getComponents(PhysicsComponent.class).forEach(c -> {
			if (!PhysicsSystem.getInstance().registerComponent(c)) {
				System.err.println(String.format("Failed to register %s to physics system", entity.toString()));
			}
		});
		entity.getComponentsByCapability(Drawable.class).forEach(c -> {
			if (!GraphicSystem.getInstance().registerDrawable(c)) {
				System.err.println(String.format("Failed to register %s to graphics system", entity.toString()));
			}
		});
		entity.getComponentsByCapability(DebuggableGeometry.class).forEach(c -> {
			if (!DebugSystem.getInstance().registerDebuggable(c)) {
				System.err.println(String.format("Failed to register %s to debug system", entity.toString()));
			}
		});
		entity.getComponentsByCapability(DebuggableText.class).forEach(c -> {
			if (!DebugSystem.getInstance().registerDebuggable(c)) {
				System.err.println(String.format("Failed to register %s to debug system", entity.toString()));
			}
		});
	}

	private final void unregisterEntityComponents(Entity entity) {
		entity.getComponents(PhysicsComponent.class).forEach(c -> {
			if (!PhysicsSystem.getInstance().unregisterComponent(c)) {
				System.err.println(String.format("Failed to unregister %s from physics system", entity.toString()));
			}
		});
		entity.getComponentsByCapability(Drawable.class).forEach(c -> {
			if (!GraphicSystem.getInstance().unregisterDrawable(c)) {
				System.err.println(String.format("Failed to unregister %s from graphics system", entity.toString()));
			}
		});
		entity.getComponentsByCapability(DebuggableGeometry.class).forEach(c -> {
			if (!DebugSystem.getInstance().unregisterDebuggable(c)) {
				System.err.println(String.format("Failed to unregister %s from debug system", entity.toString()));
			}
		});
		entity.getComponentsByCapability(DebuggableText.class).forEach(c -> {
			if (!DebugSystem.getInstance().unregisterDebuggable(c)) {
				System.err.println(String.format("Failed to unregister %s from debug system", entity.toString()));
			}
		});
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
		for (Entry<ChunkIndex, Set<Entity>> entry : this.entities.entrySet()) {
			Set<Entity> entitiesInChunk = entry.getValue();
			for (Entity entity : entitiesInChunk) {
				if (!entity.getComponents(type).isEmpty()) {
					elements.add(entity);
				}
			}
		}

		return Collections.unmodifiableCollection(elements);
	}

	/**
	 * Returns all entities which are contained inside the chunks
	 * 
	 * @param <T> A Class which extends the Component Class
	 * @param index At least one chunk index to look for entities
	 * @param more More chunks indices to look for entities
	 */
	public final <T extends Component> Collection<Entity> getEntitiesInChunk(ChunkIndex index, ChunkIndex... more) {
		ArrayList<ChunkIndex> searchedChunks = new ArrayList<>();
		searchedChunks.add(index);
		searchedChunks.addAll(Arrays.asList(more));

		ArrayList<Entity> elements = new ArrayList<>();
		for (Entry<ChunkIndex, Set<Entity>> entry : this.entities.entrySet()) {
			ChunkIndex i = entry.getKey();
			if (searchedChunks.contains(i)) {
				elements.addAll(entry.getValue());
			}
		}

		return Collections.unmodifiableCollection(elements);
	}

	public final void onEntityChunkChanged(Entity entity, ChunkIndex oldChunkIndex) {
		this.pendingChunkMoves.add(new ChunkMove(entity, oldChunkIndex));
	}

	/**
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#getUIElement(Class)}
	 * 
	 * @param <T> A Class which extends the Entity Class
	 * @param type The type of Entity which should be returned
	 */
	public final <T extends Entity> Optional<T> getEntity(Class<T> type) {
		for (Entry<ChunkIndex, Set<Entity>> entry : this.entities.entrySet()) {
			Set<Entity> entitiesInChunk = entry.getValue();
			for (Entity entity : entitiesInChunk) {
				if (type.isInstance(entity)) {
					return Optional.of(type.cast(entity));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#getUIElement(Class)}
	 * 
	 * @param <T> A Class which extends the Entity Class
	 * @param type The type of Entity which should be returned
	 */
	public final <T extends Entity> Optional<T> getEntityOrPending(Class<T> type) {
		for (Entry<ChunkIndex, Set<Entity>> entry : this.entities.entrySet()) {
			Set<Entity> entitiesInChunk = entry.getValue();
			for (Entity entity : entitiesInChunk) {
				if (type.isInstance(entity)) {
					return Optional.of(type.cast(entity));
				}
			}
		}
		for (Entity e : this.pendingAdditions) {
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
			private final Iterator<Set<Entity>> chunksIt = entities.values().iterator();
			private Iterator<Entity> entitiesInChunkIt = null;
			private Entity last = null;

			@Override
			public boolean hasNext() {
				// Loop until we find a chunk with remaining entities
				while ((entitiesInChunkIt == null || !entitiesInChunkIt.hasNext()) && chunksIt.hasNext()) {
					entitiesInChunkIt = chunksIt.next().iterator();
				}
				return entitiesInChunkIt != null && entitiesInChunkIt.hasNext();
			}

			@Override
			public Entity next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				last = entitiesInChunkIt.next();
				return last;
			}

			@Override
			public void remove() {
				if (last == null) {
					throw new IllegalStateException("remove() called before next()");
				}
				unregisterEntityComponents(last);
				entitiesInChunkIt.remove();
				last = null;
			}
		};
	}

	/**
	 * Returns an iterator over the entities in the loaded chunks only.
	 * <p>
	 * Ensure side effects.
	 * <p>
	 * For {@link UIElement UIElements} use {@link World#uiElementIterator()}
	 */
	public Iterator<Entity> loadedEntityIterator() {
		return new Iterator<>() {
			private final Iterator<Map.Entry<ChunkIndex, Set<Entity>>> chunkIt = entities.entrySet().iterator();
			private Iterator<Entity> entitiesInChunkIt = null;
			private Entity last = null;

			@Override
			public boolean hasNext() {
				// Loop until we find a loaded chunk with remaining entities
				while ((entitiesInChunkIt == null || !entitiesInChunkIt.hasNext()) && chunkIt.hasNext()) {
					Map.Entry<ChunkIndex, Set<Entity>> entry = chunkIt.next();
					ChunkIndex chunk = entry.getKey();
					if (Boolean.TRUE.equals(loadedChunks.get(chunk))) {
						entitiesInChunkIt = entry.getValue().iterator();
					} else {
						entitiesInChunkIt = null;
					}
				}
				return entitiesInChunkIt != null && entitiesInChunkIt.hasNext();
			}

			@Override
			public Entity next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				last = entitiesInChunkIt.next();
				return last;
			}

			@Override
			public void remove() {
				if (last == null) {
					throw new IllegalStateException("remove() called before next()");
				}
				unregisterEntityComponents(last);
				entitiesInChunkIt.remove();
				last = null;
			}
		};
	}

	private final void registerUIElementComponents(UIElement uiElement) {
		uiElement.getComponentsByCapability(Drawable.class).forEach(c -> {
			if (!GraphicSystem.getInstance().registerDrawable(c)) {
				System.err.println(String.format("Failed to register %s to graphics system", uiElement.toString()));
			}
		});
		uiElement.getComponentsByCapability(DebuggableGeometry.class).forEach(c -> {
			if (!DebugSystem.getInstance().registerDebuggable(c)) {
				System.err.println(String.format("Failed to register %s to debug system", uiElement.toString()));
			}
		});
		uiElement.getComponentsByCapability(DebuggableText.class).forEach(c -> {
			if (!DebugSystem.getInstance().registerDebuggable(c)) {
				System.err.println(String.format("Failed to register %s to debug system", uiElement.toString()));
			}
		});
	}

	private final void unregisterUIElementComponents(UIElement uiElement) {
		uiElement.getComponentsByCapability(Drawable.class).forEach(c -> {
			if (!GraphicSystem.getInstance().unregisterDrawable(c)) {
				System.err.println(String.format("Failed to unregister %s from graphics system", uiElement.toString()));
			}
		});
		uiElement.getComponentsByCapability(DebuggableGeometry.class).forEach(c -> {
			if (!DebugSystem.getInstance().unregisterDebuggable(c)) {
				System.err.println(String.format("Failed to unregister %s from debug system", uiElement.toString()));
			}
		});
		uiElement.getComponentsByCapability(DebuggableText.class).forEach(c -> {
			if (!DebugSystem.getInstance().unregisterDebuggable(c)) {
				System.err.println(String.format("Failed to unregister %s from debug system", uiElement.toString()));
			}
		});
	}

	/**
	 * Add an ui element to the world.
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

	/**
	 * Remove an ui element from the world.
	 * <p>
	 * For {@link Entity Entities} use {@link World#removeEntity(Entity)}
	 * <ul>
	 * <li><b>Unregister components of ui element from systems:</b>
	 * <ul>
	 * <li>Drawable → GraphicSystem</li>
	 * <li>DebuggableGeometry → DebugSystem</li>
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
	 * <li>DebuggableText → DebugSystem</li>
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

				unregisterUIElementComponents(last);
				it.remove();
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

	/**
	 * Generates number of entities based on the size of the chunks and density
	 * 
	 * @param <T> A Type of Entity
	 * @param index The index of the chunk where the entities should be spawned
	 * @param density The density determines how many entities should be spawned in average for a chunk size of 8 scales with chunk size
	 * @param createCallback A callback to create a instance of the entity. Return the entity to spawn or {@code null} if the current spawn should be abort
	 * @return The amount of generated entities
	 */
	protected final <T extends Entity> int generateEntity(ChunkIndex index, double density, Function<WorldPos, T> createCallback) {
		double lambda = Chunk.CHUNK_SIZE / 8 * density;

		String className = "Unknown";

		int spawnCount = PoissonSampling.sample(lambda);

		// Retry for 1/4 of the requested entity count but at least once, prevents infinity loops
		int retry = Math.max(1, spawnCount / 4);
		int amount = 0;

		for (int i = 0; i < spawnCount; i++) {
			ChunkLocalPos pos = new ChunkLocalPos(ThreadLocalRandom.current().nextDouble() * Chunk.getChunkSize(), ThreadLocalRandom.current().nextDouble() * Chunk.getChunkSize());

			T entity = createCallback.apply(pos.toWorldPos(index));

			// Spawn canceled by caller
			if (entity == null) {
				continue;
			}

			// Retrieve class name for logging
			if (className == "Unknown") {
				className = entity.getClass().getName();
			}

			// if collisions occur, cancel
			if (PhysicsSystem.getInstance().testCollision(entity)) {
				i--;
				retry--;
				if (retry > 0) {
					continue;
				} else {
					break;
				}
			}

			this.spawnEntity(entity);

			// Add the entity so it is registered in physics system so it could be used for future collisions checks
			this.update(0);
			amount++;
		}

		// Retrieve class name for logging
		if (className == "Unknown") {
			T entity = createCallback.apply(new WorldPos());
			if (entity != null) {
				className = entity.getClass().getName();
			}
		}

		// System.out.println(String.format("Generate %s with density %f (%d/%d) in chunk %s", className, density, amount, spawnCount, index.toString()));
		return amount;
	}

	public final Optional<Chunk> getChunk(ChunkIndex coord) {
		return Optional.ofNullable(this.generatedChunks.get(coord));
	}

	public final boolean isChunkGenerated(ChunkIndex index) {
		return this.generatedChunks.containsKey(index);
	}

	public final Set<ChunkIndex> getGeneratedChunks() {
		return this.generatedChunks.keySet();
	}

	public final boolean isChunkLoaded(ChunkIndex index) {
		return this.loadedChunks.getOrDefault(index, false);
	}

	public final Set<ChunkIndex> getLoadedChunks() {
		return this.loadedChunks.keySet();
	}

	public final boolean isChunkQueuedForGeneration(ChunkIndex index) {
		return this.generationQueue.contains(index);
	}

	public final Set<ChunkIndex> getGenerationQueue() {
		ChunkIndex[] t = new ChunkIndex[1];
		ChunkIndex[] q = this.generationQueue.toArray(t);
		HashSet<ChunkIndex> res = new HashSet<>();
		for (ChunkIndex i : q) {
			res.add(i);
		}
		return res;
	}

	/**
	 * Schedule a chunk to be generated
	 * 
	 * @param index The pos for which a chunk should be generated
	 */
	public final void enqueueChunkForGeneration(ChunkIndex index) {
		this.generationQueue.add(index);
		if (!DebugSystem.getInstance().registerDebuggable(index)) {
			System.err.println(String.format("Failed to register chunk index %s to debug system", index.toString()));
		}
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

		long start;
		for (int i = 0; i < maxChunks && !generationQueue.isEmpty(); i++) {
			ChunkIndex index = generationQueue.poll();
			if (index != null && !generatedChunks.containsKey(index)) {
				start = System.currentTimeMillis();
				Chunk chunk = generateChunk(index);
				this.lastChunkGenerationTime = (System.currentTimeMillis() - start);
				if (!registerChunk(chunk)) {
					System.err.println(String.format("Failed to register chunk %s to world", index.toString()));
				}
				if (minLoadX < index.x() && index.x() <= maxLoadX && minLoadY < index.y() && index.y() <= maxLoadY) {
					loadChunk(index);
				} else {
					if (!DebugSystem.getInstance().unregisterDebuggable(index)) {
						System.err.println(String.format("Failed to unregister chunk index %s from debug system", index.toString()));
					}
				}
			}
		}
	}

	/**
	 * Register the chunk to the generated chunks
	 * 
	 * @param chunk The chunk to register
	 * @return {@code true} if the registration was successful or if it was already registered
	 */
	private final boolean registerChunk(Chunk chunk) {
		Chunk existing = generatedChunks.putIfAbsent(chunk.getIndex(), chunk);
		return existing == null || existing == chunk;
	}

	private final void loadChunk(ChunkIndex index) {
		if (!DebugSystem.getInstance().registerDebuggable(index)) {
			System.err.println(String.format("Failed to register chunk index %s to debug system", index.toString()));
		}
		getChunk(index).ifPresent(chunk -> {
			if (!DebugSystem.getInstance().registerDebuggable(chunk)) {
				System.err.println(String.format("Failed to register chunk %s to debug system", index.toString()));
			}
			if (!GraphicSystem.getInstance().registerDrawable(chunk)) {
				System.err.println(String.format("Failed to register chunk %s to graphic system", index.toString()));
			}
		});
		loadedChunks.put(index, true);

		getEntitiesInChunk(index).forEach(entity -> {
			registerEntityComponents(entity);
		});
	}

	private final void unloadChunk(ChunkIndex index) {
		if (!DebugSystem.getInstance().unregisterDebuggable(index)) {
			System.err.println(String.format("Failed to unregister chunk index %s from debug system", index.toString()));
		}
		getChunk(index).ifPresent(chunk -> {
			if (!DebugSystem.getInstance().unregisterDebuggable(chunk)) {
				System.err.println(String.format("Failed to unregister chunk %s from debug system", index.toString()));
			}
			if (!GraphicSystem.getInstance().unregisterDrawable(chunk)) {
				System.err.println(String.format("Failed to unregister chunk %s from graphic system", index.toString()));
			}
		});
		loadedChunks.put(index, false);

		getEntitiesInChunk(index).forEach(entity -> {
			unregisterEntityComponents(entity);
		});
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

		int minGenX = (int) Math.floor(-(chunkCountX + Chunk.CHUNK_GENERATING) / 2.0) - 1;
		int maxGenX = (int) Math.ceil((chunkCountX + Chunk.CHUNK_GENERATING) / 2.0) + 1;
		int minGenY = (int) Math.floor(-(chunkCountY + Chunk.CHUNK_GENERATING) / 2.0) - 1;
		int maxGenY = (int) Math.ceil((chunkCountY + Chunk.CHUNK_GENERATING) / 2.0) + 1;

		int minLoadX = (int) Math.floor(-(chunkCountX + Chunk.CHUNK_LOADING) / 2.0) - 1;
		int maxLoadX = (int) Math.ceil((chunkCountX + Chunk.CHUNK_LOADING) / 2.0) + 1;
		int minLoadY = (int) Math.floor(-(chunkCountY + Chunk.CHUNK_LOADING) / 2.0) - 1;
		int maxLoadY = (int) Math.ceil((chunkCountY + Chunk.CHUNK_LOADING) / 2.0) + 1;

		// Enqueue chunks for generation if there are not generated but should be and collect chunks which should be loaded
		HashSet<ChunkIndex> desired = new HashSet<>();
		for (int x = minGenX; x < maxGenX; x++) {
			for (int y = minGenY; y < maxGenY; y++) {
				ChunkIndex index = viewCenter.add(x, y);
				// Chunk is not generated yet
				if (!generatedChunks.containsKey(index)) {
					enqueueChunkForGeneration(index);
				}
				// Chunk is already generated check if it should be loaded or not
				else {
					// Chunk is in range and should be loaded
					if (minLoadX < x && x <= maxLoadX && minLoadY < y && y <= maxLoadY) {
						desired.add(index);
					}
				}
			}
		}

		// Unload chunks which should be no longer loaded
		Iterator<ChunkIndex> loadedIterator = this.loadedChunks.keySet().iterator();
		while (loadedIterator.hasNext()) {
			ChunkIndex index = loadedIterator.next();

			if (!desired.contains(index)) {
				unloadChunk(index);
				loadedIterator.remove();
			}
		}

		// Load missing chunks which should be loaded
		for (ChunkIndex index : desired) {
			if (!this.loadedChunks.containsKey(index)) {
				loadChunk(index);
			}
		}

		this.generationQueue.sort();
	}

	public abstract void init();

	public abstract void UpdateEntityGeneration(double deltaTime);

	@Override
	public DebugCategoryMask getCategoryMask() {
		return new DebugCategoryMask(DebugCategory.PERFORMANCE, DebugCategory.WORLD);
	}

	@Override
	public ArrayList<String> getTextElements() {
		ArrayList<String> elements = new ArrayList<>();

		int entityCount = 0;
		for (Set<Entity> chunk : this.entities.values()) {
			entityCount += chunk.size();
		}

		elements.add(String.format("Chunks Loaded: %d", this.getLoadedChunksSize()));
		elements.add(String.format("Chunks Generated: %d", this.getGeneratedChunksSize()));
		elements.add(String.format("Chunks Queued: %d", this.getGenerationQueueSize()));
		elements.add(String.format("Generation time: %d ms", this.lastChunkGenerationTime));
		elements.add(String.format("Entities registered: %d", entityCount));
		elements.add(String.format("Entities add queue: %d", this.pendingAdditions.size()));
		elements.add(String.format("Entities remove queue: %d", this.pendingRemovals.size()));
		elements.add(String.format("Entities chunk switch queue: %d", this.pendingChunkMoves.size()));
		return elements;
	}
}
