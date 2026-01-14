package ZombieGame.Components;

import ZombieGame.Entities.Entity;

public class TimerComponent extends Component {
    private double secondes;

    public TimerComponent(Entity entity) {
        super(entity);
        this.secondes = 0;
    }

    @Override
    public void update(double deltaTime) {
        secondes += deltaTime;
    }

    public void reset() {
        this.secondes = 0;
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
