package ZombieGame.Entities;

import java.awt.Color;

import ZombieGame.Components.PlayerWeaponComponent;
import ZombieGame.Constants;
import ZombieGame.Components.LifetimeComponent;
import ZombieGame.Components.GunshotComponent;
import ZombieGame.Components.StaticSpriteComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.RectangleHitBox;

// TODO: Make Ammunition abstract and split into different Ammunition types (Shotgun, Pistol, Rifle)
// TODO: Make Ammunition pickup only work when matching the gun type
// TODO: Make Ammunition drop in packs when the gun is changed
// TODO: Add abstract class Gun extends Item and different implementations for Shotgun, Pistol, Rifle
public class Ammunition extends Item {

	/**
	 * @param pos The initial position of the ammunition
	 */
	public Ammunition(WorldPos pos) {
		super(pos, new RectangleHitBox(HitBoxType.Overlap, 21, 16), Color.ORANGE, e -> new StaticSpriteComponent(e, new StaticSprite("assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Ammo-crate_Blue.png", 1, 1, 3, 0, 0)));
	}

	@Override
	public StaticSpriteComponent getVisualComponent() {
		return (StaticSpriteComponent) super.getVisualComponent();
	}

	@Override
	public void pickUp(Entity entity) {
		super.pickUp(entity);
		for (PlayerWeaponComponent c : entity.getComponents(PlayerWeaponComponent.class)) {
			c.restockAmmunition(20);
		}
	}
}
