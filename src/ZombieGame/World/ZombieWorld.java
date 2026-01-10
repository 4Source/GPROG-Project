package ZombieGame.World;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import ZombieGame.Entities.Avatar;
import ZombieGame.Constants;
import ZombieGame.Viewport;
import ZombieGame.Algorithms.GaussianBlur;
import ZombieGame.Coordinates.ChunkIndex;
import ZombieGame.Coordinates.ChunkLocalPos;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Ammunition;
import ZombieGame.Entities.AmmunitionCounter;
import ZombieGame.Entities.HeartUI;
import ZombieGame.Entities.HelpText;
import ZombieGame.Entities.Tree;
import ZombieGame.Entities.Zombie;
import ZombieGame.Entities.ZombieCounter;
import ZombieGame.Entities.ZombieType;
import ZombieGame.Systems.Physic.PhysicsSystem;

public class ZombieWorld extends World {
	private double zombieTime = 0;

	// for grenades
	private double ammunitionTime = 0;

	public void init() {
		// add the Avatar
		this.spawnEntity(new Avatar(Viewport.getCenter().toWorldPos(this)));

		this.addUIElement(new ZombieCounter(Viewport.getTopRight()));
		this.addUIElement(new HeartUI(Viewport.getBottomLeft()));
		this.addUIElement(new AmmunitionCounter(Viewport.getBottomLeft().add(100, 0), 0));
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
		this.ammunitionTime += deltaTime;
		this.zombieTime += deltaTime;
		// this.spawnZombies();
	}

	private void spawnZombies() {
		// Spawn up to 10 Zombies
		for (int i = 0; i < 10 && this.zombieTime > Constants.SPAWN_INTERVAL; i++) {
			this.zombieTime -= Constants.SPAWN_INTERVAL;

			WorldPos pos = Viewport.getCenter().toWorldPos(this).add((ThreadLocalRandom.current().nextDouble() - 0.5) * Viewport.getScreenWidth(), (ThreadLocalRandom.current().nextDouble() - 0.5) * Viewport.getScreenHeight());

			Optional<Avatar> optA = this.getEntity(Avatar.class);
			if (optA.isEmpty()) {
				System.err.println("Could not find Avatar");
				break;
			}
			Avatar avatar = optA.get();

			// if too close to Avatar, cancel
			WorldPos d2 = pos.sub(avatar.getPositionComponent().getWorldPos()).pow2();
			if (d2.x() + d2.y() < 400 * 400) {
				this.zombieTime += Constants.SPAWN_INTERVAL;
				break;
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
				this.zombieTime += Constants.SPAWN_INTERVAL;
				break;
			}

			// else add zombie to world
			this.spawnEntity(zombie);
			zombie.getPositionComponent().setDestination(avatar);
			Optional<ZombieCounter> optZ = this.getUIElement(ZombieCounter.class);
			if (optZ.isEmpty()) {
				System.err.println("Could not find ZombieCounter");
			}
			ZombieCounter counter = optZ.get();
			counter.increment();
		}
	}

	@Override
	public Chunk generateChunk(ChunkIndex index) {
		long start = System.currentTimeMillis();

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
				// TODO: Try getting chunks to the side to fill the current values in
				map[y][x] = ThreadLocalRandom.current().nextDouble();
			}
		}

		// Smooth the random values
		map = GaussianBlur.blur(map, BLUR_RADIUS, SIGMA);

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

				System.out.print(tiles[y][x].toString().charAt(0));
			}
			System.out.println();
		}

		// Generate tree in chunk
		{
			ChunkLocalPos pos = new ChunkLocalPos(ThreadLocalRandom.current().nextDouble() * Chunk.getChunkSize(), ThreadLocalRandom.current().nextDouble() * Chunk.getChunkSize());
			this.spawnEntity(new Tree(pos.toWorldPos(index)));
		}

		// Add loot
		while (this.ammunitionTime > Constants.SPAWN_GRENADE) {
			this.ammunitionTime -= Constants.SPAWN_GRENADE;

			WorldPos pos = new ChunkLocalPos(ThreadLocalRandom.current().nextDouble() * Chunk.getChunkSize(), ThreadLocalRandom.current().nextDouble() * Chunk.getChunkSize()).toWorldPos(index);

			Optional<Avatar> opt = this.getEntity(Avatar.class);
			if (opt.isEmpty()) {
				System.err.println("No avatar found");
				break;
			}
			Avatar avatar = opt.get();

			// if too close to Avatar, cancel
			WorldPos d2 = pos.sub(avatar.getPositionComponent().getWorldPos()).pow2();
			if (d2.x() + d2.y() < 200 * 200) {
				this.ammunitionTime += Constants.SPAWN_GRENADE;
				break;
			}

			// if collisions occur, cancel
			Ammunition ammunition = new Ammunition(pos);
			if (PhysicsSystem.getInstance().testCollision(ammunition)) {
				this.ammunitionTime = Constants.SPAWN_GRENADE;
				break;
			}

			this.spawnEntity(ammunition);
		}

		// TODO: Add zombie spawnpoints

		System.out.println("Chunk generation time (ms): " + (System.currentTimeMillis() - start));
		return new Chunk(this, index, tiles);
	}
}
