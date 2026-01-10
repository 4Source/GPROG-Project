package ZombieGame;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import ZombieGame.Entities.Avatar;
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
import ZombieGame.Entities.ZombieType;

public class ZombieWorld extends World {
	private double timePassed = 0;

	// for grenades
	private double spawnAmmunition = 0;

	protected void init() {
		// add the Avatar
		this.spawnEntity(new Avatar(new WorldPos(0, 0)));

		// set WorldPart position
		this.worldPartX = 0;
		this.worldPartY = 0;

		// add a little forrest
		for (int x = 0; x < 5000; x += 1000) {
			for (int y = 0; y < 4000; y += 800) {
				this.spawnEntity(new Tree(new WorldPos(x + 300, y + 200)));
				this.spawnEntity(new Tree(new WorldPos(x + 600, y + 370)));
				this.spawnEntity(new Tree(new WorldPos(x + 200, y + 600)));
				this.spawnEntity(new Tree(new WorldPos(x + 500, y + 800)));
				this.spawnEntity(new Tree(new WorldPos(x + 900, y + 500)));
				this.spawnEntity(new Tree(new WorldPos(x + 760, y + 160)));
			}
		}

		// add one zombie
		this.spawnEntity(new Zombie(new WorldPos(100, 100), ZombieType.BIG));

		this.addUIElement(new ZombieCounter(new ViewPos(345, 40)));
		this.addUIElement(new HeartUI(new ViewPos(20, 20)));
		this.addUIElement(new AmmunitionCounter(new ViewPos(770, 40), 0));
		this.addUIElement(new HelpText(new ViewPos(100, 400), 10.0));

		final int PREGENERATE_CHUNK_X = 16;
		final int PREGENERATE_CHUNK_Y = 9;
		for (int x = - (int) (PREGENERATE_CHUNK_X / 2); x < (int) (PREGENERATE_CHUNK_X / 2); x++) {
			for (int y = - (int) (PREGENERATE_CHUNK_Y / 2); y < (int) (PREGENERATE_CHUNK_Y / 2); y++) {
				this.addChunk(this.generateChunk(new ChunkIndex(x, y)));
			}
		}
	}

	protected void createNewObjects(double deltaTime) {
		createZombie(deltaTime);
		createAmmunition(deltaTime);
	}

	private void createAmmunition(double deltaTime) {
		final double INTERVAL = Constants.SPAWN_GRENADE;

		this.spawnAmmunition += deltaTime;
		if (this.spawnAmmunition > INTERVAL) {
			this.spawnAmmunition -= INTERVAL;

			// create new Ammunition
			double x = this.worldPartX + Math.random() * Constants.WORLDPART_WIDTH;
			double y = this.worldPartY + Math.random() * Constants.WORLDPART_HEIGHT;

			Optional<Avatar> opt = this.getEntity(Avatar.class);
			if (opt.isEmpty()) {
				System.err.println("No avatar found");
				return;
			}
			Avatar avatar = opt.get();

			// if too close to Avatar, cancel
			double dx = x - avatar.getPositionComponent().getWorldPos().x();
			double dy = y - avatar.getPositionComponent().getWorldPos().y();
			if (dx * dx + dy * dy < 200 * 200) {
				this.spawnAmmunition = INTERVAL;
				return;
			}

			// if collisions occur, cancel
			Ammunition grenade = new Ammunition(new WorldPos(x, y));
			if (PhysicsSystem.getInstance().testCollision(grenade)) {
				this.spawnAmmunition = INTERVAL;
				return;
			}

			// else add zombie to world
			this.spawnEntity(grenade);
			// this.counterG.setNumber(this.grenades);
		}

	}

	private void createZombie(double deltaTime) {
		final double INTERVAL = Constants.SPAWN_INTERVAL;

		this.timePassed += deltaTime;
		if (this.timePassed > INTERVAL) {
			this.timePassed -= INTERVAL;

			// create new Zombie; preference to current screen
			double x, y;
			if (Math.random() < 0.7) {
				x = Math.random() * Constants.WORLD_WIDTH;
				y = Math.random() * Constants.WORLD_HEIGHT;
			} else {
				x = this.worldPartX + Math.random() * Constants.WORLDPART_WIDTH;
				y = this.worldPartY + Math.random() * Constants.WORLDPART_HEIGHT;
			}

			Optional<Avatar> optA = this.getEntity(Avatar.class);
			if (optA.isEmpty()) {
				System.err.println("Could not find Avatar");
				return;
			}
			Avatar avatar = optA.get();

			// if too close to Avatar, cancel
			double dx = x - avatar.getPositionComponent().getWorldPos().x();
			double dy = y - avatar.getPositionComponent().getWorldPos().y();
			if (dx * dx + dy * dy < 400 * 400) {
				this.timePassed = INTERVAL;
				return;
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
			Zombie zombie = new Zombie(new WorldPos(x, y), type);
			if (PhysicsSystem.getInstance().testCollision(zombie)) {
				this.timePassed = INTERVAL;
				return;
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
	public Chunk generateChunk(ChunkIndex coord) {
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

		System.out.println("Chunk generation time (ms): " + (System.currentTimeMillis() - start));
		return new Chunk(this, coord, tiles);
	}
}
