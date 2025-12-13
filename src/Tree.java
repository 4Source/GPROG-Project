
// (c) Thorsten Hasbargen

import java.awt.Color;

class Tree extends Obstacle {

	/**
	 * @param posX The position in x of the tree
	 * @param posY The position in y of the tree
	 * @param radius The size of the tree
	 */
	public Tree(double posX, double posY, int radius) {
		super(posX, posY, radius, new Color(64, 160, 64));
	}

	@Override
	public int getType() {
		return Constants.TYPE_TREE;
	}

	@Override
	public void update(double deltaTime) {
	}
}
