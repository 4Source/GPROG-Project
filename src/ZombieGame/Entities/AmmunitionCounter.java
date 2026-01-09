package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;

public class AmmunitionCounter extends Counter {
    /**
     * @param posX The position in x of the grenades counter on the screen
     * @param posY The position in y of the grenades counter on the screen
     * @param ammunition The initial number of ammunition
     */
    public AmmunitionCounter(double posX, double posY, int ammunition) {
        super(posX, posY, new Color(255, 255, 0, 210), new Font("Arial", Font.PLAIN, 24), ammunition);
    }

    @Override
    public String toString() {
        String display = "Ammunition: ";
        display += this.getNumber();
        return display;
    }
}
