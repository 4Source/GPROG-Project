
// (c) Thorsten Hasbargen

import java.awt.Color;

abstract class TextObject {
	protected static World world;

	protected int posX, posY;
	protected Color color;

	/**
	 * @param posX The position in x direction
	 * @param posY The position in y direction
	 * @param color The color of the game object
	 */
	public TextObject(int posX, int posY, Color color) {
		this.posX = posX;
		this.posY = posY;
		this.color = color;
	}

	/**
	 * @return Returns the text to display
	 */
	public abstract String toString();

	/**
	 * Set the world where the text objects are belonging to
	 * 
	 * @param world The world to which it should be set
	 */
	protected static void setWorld(World world) {
		TextObject.world = world;
	}
}
