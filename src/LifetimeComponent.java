public class LifetimeComponent extends LivingComponent {
    protected double lifetime;

    /**
     * A Component which provides a lifetime. When the lifetime falls below 0 the entity dies.
     * 
     * @param entity The entity to which the components belongs to
     * @param lifetime The duration the component live before being destroyed
     */
    protected LifetimeComponent(Entity entity, double lifetime) {
        super(entity);
        this.lifetime = lifetime;
    }

    @Override
    public void update(double deltaTime) {
        this.lifetime -= deltaTime;
        if (this.lifetime < 0) {
            this.kill();
        }
    }
}
