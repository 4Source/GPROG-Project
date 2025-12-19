public abstract class Component {
    protected final Entity entity;

    /**
     * @param entity The entity to which the components belongs to
     */
    protected Component(Entity entity) {
        this.entity = entity;
    }

    /**
     * Update the component using delta time to get constant change with varying fps
     * 
     * @param deltaTime The time since last frame
     */
    public abstract void update(double deltaTime);
}
