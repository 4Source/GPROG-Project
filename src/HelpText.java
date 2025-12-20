import java.awt.Color;
import java.awt.Font;

public class HelpText extends TextElement {
    private LifetimeComponent lifetimeComponent;

    /**
     * @param posX The position in x of the grenades counter on the screen
     * @param posY The position in y of the grenades counter on the screen
     * @param visibilityLifetime The time how long the text should be visible before disappearing
     */
    public HelpText(double posX, double posY, double visibilityLifetime) {
        super(posX, posY, new Color(0, 120, 255, 60), new Font("Arial", Font.PLAIN, 24));
        this.lifetimeComponent = this.add(new LifetimeComponent(this, visibilityLifetime));
    }

    public LifetimeComponent getLifetimeComponent() {
        return this.lifetimeComponent;
    }

    @Override
    public String toString() {
        return "MOVE:Mouse left      SHOOT:Mouse right      Grenade:Space bar     END: Escape";
    }
}
