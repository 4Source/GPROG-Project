
// (c) Thorsten Hasbargen

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

abstract class World {
	// top left corner of the displayed pane of the world
	double worldPartX = 0;
	double worldPartY = 0;

	// if game is over
	boolean gameOver = false;

	// all objects in the game, including the Avatar
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	Avatar avatar;
	ArrayList<UIObject> uiElements = new ArrayList<UIObject>();

	World() {
	}

	public final void addEntity(Entity entity) {
		entities.add(entity);
	}

	public final void removeEntity(Entity entity) {
		entities.remove(entity);
		entity.get(PhysicsComponent.class).ifPresent(c -> PhysicsSystem.getInstance().unregisterComponent(c));
	}

	public final void removeEntity(int index) {
		Entity entity = this.getEntity(index);
		this.removeEntity(entity);
	}

	public final Collection<Entity> getEntities() {
		return Collections.unmodifiableCollection(entities);
	}

	public final Entity getEntity(int index) {
		return entities.get(index);
	}

	// adjust the displayed pane of the world according to Avatar and Bounds
	public final void adjustWorldPart() {
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
