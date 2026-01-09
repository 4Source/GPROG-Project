package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;

import ZombieGame.Coordinates.ViewPos;

public class ZombieCounter extends Counter {
    /**
     * @param pos The position in the world
     */
    public ZombieCounter(ViewPos pos) {
        super(pos, new Color(255, 255, 0, 210), new Font("Arial", Font.PLAIN, 24), 0);
    }

    @Override
    public String toString() {
        String display = "Zombies: ";
        display += this.getNumber();
        return display;
    }
}
