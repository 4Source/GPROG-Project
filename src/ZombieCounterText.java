
// (c) Thorsten Hasbargen

import java.awt.Color;

class ZombieCounterText extends TextObject {
	private int number = 1;

	/**
	 * @param posY The position in y of the zombie counter on the screen
	 * @param posX The position in x of the zombie counter on the screen
	 */
	public ZombieCounterText(int posX, int posY) {
		super(posX, posY, new Color(255, 255, 0, 210));
	}

	public String toString() {
		String display = "Zombies: ";
		display += this.number;
		return display;
	}

	/**
	 * Increases the counter
	 */
	public void increment() {
		this.number++;
	}
}
