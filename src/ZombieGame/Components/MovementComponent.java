package ZombieGame.Components;

import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Systems.Physic.PhysicsSystem;

public abstract class MovementComponent extends WorldPositionComponent {
    protected double alpha;
    protected double speed;
    protected WorldPos old;

    /**
     * A Component which allows the entity to move.
     * 
     * @param entity The entity to which the components belongs to
     * @param pos The position in the world
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    public MovementComponent(Entity entity, WorldPos pos, double alpha, double speed) {
        super(entity, pos);
        this.alpha = alpha;
        this.speed = speed;

        // Initialize rollback position so that if a collision happens before the
        // first call to update(), moveBack() keeps the entity at its spawn
        // position instead of teleporting to (0,0).
        this.old = pos;
    }

    @Override
    public void update(double deltaTime) {
        // remember old position
        this.old = this.getWorldPos();

        // move one step
        this.setWorldPos(this.getWorldPos().add(Math.cos(this.alpha) * this.speed * deltaTime, Math.sin(this.alpha) * this.speed * deltaTime));
    }

    /**
     * Move back to a position between current position and old position where not colliding with the other entity
     * 
     * @param otherEntity The object with which the collision occurred
     */
    public void resolveCollision(Entity otherEntity) {
        double lo = 0.0;
        double hi = 1.0;

        for (int i = 0; i < 10; i++) {
            double mid = (lo + hi) * 0.5;

            WorldPos newPos = this.old.add(this.pos.sub(this.old).mul(mid));
            this.setWorldPos(newPos);

            if (PhysicsSystem.hasCollisionWith(this.getEntity(), otherEntity)) {
                // Still colliding -> go backwards
                hi = mid;
            } else {
                // Not colliding -> go forwards
                lo = mid;

                // Exit early because of minimal change only when not colliding to prevent creeping-into-collision problem
                if (hi - lo < 0.0001) {
                    break;
                }
            }
        }

        this.old = this.pos;
    }
}
