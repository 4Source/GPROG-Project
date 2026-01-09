package ZombieGame;

// (c) Thorsten Hasbargen

import java.util.Optional;

import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Ammunition;
import ZombieGame.Entities.AmmunitionCounter;
import ZombieGame.Entities.HelpText;
import ZombieGame.Entities.Tree;
import ZombieGame.Entities.Zombie;
import ZombieGame.Entities.ZombieCounter;

public class ZombieWorld extends World {
	private double timePassed = 0;

	// for grenades
	private double spawnAmmunition = 0;

	protected void init() {
		// add the Avatar
		this.spawnEntity(new Avatar(2500, 2000));

		// set WorldPart position
		this.worldPartX = 1500;
		this.worldPartY = 1500;

		// add a little forrest
		for (int x = 0; x < 5000; x += 1000) {
			for (int y = 0; y < 4000; y += 800) {
				this.spawnEntity(new Tree(x + 300, y + 200));
				this.spawnEntity(new Tree(x + 600, y + 370));
				this.spawnEntity(new Tree(x + 200, y + 600));
				this.spawnEntity(new Tree(x + 500, y + 800));
				this.spawnEntity(new Tree(x + 900, y + 500));
				this.spawnEntity(new Tree(x + 760, y + 160));
			}
		}

		// add one zombie
		this.spawnEntity(new Zombie(100, 100));

		this.addUIElement(new ZombieCounter(20, 40));
		this.addUIElement(new AmmunitionCounter(770, 40, 0));
		this.addUIElement(new HelpText(100, 400, 10.0));
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
			double dx = x - avatar.getPosX();
			double dy = y - avatar.getPosY();
			if (dx * dx + dy * dy < 200 * 200) {
				this.spawnAmmunition = INTERVAL;
				return;
			}

			// if collisions occur, cancel
			Ammunition grenade = new Ammunition(x, y);
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
			double dx = x - avatar.getPosX();
			double dy = y - avatar.getPosY();
			if (dx * dx + dy * dy < 400 * 400) {
				this.timePassed = INTERVAL;
				return;
			}

			// if collisions occur, cancel
			Zombie zombie = new Zombie(x, y);
			if (PhysicsSystem.getInstance().testCollision(zombie)) {
				this.timePassed = INTERVAL;
				return;
			}

			// else add zombie to world
			this.spawnEntity(zombie);
			zombie.getMovementComponent().setDestination(avatar);
			Optional<ZombieCounter> optZ = this.getUIElement(ZombieCounter.class);
			if (optZ.isEmpty()) {
				System.err.println("Could not find ZombieCounter");
			}
			ZombieCounter counter = optZ.get();
			counter.increment();
		}

	}
}
