public class TargetMovementComponent extends MovementComponent {
    protected double destX, destY;
    protected boolean hasDestination;

    /**
     * A movement component which will move to a given destination.
     * 
     * @param entity The entity to which the components belongs to
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    protected TargetMovementComponent(Entity entity, double alpha, double speed) {
        super(entity, alpha, speed);
        this.hasDestination = false;
    }

    /**
     * @param entity The entity to which the components belongs to
     * @param speed The speed how fast to move
     * @param destinationX The world position x where to move
     * @param destinationY The world position y where to move
     */
    protected TargetMovementComponent(Entity entity, double speed, double destinationX, double destinationY) {
        super(entity, Math.atan2(destinationY - entity.posY, destinationX - entity.posX), speed);
        this.hasDestination = true;
    }

    @Override
    public void update(double deltaTime) {
        // move if object has a destination
        if (this.hasDestination) {
            // stop if destination is reached
            double diffX = Math.abs(this.entity.posX - this.destX);
            double diffY = Math.abs(this.entity.posY - this.destY);
            if (diffX < 3 && diffY < 3) {
                this.isMoving = false;
                return;
            }
        }

        super.update(deltaTime);
    }

    /**
     * Set a point in the world as destination
     * 
     * @param destinationX The world position x where to move
     * @param destinationY The world position y where to move
     */
    public void setDestination(double destinationX, double destinationY) {
        this.isMoving = true;
        this.hasDestination = true;
        this.destX = destinationX;
        this.destY = destinationY;

        this.alpha = Math.atan2(destinationY - this.entity.posY, destinationX - this.entity.posX);
    }

    /**
     * Set the location of an object as destination
     * 
     * @param destination The object where to move to
     */
    public void setDestination(GameObject destination) {
        setDestination(destination.posX, destination.posY);
    }
}
