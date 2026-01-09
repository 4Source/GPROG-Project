package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;

import ZombieGame.Coordinates.ViewPos;

public class AmmunitionCounter extends Counter {
    /**
     * @param pos The position in the world
     * @param ammunition The initial number of ammunition
     */
    public AmmunitionCounter(ViewPos pos, int ammunition) {
        super(pos, new Color(255, 255, 0, 210), new Font("Arial", Font.PLAIN, 24), ammunition);
    }

    @Override
    public String toString() {
        String display = "Ammunition: ";
        display += this.getNumber();
        return display;
    }
}
