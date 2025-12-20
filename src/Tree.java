
// (c) Thorsten Hasbargen

import java.awt.Color;

class Tree extends Obstacle {

	/**
	 * @param posX The position in x of the tree
	 * @param posY The position in y of the tree
	 * @param radius The size of the tree
	 */
	public Tree(double posX, double posY, int radius) {
		super(posX, posY, radius, new Color(64, 160, 64), e -> new StaticPhysicsComponent(e, new CircleHitBox(HitBoxType.Block, radius)));
	}

	@Override
	public StaticPhysicsComponent getPhysicsComponent() {
		return (StaticPhysicsComponent) super.getPhysicsComponent();
	}

	@Override
	public EntityType getType() {
		return EntityType.TREE;
	}
}
