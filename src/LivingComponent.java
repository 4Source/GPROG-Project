public abstract class LivingComponent extends Component {
    protected boolean isLiving;

    /**
     * A Component which allows a entity to die.
     * Dead entity automatically will be removed.
     * 
     * @param entity The entity to which the components belongs to
     */
    protected LivingComponent(Entity entity) {
        super(entity);
        this.isLiving = true;
    }
}
