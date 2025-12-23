import java.awt.Color;

class Tree extends Obstacle {
	/**
	 * @param posX The position in x of the tree
	 * @param posY The position in y of the tree
	 * @param radius The size of the tree
	 */
	public Tree(double posX, double posY, int radius) {
		// this.circleComponent = this.add(new CircleComponent(this, radius, color));
		super(posX, posY, new CircleHitBox(HitBoxType.Block, radius), e -> new CircleComponent(e, radius, new Color(64, 160, 64)));
	}

	@Override
	public CircleComponent getVisualComponent() {
		return (CircleComponent) super.getVisualComponent();
	}

	@Override
	public EntityType getType() {
		return EntityType.TREE;
	}
}
