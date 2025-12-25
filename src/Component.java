public abstract class Component {
    private final Entity entity;

    /**
     * @param entity The entity to which the components belongs to
     */
    protected Component(Entity entity) {
        this.entity = entity;
    }

    /**
     * Update the component using delta time to get constant change with varying fps
     * 
     * @param deltaTime The time since last frame in seconds
     */
    public abstract void update(double deltaTime);

    public Entity getEntity() {
        return this.entity;
    }
}
