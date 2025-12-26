package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;

public class GrenadesCounter extends Counter {
    /**
     * @param posX The position in x of the grenades counter on the screen
     * @param posY The position in y of the grenades counter on the screen
     * @param grenades The initial number of grenades
     */
    public GrenadesCounter(double posX, double posY, int grenades) {
        super(posX, posY, new Color(255, 255, 0, 210), new Font("Arial", Font.PLAIN, 24), grenades);
    }

    @Override
    public String toString() {
        String display = "Grenades: ";
        display += this.getNumber();
        return display;
    }
}
