package ZombieGame;

import java.util.Iterator;

import ZombieGame.Components.LivingComponent;
import ZombieGame.Components.VisualComponent;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.UIElement;
import ZombieGame.Systems.Debug.DebugSystem;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;
import ZombieGame.Systems.Physic.PhysicsSystem;
import ZombieGame.World.World;
import ZombieGame.World.ZombieWorld;

public final class Game {
	public static World world;
	// defines maximum frame rate
	private static final int FRAME_MINIMUM_MILLIS = 5;
	private long lastTick;

	public Game() {
		// Setup the window
		Frame frame = new Frame();
		frame.displayOnScreen();

		// Create a new world
		Game.world = new ZombieWorld();
		Game.world.init();

		lastTick = System.currentTimeMillis();

		while (true) {

			double secondsDiff = calculateDeltaTime();

			// Open Pause menu
			if (InputSystem.getInstance().isPressed(Action.GAME_PAUSE)) {
				System.exit(0);
			}

			// Game over
			if (Game.world.gameOver) {
				Iterator<Entity> entityIt = Game.world.loadedEntityIterator();
				while (entityIt.hasNext()) {
					Entity e = entityIt.next();

					for (VisualComponent c : e.getComponents(VisualComponent.class)) {
						c.update(secondsDiff);
					}
				}

				GraphicSystem.getInstance().clear();
				GraphicSystem.getInstance().draw();
				GraphicSystem.getInstance().swapBuffers();
				continue;
			}

			Game.world.update(secondsDiff);
			Game.world.adjustWorldPart();
			Game.world.processGenerationQueue(10);

			// Update all Entities
			Iterator<Entity> entityIt = Game.world.loadedEntityIterator();
			while (entityIt.hasNext()) {
				Entity e = entityIt.next();

				// Update entity
				e.update(secondsDiff);
			}

			// Remove all dead Entities
			entityIt = Game.world.entityIterator();
			while (entityIt.hasNext()) {
				Entity e = entityIt.next();

				// Remove entity if not alive
				if (e.getComponents(LivingComponent.class).stream().anyMatch(c -> c.isLiving() == false)) {
					entityIt.remove();
					continue;
				}
			}

			// Update changed collisions
			PhysicsSystem.getInstance().update();

			// create new objects if needed
			Game.world.UpdateEntityGeneration(secondsDiff);

			// Update all UI Elements
			Iterator<UIElement> uiIt = Game.world.uiElementIterator();
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

			DebugSystem.getInstance().update();

			// After handled the inputs of components clear the input system
			InputSystem.getInstance().clear();

			// Draw everything
			GraphicSystem.getInstance().clear();
			GraphicSystem.getInstance().draw();
			GraphicSystem.getInstance().swapBuffers();
		}
	}

	private double calculateDeltaTime() {
		// calculate elapsed time
		long currentTick = System.currentTimeMillis();
		long millisDiff = currentTick - this.lastTick;

		// don't run faster then MINIMUM_DIFF_SECONDS per frame
		if (millisDiff < FRAME_MINIMUM_MILLIS) {
			try {
				Thread.sleep(FRAME_MINIMUM_MILLIS - millisDiff);
			} catch (Exception ex) {
				System.err.println(ex);
			}
			currentTick = System.currentTimeMillis();
			millisDiff = currentTick - this.lastTick;
		}

		this.lastTick = currentTick;
		return millisDiff / 1000.0;
	}

	public static void main(String[] args) {
		System.out.println("Starting game...");
		new Game();
		System.out.println("Game stopped...");
	}
}

// BUG: #1 Zombies seem to not always get damage registered

// BUG: #3 After 2 time hit by a zombie the zombie freezes

// BUG: #4 BABY Zombies can bug into player