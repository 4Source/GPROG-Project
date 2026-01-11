package ZombieGame.Entities;

import ZombieGame.EntityType;
import ZombieGame.Components.StaticSpriteComponent;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.HitBoxType;

public class Tree extends Obstacle {
	/**
	 * @param pos The position of the tree
	 */
	public Tree(WorldPos pos) {
		// this.circleComponent = this.add(new CircleComponent(this, radius, color));
		super(pos, new CircleHitBox(HitBoxType.Block, 18, new Offset(0, 30)), e -> new StaticSpriteComponent(e, new StaticSprite("assets\\PostApocalypse_AssetPack\\Objects\\Nature\\Dark-Green\\Tree_3_Normal_Dark-Green.png", 1, 1, 3, 0, 0)));
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
