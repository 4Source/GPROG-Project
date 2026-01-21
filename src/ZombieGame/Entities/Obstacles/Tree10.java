package ZombieGame.Entities.Obstacles;

import ZombieGame.EntityType;
import ZombieGame.Components.StaticSpriteComponent;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Obstacle;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.HitBoxType;

public class Tree10 extends Obstacle {
	/**
	 * @param pos The position of the tree
	 */
	public Tree10(WorldPos pos) {
		super(pos, new CircleHitBox(HitBoxType.Block, 12, new Offset(0, 44)), e -> new StaticSpriteComponent(e, new StaticSprite("assets/PostApocalypse_AssetPack/Objects/Nature/Dark-Green/Tree_10_Small-oak_Dark-Green.png", 1, 1, 3, 0, 0)));
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
