public class LifeComponent extends LivingComponent {
    protected double life;

    /**
     * A component which provides life to the entity. Allows to take damage, heal, or die if life falls below 0.
     * 
     * @param entity The entity to which the components belongs to
     * @param life The initial life of the component
     */
    protected LifeComponent(Entity entity, double life) {
        super(entity);
        this.life = life;
    }

    /**
     * Reduce the life by damage
     */
    public void takeDamage(double damage) {
        // every shot decreases life
        this.life -= damage;

        if (this.life <= 0) {
            this.kill();
        }
    }

    /**
     * Increases the life by health
     */
    public void restoreHealth(double health) {
        this.life += health;
    }

    @Override
    public void update(double deltaTime) {
    }
}
