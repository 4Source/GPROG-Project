import java.awt.Color;
import java.awt.Font;

public class ZombieCounterText extends TextObject {
    private int number = 0;

    /**
     * @param posX The position in x of the zombie counter on the screen
     * @param posY The position in y of the zombie counter on the screen
     */
    public ZombieCounterText(double posX, double posY) {
        super(posX, posY, new Color(255, 255, 0, 210), new Font("Arial", Font.PLAIN, 24));
    }

    @Override
    public String toString() {
        String display = "Zombies: ";
        display += this.number;
        return display;
    }

    @Override
    public void update(double deltaTime) {
    }

    /**
     * Increases the counter
     */
    public void increment() {
        this.number++;
    }
}
