
// (c) Thorsten Hasbargen

import java.awt.Color;

class Tree extends GameObject {

	/**
	 * @param posX The position in x of the tree
	 * @param posY The position in y of the tree
	 * @param radius The size of the tree
	 */
	public Tree(double posX, double posY, int radius) {
		super(posX, posY, 0, 0, radius, new Color(64, 160, 64));
		this.isMoving = false;
	}

	public int type() {
		return Constants.TYPE_TREE;
	}
}
