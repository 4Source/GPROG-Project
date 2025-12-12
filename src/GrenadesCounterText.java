
// (c) Thorsten Hasbargen

import java.awt.Color;

class GrenadesCounterText extends TextObject {
	private int number = 1;

	/**
	 * @param posX The position in x of the grenades counter on the screen
	 * @param posY The position in y of the grenades counter on the screen
	 */
	public GrenadesCounterText(int posX, int posY) {
		super(posX, posY, new Color(255, 255, 0, 210));
	}

	public String toString() {
		String display = "Grenades: ";
		display += this.number;
		return display;
	}

	/**
	 * Set the number of grenades
	 * 
	 * @param number The new number of grenades
	 */
	public void setNumber(int number) {
		this.number = number;
	}
}
