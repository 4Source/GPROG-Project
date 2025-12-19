public class PlayerMovementComponent extends TargetMovementComponent {

    /**
     * A Component which can move the entity via the inputs of the user.
     * 
     * @param entity The entity to which the components belongs to
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    protected PlayerMovementComponent(Entity entity, double alpha, double speed) {
        super(entity, alpha, speed);
    }

    @Override
    public void update(double deltaTime) {
        // move Avatar one step forward
        super.update(deltaTime);
    }

    // TODO: Should be controlled via the inputs
}
