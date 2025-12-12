
// (c) Thorsten Hasbargen

interface GraphicSystem {
	/**
	 * Clear the screen before drawing on it
	 */
	void clear();

	/**
	 * Draw a GameObject on the Screen
	 * 
	 * @param gameObject The game object to draw
	 */
	void draw(GameObject gameObject);

	/**
	 * Draw a TextObject on the Screen
	 * 
	 * @param textObject The text object to draw
	 */
	void draw(TextObject textObject);

	/**
	 * Draw the objects to screen
	 */
	void swapBuffers();

	/**
	 * Set the world which should be drawn
	 * 
	 * @param world The world to which it should be set
	 */
	void setWorld(World world);
}
