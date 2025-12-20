
// (c) Thorsten Hasbargen

import java.util.Optional;

class ZombieWorld extends World {
	private double timePassed = 0;
	private double timeSinceLastShot = 0;

	// for grenades
	private double spawnGrenade = 0;

	protected void init() {
		// add the Avatar
		this.addEntity(new Avatar(2500, 2000));

		// set WorldPart position
		this.worldPartX = 1500;
		this.worldPartY = 1500;

		// add a little forrest
		for (int x = 0; x < 5000; x += 1000) {
			for (int y = 0; y < 4000; y += 800) {
				this.addEntity(new Tree(x + 300, y + 200, 80));
				this.addEntity(new Tree(x + 600, y + 370, 50));
				this.addEntity(new Tree(x + 200, y + 600, 50));
				this.addEntity(new Tree(x + 500, y + 800, 40));
				this.addEntity(new Tree(x + 900, y + 500, 100));
				this.addEntity(new Tree(x + 760, y + 160, 40));
			}
		}

		// add one zombie
		this.addEntity(new Zombie(100, 100));

		this.addUIElement(new ZombieCounter(20, 40));
		this.addUIElement(new GrenadesCounter(770, 40, 5));
		this.addUIElement(new HelpText(100, 400, 10.0));
	}

	protected void processUserInput(UserInput userInput, double deltaTime) {
		// distinguish if Avatar shall move or shoots
		int button = userInput.mouseButton;

		Optional<Avatar> opt = this.getEntity(Avatar.class);
		if (opt.isEmpty()) {
			System.err.println("No avatar found");
			return;
		}
		Avatar avatar = opt.get();

		// Mouse events
		if (userInput.isMouseEvent) {
			// move
			if (button == 1) {
				avatar.getMovementComponent().setDestination(userInput.mousePressedX + this.worldPartX, userInput.mousePressedY + this.worldPartY);
			}
		}

		// Mouse still pressed?
		if (userInput.isMousePressed && button == 3) {
			// only 1 shot every ... seconds:
			this.timeSinceLastShot += deltaTime;
			if (this.timeSinceLastShot > 0.2) {
				this.timeSinceLastShot = 0;

				Gunshot shot = new Gunshot(avatar.posX, avatar.posY, userInput.mouseMovedX + this.worldPartX, userInput.mouseMovedY + this.worldPartY);
				this.addEntity(shot);
			}
		}

		// Keyboard events
		if (userInput.isKeyEvent) {
			if (userInput.keyPressed == ' ') {
				this.throwGrenade(userInput.mouseMovedX + this.worldPartX, userInput.mouseMovedY + this.worldPartY);
			}
			// Exit game on escape
			else if (userInput.keyPressed == (char) 27) {
				System.exit(0);
			}
		}
	}

	// TODO: Why throws the world a granade ?
	/**
	 * @param posX the position in x where the grenade should be thrown to
	 * @param posY the position in y where the grenade should be thrown to
	 */
	private void throwGrenade(double posX, double posY) {
		Optional<GrenadesCounter> opt = this.getUIElement(GrenadesCounter.class);
		if (opt.isEmpty()) {
			System.err.println("Could not find GrenadesCounterText");
			return;
		}

		GrenadesCounter grenades = opt.get();
		if (grenades.getNumber() <= 0) {
			return;
		}

		// throw grenade
		for (int i = 0; i < 2000; i++) {
			double alfa = Math.random() * Math.PI * 2;
			double speed = 50 + Math.random() * 200;
			double time = 0.2 + Math.random() * 0.4;
			Gunshot shot = new Gunshot(posX, posY, alfa, speed, time);
			this.addEntity(shot);
		}

		// inform counter
		grenades.decrement();
	}

	protected void createNewObjects(double deltaTime) {
		createZombie(deltaTime);
		createGrenade(deltaTime);
	}

	private void createGrenade(double deltaTime) {
		final double INTERVAL = Constants.SPAWN_GRENADE;

		this.spawnGrenade += deltaTime;
		if (this.spawnGrenade > INTERVAL) {
			this.spawnGrenade -= INTERVAL;

			// create new Grenade
			double x = this.worldPartX + Math.random() * Constants.WORLDPART_WIDTH;
			double y = this.worldPartY + Math.random() * Constants.WORLDPART_HEIGHT;

			Optional<Avatar> opt = this.getEntity(Avatar.class);
			if (opt.isEmpty()) {
				System.err.println("No avatar found");
				return;
			}
			Avatar avatar = opt.get();

			// if too close to Avatar, cancel
			double dx = x - avatar.posX;
			double dy = y - avatar.posY;
			if (dx * dx + dy * dy < 200 * 200) {
				this.spawnGrenade = INTERVAL;
				return;
			}

			// if collisions occur, cancel
			Grenade grenade = new Grenade(x, y);
			if (PhysicsSystem.getInstance().testCollision(grenade)) {
				this.spawnGrenade = INTERVAL;
				return;
			}

			// else add zombie to world
			this.addEntity(grenade);
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
			double dx = x - avatar.posX;
			double dy = y - avatar.posY;
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
			this.addEntity(zombie);
			zombie.getMovementComponent().setDestination(avatar);
			Optional<ZombieCounter> optZ = this.getUIElement(ZombieCounter.class);
			if (optZ.isEmpty()) {
				System.err.println("Could not find ZombieCounter");
			}
			ZombieCounter counter = optZ.get();
			counter.increment();
		}

	}

	public void addGrenade() {
		Optional<GrenadesCounter> opt = this.getUIElement(GrenadesCounter.class);
		if (opt.isEmpty()) {
			System.err.println("Could not find GrenadesCounterText");
			return;
		}

		GrenadesCounter grenades = opt.get();
		if (grenades.getNumber() < 999) {
			grenades.increment();
		}
	}
}
