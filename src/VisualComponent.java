public abstract class VisualComponent extends Component {

    /**
     * A Component which allows the graphic system to visualize the entity.
     * 
     * @param entity The entity to which the components belongs to
     */
    protected VisualComponent(Entity entity) {
        super(entity);
    }

    /**
     * Draw the component in the graphics system.
     */
    public abstract void draw();
}
