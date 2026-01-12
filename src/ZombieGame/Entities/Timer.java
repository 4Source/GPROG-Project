package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;

import ZombieGame.Components.TimerComponent;
import ZombieGame.Coordinates.ViewPos;

public class Timer extends TextElement {
    private final TimerComponent timerComponent;
    private final String format;

    /**
     * @param pos The position in the world
     * @param format A format string how the time shool be displayed
     */
    public Timer(ViewPos pos, String format) {
        super(pos, new Color(255, 255, 0, 210), new Font("Arial", Font.PLAIN, 24));
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
