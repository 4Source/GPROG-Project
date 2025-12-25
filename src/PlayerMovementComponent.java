public class PlayerMovementComponent extends MovementComponent {

    /**
     * A Component which can move the entity via the inputs of the user.
     * 
     * @param entity The entity to which the components belongs to
     * @param speed The speed how fast to move
     */
    protected PlayerMovementComponent(Entity entity, double speed) {
        super(entity, 0, speed);
    }

    @Override
    public void update(double deltaTime) {
        InputSystem input = InputSystem.getInstance();
        boolean moved = false;
        double dx = 0;
        double dy = 0;

        if (input.isDown(Action.MOVE_UP)) {
            dy -= 1;
            moved = true;
        }
        if (input.isDown(Action.MOVE_DOWN)) {
            dy += 1;
            moved = true;
        }
        if (input.isDown(Action.MOVE_LEFT)) {
            dx -= 1;
            moved = true;
        }
        if (input.isDown(Action.MOVE_RIGHT)) {
            dx += 1;
            moved = true;
        }

        if (moved) {
            this.alpha = Math.atan2(dy, dx);
            super.update(deltaTime);
        }
    }

    @Override
    public Avatar getEntity() {
        return (Avatar) super.getEntity();
    }
}
