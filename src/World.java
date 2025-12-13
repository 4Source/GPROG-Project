
// (c) Thorsten Hasbargen

import java.util.ArrayList;

abstract class World {
	private UserInput userInput; // TODO: Why not local variable

	// top left corner of the displayed pane of the world
	double worldPartX = 0;
	double worldPartY = 0;

	// defines maximum frame rate
	private static final int FRAME_MINIMUM_MILLIS = 10;

	// if game is over
	boolean gameOver = false;

	// all objects in the game, including the Avatar
	ArrayList<Entity> entities = new ArrayList<Entity>();
	Avatar avatar;
	ArrayList<UIObject> uiElements = new ArrayList<UIObject>();

	World() {
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
			this.userInput = InputSystem.getInstance().getUserInput();
			processUserInput(this.userInput, millisDiff / 1000.0);
			this.userInput.clear();

			// no actions if game is over
			if (this.gameOver) {
				continue;
			}

			// move all Objects, maybe collide them etc...
			for (int i = 0; i < this.entities.size(); i++) {
				GameObject obj = this.entities.get(i);
				if (obj instanceof Entity && ((Entity) obj).isLiving) {
					obj.update(millisDiff / 1000.0);
				}
			}

			// TODO: Iterator ?
			// delete all Objects which are not living anymore
			int gameSize = this.entities.size();
			int num = 0;
			while (num < gameSize) {
				if (this.entities.get(num).isLiving == false) {
					this.entities.remove(num);
					gameSize--;
				} else {
					num++;
				}
			}

			// adjust displayed pane of the world
			this.adjustWorldPart();

			// draw all Objects
			GraphicSystem.getInstance().clear();
			for (int i = 0; i < gameSize; i++) {
				GraphicSystem.getInstance().draw(this.entities.get(i));
			}

			// draw all TextObjects
			for (int i = 0; i < this.uiElements.size(); i++) {
				GraphicSystem.getInstance().draw(this.uiElements.get(i));
			}

			// redraw everything
			GraphicSystem.getInstance().swapBuffers();

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

	protected abstract void init();

	protected abstract void processUserInput(UserInput input, double deltaTime);

	protected abstract void createNewObjects(double deltaTime);

}
