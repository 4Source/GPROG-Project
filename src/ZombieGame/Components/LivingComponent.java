package ZombieGame.Components;

import ZombieGame.Entities.Entity;

public class LivingComponent extends Component {
    private boolean isLiving;
    private Runnable onDie;

    /**
     * A Component which allows a entity to die.
     * Dead entity automatically will be removed.
     * 
     * @param entity The entity to which the components belongs to
     * @param onDie A callback function which gets called when the component is marked as dead
     */
    public LivingComponent(Entity entity, Runnable onDie) {
        super(entity);
        this.isLiving = true;
        this.onDie = onDie;
    }

    /**
     * A Component which allows a entity to die.
     * Dead entity automatically will be removed.
     * 
     * @param entity The entity to which the components belongs to
     */
    public LivingComponent(Entity entity) {
        this(entity, () -> {});
    }

    public boolean isLiving() {
        return this.isLiving;
    }

    public void kill() {
        this.isLiving = false;
        this.onDie.run();
    }

    @Override
    public void update(double deltaTime) {
    }
}
