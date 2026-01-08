package ZombieGame.Entities;

import java.awt.Color;

import ZombieGame.Constants;
import ZombieGame.EntityType;
import ZombieGame.Components.CircleComponent;
import ZombieGame.Components.LifetimeComponent;

public class Grenade extends Item {
	private LifetimeComponent lifetimeComponent;

	/**
	 * @param posX The initial position in x of the grenade
	 * @param posY The initial position in y of the grenade
	 */
	public Grenade(double posX, double posY) {
		super(posX, posY, 15, Color.ORANGE, e -> new CircleComponent(e, 15, Color.ORANGE));
		this.lifetimeComponent = this.add(new LifetimeComponent(this, Constants.LIFE_GRENADE));
	}

	public LifetimeComponent getLifetimeComponent() {
		return this.lifetimeComponent;
	}

	@Override
	public CircleComponent getVisualComponent() {
		return (CircleComponent) super.getVisualComponent();
	}

	@Override
	public EntityType getType() {
		return EntityType.GRENADE_ITEM;
	}
}
