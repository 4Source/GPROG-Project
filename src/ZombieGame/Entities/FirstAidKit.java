package ZombieGame.Entities;

import java.awt.Color;

import ZombieGame.Components.LifeComponent;
import ZombieGame.Components.StaticSpriteComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.HitBoxType;

public class FirstAidKit extends Item {

	/**
	 * @param pos The initial position of the ammunition
	 */
	public FirstAidKit(WorldPos pos) {
		super(pos, new CircleHitBox(HitBoxType.Overlap, 16), Color.ORANGE, e -> new StaticSpriteComponent(e, new StaticSprite("assets/PostApocalypse_AssetPack/Objects/Pickable/Bandage.png", 1, 1, 3, 0, 0)));
	}

	@Override
	public StaticSpriteComponent getVisualComponent() {
		return (StaticSpriteComponent) super.getVisualComponent();
	}

	@Override
	public void pickUp(Entity entity) {
		super.pickUp(entity);
		for (LifeComponent c : entity.getComponents(LifeComponent.class)) {
			c.restoreHealth(2);
		}
	}
}
