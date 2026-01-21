package ZombieGame.Components;

import java.util.Optional;

import ZombieGame.AIState;
import ZombieGame.CharacterAction;
import ZombieGame.CharacterDirection;
import ZombieGame.Game;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Zombie;

public class AIMovementComponent extends TargetMovementComponent {
    protected AIState state;
    protected double alphaClear;
    protected double secondsClear;

    /**
     * A movement component which provides "intelligent" movement by different states.
     * 
     * @param entity The entity to which the components belongs to
     * @param pos The position in the world
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    public AIMovementComponent(Zombie entity, WorldPos pos, double alpha, double speed) {
        super(entity, pos, alpha, speed);
        this.state = AIState.HUNTING;

        // turn left or right to clear
        this.alphaClear = Math.PI;
        if (Math.random() < 0.5) {
            this.alphaClear = -this.alphaClear;
        }
    }

    @Override
    public void update(double deltaTime) {

        Optional<Avatar> opt = Game.world.getEntity(Avatar.class);
        if (opt.isEmpty()) {
            System.err.println("No avatar found");
            return;
        }
        Avatar avatar = opt.get();

        this.getEntity().getPrimaryAttackComponent().setTarget(avatar);
        AttackComponent c = this.getEntity().getSecondaryAttackComponent();
        if (c != null) {
            c.setTarget(avatar);
        }

        switch (this.state) {
            case HUNTING:
                // Update the destination to avatars current position
                this.setDestination(avatar);

                super.update(deltaTime);
                break;
            case STUCK:
                // seconds left for clearing
                this.secondsClear = 1.0 + Math.random() * 0.5;
                // turn and hope to get clear
                this.alpha += this.alphaClear * deltaTime;
                this.hasDestination = true;

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
            case ATTACKING:
                this.hasDestination = false;
                break;
            default:
                System.err.println("Unknown state: " + this.state);
                break;
        }

        if (this.hasDestination) {
            this.getEntity().getVisualComponent().changeState(CharacterAction.MOVE);

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
        } else if (this.getEntity().getVisualComponent().getCharacterAction() == CharacterAction.MOVE) {
            this.getEntity().getVisualComponent().changeState(CharacterAction.IDLE);
        }

    }

    public AIState getState() {
        return state;
    }

    public void setState(AIState state) {
        this.state = state;
    }

    @Override
    public Zombie getEntity() {
        return (Zombie) super.getEntity();
    }
}
