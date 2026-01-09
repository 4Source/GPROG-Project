package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;

import ZombieGame.Coordinates.ViewPos;

public abstract class Counter extends TextElement {
    private int number;

    /**
     * @param pos The position in the world
     * @param color The color of the ui object
     * @param font The font to use to print the text to the screen
     * @param number The initial number of the counter
     */
    public Counter(ViewPos pos, Color color, Font font, int number) {
        super(pos, color, font);
        this.number = number;
    }

    public final int getNumber() {
        return this.number;
    }

    public final void setNumber(int number) {
        this.number = number;
    }

    public final int increment() {
        this.number++;
        return this.number;
    }

    public final int decrement() {
        this.number--;
        return this.number;
    }
}
