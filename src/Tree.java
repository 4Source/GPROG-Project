class Tree extends Obstacle {
	/**
	 * @param posX The position in x of the tree
	 * @param posY The position in y of the tree
	 */
	public Tree(double posX, double posY) {
		// this.circleComponent = this.add(new CircleComponent(this, radius, color));
		super(posX, posY, new CircleHitBox(HitBoxType.Block, 18, 0, 30), e -> new ObstacleSpriteComponent(e));

		this.getVisualComponent().addSprite(new StaticSprite("assets\\PostApocalypse_AssetPack\\Objects\\Nature\\Dark-Green\\Tree_3_Normal_Dark-Green.png", 1, 1, 3, 0, 0));
	}

	@Override
	public ObstacleSpriteComponent getVisualComponent() {
		return (ObstacleSpriteComponent) super.getVisualComponent();
	}

	@Override
	public EntityType getType() {
		return EntityType.TREE;
	}
}
