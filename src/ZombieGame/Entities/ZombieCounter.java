package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;

public class ZombieCounter extends Counter {
    /**
     * @param posX The position in x of the zombie counter on the screen
     * @param posY The position in y of the zombie counter on the screen
     */
    public ZombieCounter(double posX, double posY) {
        super(posX, posY, new Color(255, 255, 0, 210), new Font("Arial", Font.PLAIN, 24), 0);
    }

    @Override
    public String toString() {
        String display = "Zombies: ";
        display += this.getNumber();
        return display;
    }
}
