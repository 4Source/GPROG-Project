package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;

import ZombieGame.Components.TimerComponent;
import ZombieGame.Coordinates.ViewPos;

public class Timer extends TextElement {
    private final TimerComponent timerComponent;
    private final String format;

    private static Font BASE_FONT;

    private static Font getPixelFont(float size) {
        try {
            if (BASE_FONT == null) {
                InputStream is = Timer.class.getClassLoader().getResourceAsStream("assets/fonts/PressStart2P-Regular.ttf");

                if (is == null) {
                    throw new RuntimeException("Font not found");
                }

                BASE_FONT = Font.createFont(Font.TRUETYPE_FONT, is);
            }
            return BASE_FONT.deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Monospaced", Font.BOLD, (int) size); // Fallback
        }
    }

    /**
     * @param pos The position in the world
     * @param format A format string how the time shool be displayed
     */
    public Timer(ViewPos pos, String format) {
        super(pos, new Color(245, 245, 235, 230), getPixelFont(26f));
        this.timerComponent = add(new TimerComponent(this));
        this.format = format;
    }

    /**
     * @param pos The position in the world
     */
    public Timer(ViewPos pos) {
        this(pos, "%02d:%02d:%02d");
    }

    public TimerComponent getTimerComponent() {
        return this.timerComponent;
    }

    @Override
    public String toString() {
        return String.format(format, timerComponent.getHours(), timerComponent.getMinutes(), timerComponent.getSeconds(), timerComponent.getMilliseconds());
    }
}
