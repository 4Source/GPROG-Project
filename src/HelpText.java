
// (c) Thorsten Hasbargen

import java.awt.Color;

class HelpText extends TextObject {

	/**
	 * @param posX The position in x of the grenades counter on the screen
	 * @param posY The position in y of the grenades counter on the screen
	 */
	public HelpText(int posX, int posY) {
		super(posX, posY, new Color(0, 120, 255, 60));
	}

	public String toString() {
		return "MOVE:Mouse left      SHOOT:Mouse right      Grenade:Space bar     END: Escape";
	}
}
