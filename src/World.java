
// (c) Thorsten Hasbargen

import java.util.ArrayList;

abstract class World {
	private GraphicSystem graphicSystem;
	private PhysicsSystem physicsSystem;
	private InputSystem inputSystem;
	private UserInput userInput; // TODO: Why not local variable

	// top left corner of the displayed pane of the world
	double worldPartX = 0;
	double worldPartY = 0;

	// defines maximum frame rate
	private static final int FRAME_MINIMUM_MILLIS = 10;

	// if game is over
	boolean gameOver = false;

	// all objects in the game, including the Avatar
	GameObjectList gameObjects = new GameObjectList();
	GameObject avatar;
	ArrayList<TextObject> textObjects = new ArrayList<TextObject>();

	World() {
		// TODO: Move this to main which physicsSystem is used should not be hardcoded in world creation
		this.physicsSystem = new CirclePhysicsSystem(this);
	}

	// TODO: Why is the main game loop inside world makes no sense should be in main
	/**
	 * The main GAME LOOP
	 */
	final void run() {
		long lastTick = System.currentTimeMillis();

		while (true) {
			// calculate elapsed time
			long currentTick = System.currentTimeMillis();
			long millisDiff = currentTick - lastTick;

			// don´t run faster then MINIMUM_DIFF_SECONDS per frame
			if (millisDiff < FRAME_MINIMUM_MILLIS) {
				try {
					Thread.sleep(FRAME_MINIMUM_MILLIS - millisDiff);
				} catch (Exception ex) {
					// TODO: Handle exception or at least log something
				}
				currentTick = System.currentTimeMillis();
				millisDiff = currentTick - lastTick;
			}

			lastTick = currentTick;

			// process User Input
			this.userInput = this.inputSystem.getUserInput();
			processUserInput(this.userInput, millisDiff / 1000.0);
			this.userInput.clear();

			// no actions if game is over
			if (this.gameOver) {
				continue;
			}

			// move all Objects, maybe collide them etc...
			for (int i = 0; i < this.gameObjects.size(); i++) {
				GameObject obj = this.gameObjects.get(i);
				if (obj.isLiving) {
					obj.update(millisDiff / 1000.0);
				}
			}

			// TODO: Iterator ?
			// delete all Objects which are not living anymore
			int gameSize = this.gameObjects.size();
			int num = 0;
			while (num < gameSize) {
				if (this.gameObjects.get(num).isLiving == false) {
					this.gameObjects.remove(num);
					gameSize--;
				} else {
					num++;
				}
			}

			// adjust displayed pane of the world
			this.adjustWorldPart();

			// draw all Objects
			this.graphicSystem.clear();
			for (int i = 0; i < gameSize; i++) {
				this.graphicSystem.draw(this.gameObjects.get(i));
			}

			// draw all TextObjects
			for (int i = 0; i < this.textObjects.size(); i++) {
				this.graphicSystem.draw(this.textObjects.get(i));
			}

			// redraw everything
			this.graphicSystem.swapBuffers();

			// create new objects if needed
			this.createNewObjects(millisDiff / 1000.0);
		}
	}

	// adjust the displayed pane of the world according to Avatar and Bounds
	private final void adjustWorldPart() {
		final int RIGHT_END = Constants.WORLD_WIDTH - Constants.WORLDPART_WIDTH;
		final int BOTTOM_END = Constants.WORLD_HEIGHT - Constants.WORLDPART_HEIGHT;

		// if avatar is too much right in display ...
		if (this.avatar.posX > this.worldPartX + Constants.WORLDPART_WIDTH - Constants.SCROLL_BOUNDS) {
			// ... adjust display
			this.worldPartX = this.avatar.posX + Constants.SCROLL_BOUNDS - Constants.WORLDPART_WIDTH;
			if (this.worldPartX >= RIGHT_END) {
				this.worldPartX = RIGHT_END;
			}
		}

		// same left
		else if (this.avatar.posX < this.worldPartX + Constants.SCROLL_BOUNDS) {
			this.worldPartX = this.avatar.posX - Constants.SCROLL_BOUNDS;
			if (this.worldPartX <= 0) {
				this.worldPartX = 0;
			}
		}

		// same bottom
		if (this.avatar.posY > this.worldPartY + Constants.WORLDPART_HEIGHT - Constants.SCROLL_BOUNDS) {
			this.worldPartY = this.avatar.posY + Constants.SCROLL_BOUNDS - Constants.WORLDPART_HEIGHT;
			if (this.worldPartY >= BOTTOM_END) {
				this.worldPartY = BOTTOM_END;
			}
		}

		// same top
		else if (this.avatar.posY < this.worldPartY + Constants.SCROLL_BOUNDS) {
			this.worldPartY = this.avatar.posY - Constants.SCROLL_BOUNDS;
			if (this.worldPartY <= 0) {
				this.worldPartY = 0;
			}
		}

	}

	protected void setGraphicSystem(GraphicSystem graphicSystem) {
		this.graphicSystem = graphicSystem;
	}

	protected void setInputSystem(InputSystem inputSystem) {
		this.inputSystem = inputSystem;
	}

	protected PhysicsSystem getPhysicsSystem() {
		return this.physicsSystem;
	}

	protected abstract void init();

	protected abstract void processUserInput(UserInput input, double deltaTime);

	protected abstract void createNewObjects(double deltaTime);

}
