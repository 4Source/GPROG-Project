import java.util.Optional;

final class Game {
	private World world = null;
	// defines maximum frame rate
	private static final int FRAME_MINIMUM_MILLIS = 10;

	public Game() {
		// Setup the window
		Frame frame = new Frame();
		frame.displayOnScreen();
		// TODO: Allow to enable debug by key
		PhysicsSystem.enableDebug = true;

		// Create a new world
		this.world = new ZombieWorld();

		PhysicsSystem.setWorld(world);

		GameObject.setWorld(this.world);

		this.world.init();
		// this.world.run();
		long lastTick = System.currentTimeMillis();

		while (true) {
			// calculate elapsed time
			long currentTick = System.currentTimeMillis();
			long millisDiff = currentTick - lastTick;

			// don't run faster then MINIMUM_DIFF_SECONDS per frame
			if (millisDiff < FRAME_MINIMUM_MILLIS) {
				try {
					Thread.sleep(FRAME_MINIMUM_MILLIS - millisDiff);
				} catch (Exception ex) {
					System.err.print(ex);
				}
				currentTick = System.currentTimeMillis();
				millisDiff = currentTick - lastTick;
			}

			lastTick = currentTick;

			// process User Input
			UserInput userInput = InputSystem.getInstance().getUserInput();
			world.processUserInput(userInput, millisDiff / 1000.0);
			userInput.clear();

			// no actions if game is over
			if (this.world.gameOver) {
				continue;
			}

			PhysicsSystem.getInstance().update();

			// move all Objects, maybe collide them etc...
			for (int i = 0; i < this.world.getEntities().size(); i++) {
				GameObject entity = this.world.getEntity(i);
				entity.update(millisDiff / 1000.0);
			}

			// delete all Objects which are not living anymore
			int gameSize = this.world.getEntities().size();
			int num = 0;
			while (num < gameSize) {
				Optional<LivingComponent> component = this.world.getEntity(num).get(LivingComponent.class);
				if (component.isPresent() && component.get().isLiving == false) {
					this.world.removeEntity(num);
					gameSize--;
				} else {
					num++;
				}
			}

			// adjust displayed pane of the world
			this.world.adjustWorldPart();

			// draw all Objects
			GraphicSystem.getInstance().clear();
			for (int i = 0; i < gameSize; i++) {
				GraphicSystem.getInstance().draw(this.world.getEntity(i));
			}

			// draw all TextObjects
			for (int i = 0; i < this.world.uiElements.size(); i++) {
				GraphicSystem.getInstance().draw(this.world.uiElements.get(i));
			}

			// redraw everything
			GraphicSystem.getInstance().swapBuffers();

			// create new objects if needed
			this.world.createNewObjects(millisDiff / 1000.0);
		}
	}

	public static void main(String[] args) {
		System.out.print("Starting game...");
		new Game();
		System.out.print("Game stopped...");
	}
}
