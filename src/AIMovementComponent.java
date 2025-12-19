enum AIState {
    HUNTING, STUCK, CLEARING
}

public class AIMovementComponent extends TargetMovementComponent {
    protected AIState state;
    private double alphaClear;
    private double secondsClear;

    /**
     * A movement component which provides "intelligent" movement by different states.
     * 
     * @param entity The entity to which the components belongs to
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    protected AIMovementComponent(Entity entity, double alpha, double speed) {
        super(entity, alpha, speed);
        this.state = AIState.HUNTING;

        // turn left or right to clear
        this.alphaClear = Math.PI;
        if (Math.random() < 0.5) {
            this.alphaClear = -this.alphaClear;
        }
    }

    @Override
    public void update(double deltaTime) {
        // if avatar is too far away: stop
        double dist = PhysicsSystem.distance(this.entity.posX, this.entity.posY, GameObject.world.avatar.posX, GameObject.world.avatar.posY);

        if (dist > 1000) {
            this.isMoving = false;
            return;
        } else {
            this.isMoving = true;
        }

        switch (this.state) {
            case HUNTING:
                this.setDestination(GameObject.world.avatar);

                super.update(deltaTime);
                break;
            case STUCK:
                // seconds left for clearing
                this.secondsClear = 1.0 + Math.random() * 0.5;
                // turn and hope to get clear
                this.alpha += this.alphaClear * deltaTime;

                // try to clear
                this.state = AIState.CLEARING;
                break;
            case CLEARING:
                // check, if the clearing time has ended
                this.secondsClear -= deltaTime;
                if (this.secondsClear < 0) {
                    this.state = AIState.HUNTING;
                    return;
                }

                // try step in this direction
                super.update(deltaTime);
                break;

            default:
                System.err.print("Unknown state: " + this.state);
                break;
        }
    }

}
