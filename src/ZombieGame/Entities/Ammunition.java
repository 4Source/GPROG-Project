package ZombieGame.Entities;

import java.awt.Color;

import ZombieGame.Constants;
import ZombieGame.HitBoxType;
import ZombieGame.RectangleHitBox;
import ZombieGame.Components.GunshotComponent;
import ZombieGame.Components.LifetimeComponent;
import ZombieGame.Components.StaticSpriteComponent;
import ZombieGame.Sprites.StaticSprite;

public class Ammunition extends Item {
	private LifetimeComponent lifetimeComponent;

	/**
	 * @param posX The initial position in x of the grenade
	 * @param posY The initial position in y of the grenade
	 */
	public Ammunition(double posX, double posY) {
		super(posX, posY, new RectangleHitBox(HitBoxType.Overlap, 21, 16), Color.ORANGE, e -> new StaticSpriteComponent(e));
		this.lifetimeComponent = this.add(new LifetimeComponent(this, Constants.LIFE_GRENADE));
		this.getVisualComponent().addSprite(new StaticSprite("assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Ammo-crate_Blue.png", 1, 1, 3, 0, 0));
	}

	public LifetimeComponent getLifetimeComponent() {
		return this.lifetimeComponent;
	}

	@Override
	public StaticSpriteComponent getVisualComponent() {
		return (StaticSpriteComponent) super.getVisualComponent();
	}

	@Override
	public void pickUp(Entity entity) {
		super.pickUp(entity);
		entity.getComponents(GunshotComponent.class).forEach(c -> c.restockAmmunition(20));
	}
}
