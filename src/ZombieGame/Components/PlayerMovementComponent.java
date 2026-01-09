package ZombieGame.Components;

import ZombieGame.Action;
import ZombieGame.CharacterAction;
import ZombieGame.CharacterDirection;
import ZombieGame.InputSystem;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Entity;

public class PlayerMovementComponent extends MovementComponent {

    /**
     * A Component which can move the entity via the inputs of the user.
     * 
     * @param entity The entity to which the components belongs to
     * @param pos The position in the world
     * @param speed The speed how fast to move
     */
    public PlayerMovementComponent(Entity entity, WorldPos pos, double speed) {
        super(entity, pos, 0, speed);
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
            this.getEntity().getVisualComponent().changeState(CharacterAction.MOVE);

            this.alpha = Math.atan2(dy, dx); // range: [-PI, PI]

            // RIGHT: include ±45°
            if (alpha >= -Math.PI / 4 && alpha <= Math.PI / 4) {
                this.getEntity().getVisualComponent().changeState(CharacterDirection.RIGHT);
            }
            // DOWN: strictly between 45° and 135°
            else if (alpha > Math.PI / 4 && alpha < 3 * Math.PI / 4) {
                this.getEntity().getVisualComponent().changeState(CharacterDirection.DOWN);
            }
            // UP: strictly between -135° and -45°
            else if (alpha > -3 * Math.PI / 4 && alpha < -Math.PI / 4) {
                this.getEntity().getVisualComponent().changeState(CharacterDirection.UP);
            }
            // LEFT: include ±135°
            else {
                this.getEntity().getVisualComponent().changeState(CharacterDirection.LEFT);
            }

            super.update(deltaTime);
        } else if (this.getEntity().getVisualComponent().getCharacterAction() == CharacterAction.MOVE) {
            this.getEntity().getVisualComponent().changeState(CharacterAction.IDLE);
        }
    }

    @Override
    public Avatar getEntity() {
        return (Avatar) super.getEntity();
    }
}
