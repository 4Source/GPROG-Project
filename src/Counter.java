import java.awt.Color;
import java.awt.Font;

public abstract class Counter extends TextElement {
    private int number;

    /**
     * @param posX The position in x of the grenades counter on the screen
     * @param posY The position in y of the grenades counter on the screen
     * @param color The color of the ui object
     * @param font The font to use to print the text to the screen
     * @param number The initial number of the counter
     */
    public Counter(double posX, double posY, Color color, Font font, int number) {
        super(posX, posY, color, font);
        this.number = number;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int increment() {
        this.number++;
        return this.number;
    }

    public int decrement() {
        this.number--;
        return this.number;
    }
}
