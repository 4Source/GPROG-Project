package ZombieGame;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import ZombieGame.Components.ImageComponent;
import ZombieGame.Components.LivingComponent;
import ZombieGame.Components.VisualComponent;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Entities.Button;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.HelpText;
import ZombieGame.Entities.UIElement;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Debug.DebugSystem;
import ZombieGame.Systems.Graphic.GraphicLayer;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;
import ZombieGame.Systems.Physic.PhysicsSystem;
import ZombieGame.World.TileType;
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
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Viewport.setScreenWidth(frame.getWidth());
				Viewport.setScreenHeight(frame.getHeight());
				GraphicSystem.getInstance().onViewportResize();
			}
		});
		frame.displayOnScreen();

		// Create a new world
		Game.world = new ZombieWorld();
		Game.world.adjustWorldPart();
	}

	private void startScreen() {
		AtomicBoolean started = new AtomicBoolean(false);

		UIElement title = new UIElement(Viewport.getTopCenter().add(new Offset(0, Viewport.getScreenHeight() / 3)),
				e -> new ImageComponent(e, new StaticSprite("assets\\TheUndeadTitle.png", 1, 1, 1, 0, 0), GraphicLayer.UI)) {
		};
		Button startBtn = new Button(Viewport.getBottomCenter().sub(new Offset(0, Viewport.getScreenHeight() / 4)),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Play_Pressed.png", 1, 1, 3, 0, 0),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Play_Not-Pressed.png", 1, 1, 3, 0, 0),
				() -> {
					started.set(true);
				});
		Button exitBtn = new Button(
				Viewport.getBottomCenter().sub(new Offset(0, Viewport.getScreenHeight() / 4).sub(0, startBtn.getUIComponent().getHeight() + 10)),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Quit_Pressed.png", 1, 1, 3, 0, 0),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Quit_Not-Pressed.png", 1, 1, 3, 0, 0),
				() -> {
					System.exit(0);
				});

		Game.world.addUIElement(title);
		Game.world.addUIElement(startBtn);
		Game.world.addUIElement(exitBtn);

		while (!started.get()) {
			double secondsDiff = calculateDeltaTime();

			// Update the visual components of all Entities
			Iterator<Entity> entityIt = Game.world.loadedEntityIterator();
			while (entityIt.hasNext()) {
				Entity e = entityIt.next();

				for (VisualComponent c : e.getComponents(VisualComponent.class)) {
					c.update(secondsDiff);
				}
			}

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

			GraphicSystem.getInstance().clear();
			GraphicSystem.getInstance().draw();
			GraphicSystem.getInstance().swapBuffers();
		}

		Game.world.removeUIElement(title);
		Game.world.removeUIElement(startBtn);
		Game.world.removeUIElement(exitBtn);
	}

	private void pauseMenu() {
		AtomicBoolean continueGame = new AtomicBoolean(false);
		int padding = 10;

		// Viewport.getTopCenter().add(new Offset(0, Viewport.getScreenHeight() / 3)
		Button startBtn = new Button(Viewport.getCenter(),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Play_Pressed.png", 1, 1, 3, 0, 0),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Play_Not-Pressed.png", 1, 1, 3, 0, 0),
				() -> {
					continueGame.set(true);
				});
		Button exitBtn = new Button(startBtn.getPositionComponent().getViewPos().add(0, (int) (startBtn.getUIComponent().getHeight() + padding)),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Quit_Pressed.png", 1, 1, 3, 0, 0),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Quit_Not-Pressed.png", 1, 1, 3, 0, 0),
				() -> {
					System.exit(0);
				});

		Game.world.addUIElement(new HelpText(Viewport.getBottomLeft().sub(-200, 200)));

		Game.world.addUIElement(startBtn);
		Game.world.addUIElement(exitBtn);

		while (!continueGame.get()) {
			double secondsDiff = calculateDeltaTime();

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

			GraphicSystem.getInstance().clear();
			GraphicSystem.getInstance().draw();
			GraphicSystem.getInstance().swapBuffers();
		}

		Game.world.removeUIElement(startBtn);
		Game.world.removeUIElement(exitBtn);
	}

	private void gameOver() {
		System.out.println("Game over");
		UIElement gameover = new UIElement(Viewport.getTopCenter().add(new Offset(0, Viewport.getScreenHeight() / 3)),
				e -> new ImageComponent(e, new StaticSprite("assets\\GameOver.png", 1, 1, 1, 0, 0), GraphicLayer.UI)) {
		};
		Button exitBtn = new Button(Viewport.getBottomCenter().sub(new Offset(0, Viewport.getScreenHeight() / 4)),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Quit_Pressed.png", 1, 1, 3, 0, 0),
				new StaticSprite("assets\\PostApocalypse_AssetPack\\UI\\Menu\\Main Menu\\Quit_Not-Pressed.png", 1, 1, 3, 0, 0),
				() -> {
					System.exit(0);
				});

		Game.world.addUIElement(gameover);
		Game.world.addUIElement(exitBtn);

		while (true) {
			double secondsDiff = calculateDeltaTime();

			// Update the visual components of avatar for death animation
			Iterator<Entity> entityIt = Game.world.loadedEntityIterator();
			while (entityIt.hasNext()) {
				Entity e = entityIt.next();
				if (e.getType() == EntityType.AVATAR) {
					for (VisualComponent c : e.getComponents(VisualComponent.class)) {
						c.update(secondsDiff);
					}
					break;
				}
			}

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

			GraphicSystem.getInstance().clear();
			GraphicSystem.getInstance().draw();
			GraphicSystem.getInstance().swapBuffers();
		}
	}

	private void run() {
		Game.world.init();
		lastTick = System.currentTimeMillis();

		while (true) {

			double secondsDiff = calculateDeltaTime();

			// Open Pause menu
			if (InputSystem.getInstance().isPressed(Action.GAME_PAUSE)) {
				pauseMenu();
			}

			// Game over
			if (Game.world.gameOver) {
				gameOver();
			}

			Game.world.update(secondsDiff);
			Game.world.adjustWorldPart();
			Game.world.processGenerationQueue(2);

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
		System.out.println("Creating world...");
		Game game = new Game();
		System.out.println("Loading game...");
		game.startScreen();
		System.out.println("Starting game...");
		game.run();

		// Generate Tile test sheet
		// TileType.testTileClusters();
	}
}

// BUG: #1 Zombies seem to not always get damage registered

// BUG: #3 After 2 time hit by a zombie the zombie freezes

// BUG: #4 BABY Zombies can bug into player

// TODO: More Obstacles for visual variety