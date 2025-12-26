import java.util.Iterator;
import java.util.Optional;

final class Game {
	private World world = null;
	// defines maximum frame rate
	private static final int FRAME_MINIMUM_MILLIS = 5;

	public Game() {
		// Setup the window
		Frame frame = new Frame();
		frame.displayOnScreen();

		// Create a new world
		this.world = new ZombieWorld();

		Entity.setWorld(this.world);

		this.world.init();
		// this.world.run();
		long lastTick = System.currentTimeMillis();

		while (true) {
			this.world.update();

			// calculate elapsed time
			long currentTick = System.currentTimeMillis();
			long millisDiff = currentTick - lastTick;

			// don't run faster then MINIMUM_DIFF_SECONDS per frame
			if (millisDiff < FRAME_MINIMUM_MILLIS) {
				try {
					Thread.sleep(FRAME_MINIMUM_MILLIS - millisDiff);
				} catch (Exception ex) {
					System.err.println(ex);
				}
				currentTick = System.currentTimeMillis();
				millisDiff = currentTick - lastTick;
			}

			lastTick = currentTick;

			// Open Pause menu
			if (InputSystem.getInstance().isPressed(Action.GAME_PAUSE)) {
				System.exit(0);
			}

			// no actions if game is over
			if (this.world.gameOver) {
				Iterator<Entity> entityIt = this.world.entityIterator();
				while (entityIt.hasNext()) {
					Entity e = entityIt.next();

					Optional<VisualComponent> c = e.getComponent(VisualComponent.class);
					if (c.isPresent()) {
						c.get().update(millisDiff / 1000.0);
					}
				}

				GraphicSystem.getInstance().clear();
				GraphicSystem.getInstance().draw();
				GraphicSystem.getInstance().swapBuffers();
				continue;
			}

			// Update all Entities
			Iterator<Entity> entityIt = this.world.entityIterator();
			while (entityIt.hasNext()) {
				Entity e = entityIt.next();

				// Update entity
				e.update(millisDiff / 1000.0);
			}

			// Update all UI Elements
			Iterator<UIElement> uiIt = this.world.uiElementIterator();
			while (uiIt.hasNext()) {
				UIElement ui = uiIt.next();

				// Update entity
				ui.update(millisDiff / 1000.0);

				// Remove entity if not alive
				Optional<LivingComponent> c = ui.getComponent(LivingComponent.class);
				if (c.isPresent() && c.get().isLiving() == false) {
					uiIt.remove();
					continue;
				}
			}

			GraphicSystem.getInstance().update();

			// Update changed collisions
			PhysicsSystem.getInstance().update();

			// After handled the inputs of components clear the input system
			InputSystem.getInstance().clear();

			// Remove all dead Entities
			entityIt = this.world.entityIterator();
			while (entityIt.hasNext()) {
				Entity e = entityIt.next();

				// Remove entity if not alive
				Optional<LivingComponent> c = e.getComponent(LivingComponent.class);
				if (c.isPresent() && c.get().isLiving() == false) {
					entityIt.remove();
					continue;
				}
			}

			// adjust displayed pane of the world
			this.world.adjustWorldPart();

			// Draw everything
			GraphicSystem.getInstance().clear();
			GraphicSystem.getInstance().draw();

			// redraw everything
			GraphicSystem.getInstance().swapBuffers();

			// TODO: Entities which can Spawn should implement spawnable
			// create new objects if needed
			this.world.createNewObjects(millisDiff / 1000.0);
		}
	}

	public static void main(String[] args) {
		System.out.println("Starting game...");
		new Game();
		System.out.println("Game stopped...");
	}
}
