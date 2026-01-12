package ZombieGame.World;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import ZombieGame.Entities.Avatar;
import ZombieGame.Viewport;
import ZombieGame.ZombieType;
import ZombieGame.Algorithms.GaussianBlur;
import ZombieGame.Coordinates.ChunkIndex;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Ammunition;
import ZombieGame.Entities.AmmunitionCounter;
import ZombieGame.Entities.HeartUI;
import ZombieGame.Entities.HelpText;
import ZombieGame.Entities.Tree;
import ZombieGame.Entities.Zombie;
import ZombieGame.Entities.ZombieCounter;
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

	public void init() {
		// add the Avatar
		this.spawnEntity(new Avatar(Viewport.getCenter().toWorldPos(this)));

		this.addUIElement(new ZombieCounter(Viewport.getTopLeft().add(20, 40)));
		this.addUIElement(new Timer(Viewport.getTopCenter().add(-45, 40)));
		this.addUIElement(new HeartUI(Viewport.getBottomLeft().add(20, -56)));
		this.addUIElement(new AmmunitionCounter(Viewport.getBottomLeft().add(20, -68), 0));
		this.addUIElement(new HelpText(new ViewPos(100, 400), 10.0));

		// Pregenerate chunks
		final int PREGENERATE_CHUNK_X = 8;
		final int PREGENERATE_CHUNK_Y = 6;
		for (int x = -(int) (PREGENERATE_CHUNK_X / 2); x < (int) (PREGENERATE_CHUNK_X / 2); x++) {
			for (int y = -(int) (PREGENERATE_CHUNK_Y / 2); y < (int) (PREGENERATE_CHUNK_Y / 2); y++) {
				this.enqueueChunkForGeneration(new ChunkIndex(x, y));
			}
		}
		this.processGenerationQueue(this.getGenerationQueueSize());
		this.updateLoadedChunks();
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
		final int CHUNK_SIZE = Chunk.SIZE;
		// radius ≤ CHUNK_SIZE / 6
		final int BLUR_RADIUS = 1;
		// sigma ≈ radius × 0.7
		final float SIGMA = 0.55f;

		int W = CHUNK_SIZE + 2 * BLUR_RADIUS;
		double[][] map = new double[W][W];

		// Fill with random values
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				// TODO: Getting chunks to the side to fill the current values in
				map[y][x] = ThreadLocalRandom.current().nextDouble();
			}
		}

		if (debugGeneration)
			GraphicSystem.getInstance().saveAsGreyScaleImage(map, map.length, map[0].length, "chunk.png");

		// Smooth the random values
		map = GaussianBlur.blur(map, BLUR_RADIUS, SIGMA);

		if (debugGeneration)
			GraphicSystem.getInstance().saveAsGreyScaleImage(map, map.length, map[0].length, "chunk_blur.png");

		// Find min and max of the blurred map
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				double v = map[y][x];
				if (v < min)
					min = v;
				if (v > max)
					max = v;
			}
		}

		double[][] t = new double[CHUNK_SIZE][CHUNK_SIZE];
		TileType[][] tiles = new TileType[CHUNK_SIZE][CHUNK_SIZE];
		for (int y = 0; y < tiles.length; y++) {
			for (int x = 0; x < tiles[y].length; x++) {
				// Map everything to 0..1
				map[y + BLUR_RADIUS][x + BLUR_RADIUS] = (map[y + BLUR_RADIUS][x + BLUR_RADIUS] - min) / (max - min);

				// Convert the random values to TileTypes
				tiles[y][x] = TileType.select(map[y + BLUR_RADIUS][x + BLUR_RADIUS]);
				t[y][x] = tiles[y][x].getValue();

				// System.out.print(tiles[y][x].toString().charAt(0));
			}
			// System.out.println();
		}

		if (debugGeneration)
			GraphicSystem.getInstance().saveAsGreyScaleImage(map, map.length, map[0].length, "chunk_normalized.png");

		if (debugGeneration)
			GraphicSystem.getInstance().saveAsGreyScaleImage(t, t.length, t[0].length, "chunk_tiles.png");

		// Generate tree in chunk
		this.generateEntity(index, 2.5, pos -> new Tree(pos));

		// Add loot
		this.generateEntity(index, 0.1, pos -> new Ammunition(pos));

		return new Chunk(this, index, tiles);
	}
}
