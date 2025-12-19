import java.awt.Color;
import java.awt.Font;

public class GrenadesCounterText extends TextObject {
    private int number = 0;

    /**
     * @param posX The position in x of the grenades counter on the screen
     * @param posY The position in y of the grenades counter on the screen
     */
    public GrenadesCounterText(double posX, double posY) {
        super(posX, posY, new Color(255, 255, 0, 210), new Font("Arial", Font.PLAIN, 24));
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
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
