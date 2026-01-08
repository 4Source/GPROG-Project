package ZombieGame;

import java.util.Iterator;

import ZombieGame.Components.LivingComponent;
import ZombieGame.Components.VisualComponent;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.UIElement;

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

			double secondsDiff = millisDiff / 1000.0;
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

					e.getComponents(VisualComponent.class).forEach(c -> {
						c.update(secondsDiff);
					});
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
				e.update(secondsDiff);
			}

			// Update all UI Elements
			Iterator<UIElement> uiIt = this.world.uiElementIterator();
			while (uiIt.hasNext()) {
				UIElement ui = uiIt.next();

				// Update entity
				ui.update(secondsDiff);

				// Remove entity if not alive
				if (ui.getComponents(LivingComponent.class).stream().anyMatch(c -> c.isLiving() == false)) {
					uiIt.remove();
					continue;
				}
			}

			GraphicSystem.getInstance().update();
			GraphicSystem.getInstance().clear();

			// Update changed collisions
			PhysicsSystem.getInstance().update();

			// After handled the inputs of components clear the input system
			InputSystem.getInstance().clear();

			// adjust displayed pane of the world
			this.world.adjustWorldPart();

			// Draw everything
			GraphicSystem.getInstance().draw();

			// redraw everything
			GraphicSystem.getInstance().swapBuffers();

			// Remove all dead Entities
			entityIt = this.world.entityIterator();
			while (entityIt.hasNext()) {
				Entity e = entityIt.next();

				// Remove entity if not alive
				if (e.getComponents(LivingComponent.class).stream().anyMatch(c -> c.isLiving() == false)) {
					entityIt.remove();
					continue;
				}
			}

			// TODO: Entities which can Spawn should implement spawnable
			// create new objects if needed
			this.world.createNewObjects(secondsDiff);
		}
	}

	public static void main(String[] args) {
		System.out.println("Starting game...");
		new Game();
		System.out.println("Game stopped...");
	}
}
