
// (c) Thorsten Hasbargen

class ZombieWorld extends World {
	private double timePassed = 0;
	private double timeSinceLastShot = 0;

	// for grenades
	private int grenades = 5;
	private GrenadesCounterText counterG;
	private ZombieCounterText counterZ;
	private HelpText helpText;
	private double spawnGrenade = 0;

	private double lifeHelpText = 10.0;

	protected void init() {
		// add the Avatar
		this.avatar = new Avatar(2500, 2000);
		this.gameObjects.add(this.avatar);

		// set WorldPart position
		this.worldPartX = 1500;
		this.worldPartY = 1500;

		// add a little forrest
		for (int x = 0; x < 5000; x += 1000) {
			for (int y = 0; y < 4000; y += 800) {
				this.gameObjects.add(new Tree(x + 300, y + 200, 80));
				this.gameObjects.add(new Tree(x + 600, y + 370, 50));
				this.gameObjects.add(new Tree(x + 200, y + 600, 50));
				this.gameObjects.add(new Tree(x + 500, y + 800, 40));
				this.gameObjects.add(new Tree(x + 900, y + 500, 100));
				this.gameObjects.add(new Tree(x + 760, y + 160, 40));
			}
		}

		// add one zombie
		this.gameObjects.add(new ZombieAI(100, 100));

		this.counterZ = new ZombieCounterText(20, 40);
		this.counterG = new GrenadesCounterText(770, 40);
		this.helpText = new HelpText(100, 400);

		this.counterG.setNumber(this.grenades);
		this.textObjects.add(this.counterZ);
		this.textObjects.add(this.counterG);
		this.textObjects.add(this.helpText);
	}

	protected void processUserInput(UserInput userInput, double deltaTime) {
		// distinguish if Avatar shall move or shoots
		int button = userInput.mouseButton;

		// Mouse events
		if (userInput.isMouseEvent) {
			// move
			if (button == 1) {
				this.avatar.setDestination(userInput.mousePressedX + this.worldPartX, userInput.mousePressedY + this.worldPartY);
			}
		}

		// Mouse still pressed?
		if (userInput.isMousePressed && button == 3) {
			// only 1 shot every ... seconds:
			this.timeSinceLastShot += deltaTime;
			if (this.timeSinceLastShot > 0.2) {
				this.timeSinceLastShot = 0;

				Gunshot shot = new Gunshot(this.avatar.posX, this.avatar.posY, userInput.mouseMovedX + this.worldPartX, userInput.mouseMovedY + this.worldPartY);
				this.gameObjects.add(shot);
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

	/**
	 * @param posX the position in x where the grenade should be thrown to
	 * @param posY the position in y where the grenade should be thrown to
	 */
	private void throwGrenade(double posX, double posY) {
		if (this.grenades <= 0)
			return;

		// throw grenade
		for (int i = 0; i < 2000; i++) {
			double alfa = Math.random() * Math.PI * 2;
			double speed = 50 + Math.random() * 200;
			double time = 0.2 + Math.random() * 0.4;
			Gunshot shot = new Gunshot(posX, posY, alfa, speed, time);
			this.gameObjects.add(shot);
		}

		// inform counter
		this.grenades--;
		this.counterG.setNumber(this.grenades);
	}

	protected void createNewObjects(double deltaTime) {
		createZombie(deltaTime);
		createGrenade(deltaTime);

		// delete HelpText after ... seconds
		if (this.helpText != null) {
			this.lifeHelpText -= deltaTime;
			if (this.lifeHelpText < 0) {
				textObjects.remove(this.helpText);
				this.helpText = null;
			}
		}
	}

	private void createGrenade(double deltaTime) {
		final double INTERVAL = Constants.SPAWN_GRENADE;

		this.spawnGrenade += deltaTime;
		if (this.spawnGrenade > INTERVAL) {
			this.spawnGrenade -= INTERVAL;

			// create new Grenade
			double x = this.worldPartX + Math.random() * Constants.WORLDPART_WIDTH;
			double y = this.worldPartY + Math.random() * Constants.WORLDPART_HEIGHT;

			// if too close to Avatar, cancel
			double dx = x - this.avatar.posX;
			double dy = y - this.avatar.posY;
			if (dx * dx + dy * dy < 200 * 200) {
				this.spawnGrenade = INTERVAL;
				return;
			}

			// if collisions occur, cancel
			Grenade grenade = new Grenade(x, y);
			GameObjectList list = getPhysicsSystem().getCollisions(grenade);
			if (list.size() != 0) {
				this.spawnGrenade = INTERVAL;
				return;
			}

			// else add zombie to world
			this.gameObjects.add(grenade);
			this.counterG.setNumber(this.grenades);
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

			// if too close to Avatar, cancel
			double dx = x - this.avatar.posX;
			double dy = y - this.avatar.posY;
			if (dx * dx + dy * dy < 400 * 400) {
				this.timePassed = INTERVAL;
				return;
			}

			// if collisions occur, cancel
			ZombieAI zombie = new ZombieAI(x, y);
			GameObjectList list = getPhysicsSystem().getCollisions(zombie);
			if (list.size() != 0) {
				this.timePassed = INTERVAL;
				return;
			}

			// else add zombie to world
			this.gameObjects.add(zombie);
			zombie.setDestination(this.avatar);
			ZombieCounterText counter = (ZombieCounterText) this.textObjects.get(0);
			counter.increment();
		}

	}

	public void addGrenade() {
		if (this.grenades < 999) {
			this.grenades++;
		}
		this.counterG.setNumber(this.grenades);
	}
}
