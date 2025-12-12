
// (c) Thorsten Hasbargen

import java.awt.Color;

class Grenade extends GameObject {
	double lifetime = Constants.LIFE_GRENADE;

	/**
	 * @param posX The initial position in x of the grenade
	 * @param posY The initial position in y of the grenade
	 */
	public Grenade(double posX, double posY) {
		super(posX, posY, 0, 0, 15, Color.ORANGE);
	}

	public void update(double deltaTime) {
		this.lifetime -= deltaTime;
		if (this.lifetime < 0) {
			this.isLiving = false;
			return;
		}
	}

	public int type() {
		return Constants.TYPE_GRENADE;
	}
}
