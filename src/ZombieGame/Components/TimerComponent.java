package ZombieGame.Components;

import ZombieGame.Entities.Entity;

public class TimerComponent extends Component {
    private double secondes;
    private boolean running;

    public TimerComponent(Entity entity, boolean running) {
        super(entity);
        this.secondes = 0;
        this.running = running;
    }

    public TimerComponent(Entity entity) {
        this(entity, true);
    }

    @Override
    public void update(double deltaTime) {
        if (running) {
            secondes += deltaTime;
        }
    }

    /**
     * Start the timer. It will continue where it currently is
     */
    public void resume() {
        this.running = true;
    }

    /**
     * Pause the timer. Will keep it current time.
     */
    public void pause() {
        this.running = false;
    }

    /**
     * Stops and resets the timer
     */
    public void stop() {
        this.secondes = 0;
        this.running = false;
    }

    public int getMilliseconds() {
        return (int) ((secondes * 1000.0) % 1000.0);
    }

    public int getSeconds() {
        return (int) (secondes % 60.0);
    }

    public int getMinutes() {
        return (int) ((secondes / 60.0) % 60.0);
    }

    public int getHours() {
        return (int) (secondes / 3600.0);
    }
}
