package ZombieGame.Entities.Obstacles;

import ZombieGame.EntityType;
import ZombieGame.Components.StaticSpriteComponent;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Obstacle;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.HitBoxType;

public class Tree3 extends Obstacle {
	/**
	 * @param pos The position of the tree
	 */
	public Tree3(WorldPos pos) {
		super(pos, new CircleHitBox(HitBoxType.Block, 18, new Offset(0, 35)), e -> new StaticSpriteComponent(e, new StaticSprite("assets\\PostApocalypse_AssetPack\\Objects\\Nature\\Dark-Green\\Tree_3_Normal_Dark-Green.png", 1, 1, 3, 0, 0)));
	}

	@Override
	public StaticSpriteComponent getVisualComponent() {
		return (StaticSpriteComponent) super.getVisualComponent();
	}

	@Override
	public EntityType getType() {
		return EntityType.TREE;
	}
}
