
// (c) Thorsten Hasbargen

import java.awt.Color;

class Avatar extends GameObject {

	/**
	 * Spawns an avatar
	 * 
	 * @param startX The position in x of the avatar where is should be at game start
	 * @param startY The position in y of the avatar where is should be at game start
	 */
	public Avatar(double startX, double startY) {
		super(startX, startY, 0, 200, 15, new Color(96, 96, 255));
		this.isMoving = false;
	}

	public void update(double deltaTime) {
		// move Avatar one step forward
		super.update(deltaTime);

		// calculate all collisions with other Objects
		GameObjectList collisions = GameObject.world.getPhysicsSystem().getCollisions(this);
		for (int i = 0; i < collisions.size(); i++) {
			GameObject obj = collisions.get(i);

			// if Object is a tree, move back one step
			if (obj.type() == Constants.TYPE_TREE) {
				this.moveBack();
			}

			// pick up Grenades
			else if (obj.type() == Constants.TYPE_GRENADE) {
				((ZombieWorld) GameObject.world).addGrenade();
				obj.isLiving = false;
			}
		}
	}

	public int type() {
		return Constants.TYPE_AVATAR;
	}
}
