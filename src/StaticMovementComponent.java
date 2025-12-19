public class StaticMovementComponent extends MovementComponent {

    /**
     * A Movement component which allows only static movement with the initial direction and speed
     * 
     * @param entity The entity to which the components belongs to
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    protected StaticMovementComponent(Entity entity, double alpha, double speed) {
        super(entity, alpha, speed);
        this.isMoving = true;
    }
}
