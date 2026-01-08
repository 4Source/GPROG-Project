package ZombieGame.Components;

import java.util.Optional;

import ZombieGame.AIState;
import ZombieGame.CharacterAction;
import ZombieGame.CharacterDirection;
import ZombieGame.PhysicsSystem;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.Zombie;

public class AIMovementComponent extends TargetMovementComponent {
    protected AIState state;
    protected double alphaClear;
    protected double secondsClear;

    /**
     * A movement component which provides "intelligent" movement by different states.
     * 
     * @param entity The entity to which the components belongs to
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    public AIMovementComponent(Entity entity, double alpha, double speed) {
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
        Optional<Avatar> opt = Entity.world.getEntity(Avatar.class);
        if (opt.isEmpty()) {
            System.err.println("No avatar found");
            return;
        }
        Avatar avatar = opt.get();

        // if avatar is too far away: stop
        double dist = PhysicsSystem.distance(this.getEntity().getPosX(), this.getEntity().getPosY(), avatar.getPosX(), avatar.getPosY());

        if (dist > 1500) {
            this.hasDestination = false;
            this.state = AIState.IDLING;
        }

        switch (this.state) {
            case HUNTING:
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
            case IDLING:
                if (dist < 800) {
                    this.state = AIState.HUNTING;
                }
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
