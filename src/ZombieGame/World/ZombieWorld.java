package ZombieGame.World;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.FirstAidKit;
import ZombieGame.Viewport;
import ZombieGame.ZombieType;
import ZombieGame.Algorithms.GaussianBlur;
import ZombieGame.Coordinates.ChunkIndex;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Ammunition;
import ZombieGame.Entities.AmmunitionCounter;
import ZombieGame.Entities.HeartUI;
import ZombieGame.Entities.Zombie;
import ZombieGame.Entities.ZombieCounter;
import ZombieGame.Entities.Obstacles.Tree1;
import ZombieGame.Entities.Obstacles.Tree10;
import ZombieGame.Entities.Obstacles.Tree2;
import ZombieGame.Entities.Obstacles.Tree3;
import ZombieGame.Entities.Timer;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Physic.PhysicsSystem;

public class ZombieWorld extends World {
	private double zombieTime = 0;
	private boolean debugGeneration = true;
	public final double SPAWN_INTERVAL = 1.0; // secondes
	private final double ZOMBIE_BASE_DENSITY = 1.0;
	private final double ZOMBIE_MAX_DENSITY = 10.0;
	private final double ZOMBIE_GROTH = 0.08;
	private final double CURVE = 1.8;

	public ZombieWorld() {
		super();
		// Pregenerate chunks
		final int PREGENERATE_CHUNK_X = 8;
		final int PREGENERATE_CHUNK_Y = 6;
		for (int x = -(int) (PREGENERATE_CHUNK_X / 2); x < (int) (PREGENERATE_CHUNK_X / 2); x++) {
			for (int y = -(int) (PREGENERATE_CHUNK_Y / 2); y < (int) (PREGENERATE_CHUNK_Y / 2); y++) {
				this.enqueueChunkForGeneration(new ChunkIndex(x, y));
			}
		}

		this.processGenerationQueue(this.getGenerationQueueSize());

		// add the Avatar
		this.spawnEntity(new Avatar(Viewport.getBottomCenter().sub(new Offset(0, Viewport.getScreenHeight() / 3)).toWorldPos(this)));
		this.update(0);
		this.updateLoadedChunks();
	}

	public void init() {
		this.addUIElement(new ZombieCounter(Viewport.getTopLeft().add(20, 40)));
		this.addUIElement(new Timer(Viewport.getTopCenter().add(-45, 40)));
		this.addUIElement(new HeartUI(Viewport.getBottomLeft().add(20, -56)));
		this.addUIElement(new AmmunitionCounter(Viewport.getBottomLeft().add(20, -68), 0));
	}

	public void UpdateEntityGeneration(double deltaTime) {
		this.zombieTime += deltaTime;

		if (this.zombieTime > SPAWN_INTERVAL) {
			// Select random loaded chunk
			Object[] loaded = getLoadedChunks().toArray();
			ChunkIndex index = (ChunkIndex) loaded[Math.min(loaded.length - 1, Math.max(0, (int) Math.floor(ThreadLocalRandom.current().nextDouble() * loaded.length)))];

			// Spawn zombies in it
			this.zombieTime -= SPAWN_INTERVAL * Math.max(1, this.generateEntity(index, Math.min(ZOMBIE_BASE_DENSITY * Math.pow((1 + ZOMBIE_GROTH * getWorldTimeSeconds() / 60), CURVE), ZOMBIE_MAX_DENSITY), pos -> spawnZombie(pos)));
		}
	}

	private Zombie spawnZombie(WorldPos pos) {
		Optional<Avatar> optA = this.getEntityOrPending(Avatar.class);
		if (optA.isEmpty()) {
			System.err.println("Could not find Avatar");
			return null;
		}
		Avatar avatar = optA.get();

		// if too close to Avatar, cancel
		WorldPos d2 = pos.sub(avatar.getPositionComponent().getWorldPos()).pow2();
		if (d2.x() + d2.y() < 400 * 400) {
			this.zombieTime += SPAWN_INTERVAL;
			return null;
		}

		// Pick a type: mostly BIG, some SMALL, few AXE
		ZombieType type;
		double r = Math.random();
		if (r < 0.55) {
			type = ZombieType.BIG;
		} else if (r < 0.90) {
			type = ZombieType.SMALL;
		} else {
			type = ZombieType.AXE;
		}

		// if collisions occur, cancel
		Zombie zombie = new Zombie(pos, type);
		if (PhysicsSystem.getInstance().testCollision(zombie)) {
			this.zombieTime += SPAWN_INTERVAL;
			return null;
		}

		// Increase zombie counter
		zombie.getPositionComponent().setDestination(avatar);
		Optional<ZombieCounter> optZ = this.getUIElement(ZombieCounter.class);
		if (optZ.isEmpty()) {
			System.err.println("Could not find ZombieCounter");
		}
		ZombieCounter counter = optZ.get();
		counter.increment();

		return zombie;
	}

	@Override
	public Chunk generateChunk(ChunkIndex index) {
		// radius ≤ CHUNK_SIZE / 6
		final int BLUR_RADIUS = 2;
		// sigma ≈ radius × 0.7
		final float SIGMA = 1.9f;

		int W = Chunk.DATA_SIZE + 2 * BLUR_RADIUS;
		double[][] tileData = new double[W][W];
		double[][] takeOverTileData = new double[W][W];
		Chunk currentTempChunk = new Chunk(this, index);

		// Fill with random values
		for (int y = 0; y < tileData.length; y++) {
			boolean isTopChunk = y < BLUR_RADIUS + 1;
			boolean isBottomChunk = y >= Chunk.DATA_SIZE + BLUR_RADIUS - 1;

			for (int x = 0; x < tileData[y].length; x++) {
				boolean isLeftChunk = x < BLUR_RADIUS + 1;
				boolean isRightChunk = x >= Chunk.DATA_SIZE + BLUR_RADIUS - 1;

				// Getting the tile from chunk to the side to fill the existing values in
				if (isTopChunk || isBottomChunk || isLeftChunk || isRightChunk) {
					int offsetY = -BLUR_RADIUS;
					int offsetX = -BLUR_RADIUS;
					if (isTopChunk) {
						offsetY = -(BLUR_RADIUS + 1);
					} else if (isBottomChunk) {
						offsetY = 1;
					}

					if (isLeftChunk) {
						offsetX = -(BLUR_RADIUS + 1);
					} else if (isRightChunk) {
						offsetX = 1;
					}

					TileType tileType = currentTempChunk.getTile(x + offsetX, y + offsetY).orElse(null);

					if (tileType != null) {
						tileData[y][x] = tileType.getValue();
						takeOverTileData[y][x] = tileType.getValue();
						continue;
					}
				}

				tileData[y][x] = ThreadLocalRandom.current().nextDouble();
				takeOverTileData[y][x] = Double.NaN;
			}
		}

		if (debugGeneration) {
			GraphicSystem.getInstance().saveAsGreyScaleImage(tileData, tileData.length, tileData[0].length, String.format("ChunkGeneration/rng/%d_%d_chunk_rng.png", index.x(), index.y()));
		}

		// Smooth the random values
		tileData = GaussianBlur.blur(tileData, BLUR_RADIUS, SIGMA);

		if (debugGeneration) {
			GraphicSystem.getInstance().saveAsGreyScaleImage(tileData, tileData.length, tileData[0].length, String.format("ChunkGeneration/blur/%d_%d_chunk_blur.png", index.x(), index.y()));
		}

		// Find min and max of the blurred map
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int y = 0; y < tileData.length; y++) {
			for (int x = 0; x < tileData[y].length; x++) {
				double v = tileData[y][x];
				if (v < min) {
					min = v;
				}
				if (v > max) {
					max = v;
				}
			}
		}

		double[][] t = new double[Chunk.DATA_SIZE][Chunk.DATA_SIZE];
		TileType[][] tiles = new TileType[Chunk.DATA_SIZE][Chunk.DATA_SIZE];
		for (int y = 0; y < tiles.length; y++) {
			for (int x = 0; x < tiles[y].length; x++) {
				if (Double.isFinite(takeOverTileData[y + BLUR_RADIUS][x + BLUR_RADIUS])) {
					// If take over data from other chunk exists use it
					tiles[y][x] = TileType.select(takeOverTileData[y + BLUR_RADIUS][x + BLUR_RADIUS]);
					t[y][x] = tiles[y][x].getValue();
				} else {
					// Map everything to 0..1
					tileData[y + BLUR_RADIUS][x + BLUR_RADIUS] = (tileData[y + BLUR_RADIUS][x + BLUR_RADIUS] - min) / (max - min);

					// Convert the random values to TileTypes
					tiles[y][x] = TileType.select(tileData[y + BLUR_RADIUS][x + BLUR_RADIUS]);
					t[y][x] = tiles[y][x].getValue();
				}
			}
		}

		if (debugGeneration) {
			GraphicSystem.getInstance().saveAsGreyScaleImage(tileData, tileData.length, tileData[0].length, String.format("ChunkGeneration/normalized/%d_%d_chunk_normalized.png", index.x(), index.y()));
		}

		if (debugGeneration) {
			GraphicSystem.getInstance().saveAsGreyScaleImage(t, t.length, t[0].length, String.format("ChunkGeneration/tiles/%d_%d_chunk_tiles.png", index.x(), index.y()));
		}

		// Generate tree in chunk
		this.generateEntity(index, 3, pos -> new Tree1(pos));
		this.generateEntity(index, 3, pos -> new Tree2(pos));
		this.generateEntity(index, 3, pos -> new Tree3(pos));
		this.generateEntity(index, 3, pos -> new Tree10(pos));

		// Add loot
		this.generateEntity(index, 0.25, pos -> new Ammunition(pos));
		this.generateEntity(index, 0.2, pos -> new FirstAidKit(pos));

		return new Chunk(this, index, tiles);
	}
}
