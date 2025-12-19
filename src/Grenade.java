
// (c) Thorsten Hasbargen

import java.awt.Color;

class Grenade extends Item {
	LifetimeComponent lifetimeComponent;

	/**
	 * @param posX The initial position in x of the grenade
	 * @param posY The initial position in y of the grenade
	 */
	public Grenade(double posX, double posY) {
		super(posX, posY, 15, Color.ORANGE);
		this.lifetimeComponent = this.add(new LifetimeComponent(this, Constants.LIFE_GRENADE));
	}

	@Override
	public EntityType getType() {
		return EntityType.GRENADE_ITEM;
	}
}
