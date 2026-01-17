package ZombieGame.Components;

import ZombieGame.Entities.Entity;

public class LifetimeComponent extends LivingComponent {
    private double lifetime;

    /**
     * A Component which provides a lifetime. When the lifetime falls below 0 the entity dies.
     * 
     * @param entity The entity to which the components belongs to
     * @param lifetime The duration the component live before being destroyed
     * @param onDie A callback function which gets called when the component is marked as dead
     */
    public LifetimeComponent(Entity entity, double lifetime, Runnable onDie) {
        super(entity, onDie);
        this.lifetime = lifetime;
    }

    /**
     * A Component which provides a lifetime. When the lifetime falls below 0 the entity dies.
     * 
     * @param entity The entity to which the components belongs to
     * @param lifetime The duration the component live before being destroyed
     */
    public LifetimeComponent(Entity entity, double lifetime) {
        this(entity, lifetime, () -> {});
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        this.lifetime -= deltaTime;
        if (this.lifetime < 0) {
            this.kill();
        }
    }
}
