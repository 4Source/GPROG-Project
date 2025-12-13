
// (c) Thorsten Hasbargen

import java.awt.Color;

class Grenade extends Item {
	double lifetime = Constants.LIFE_GRENADE;

	/**
	 * @param posX The initial position in x of the grenade
	 * @param posY The initial position in y of the grenade
	 */
	public Grenade(double posX, double posY) {
		super(posX, posY, 15, Color.ORANGE);
	}

	public void update(double deltaTime) {
		this.lifetime -= deltaTime;
		if (this.lifetime < 0) {
			this.isLiving = false;
			return;
		}
	}

	@Override
	public int getType() {
		return Constants.TYPE_GRENADE;
	}
}
