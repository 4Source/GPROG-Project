package ZombieGame.Components;

import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Systems.Physic.PhysicsSystem;

public abstract class MovementComponent extends WorldPositionComponent {
    protected double alpha;
    protected double speed;
    protected WorldPos old;

    /**
     * Intended movement delta from the last {@link #update(double)} call (before collision resolution).
     * This is useful for push / shove interactions.
     */
    protected WorldPos lastStepDelta = new WorldPos(0, 0);

    /** True if the entity attempted to move in the last update step. */
    protected boolean movedThisFrame = false;

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
        WorldPos delta = new WorldPos(Math.cos(this.alpha) * this.speed * deltaTime, Math.sin(this.alpha) * this.speed * deltaTime);
        this.lastStepDelta = delta;
        this.movedThisFrame = (Math.abs(delta.x()) > 1e-9) || (Math.abs(delta.y()) > 1e-9);
        this.setWorldPos(this.getWorldPos().add(delta));
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

    /**
     * Tries to move this entity along {@code delta} just far enough to no longer collide with {@code target},
     * while also preventing new blocking collisions with any other registered entity.
     *
     * This is the core of "pushing" (avatar pushes zombies, zombies push each other) without allowing overlap.
     *
     * @param target The entity we want to get out of (must currently collide with it).
     * @param delta  Preferred direction and maximum distance for the separation move.
     * @return The fraction (0..1) of {@code delta} that was applied.
     */
    public double pushOutOf(Entity target, WorldPos delta) {
        if (target == null) {
            return 0.0;
        }

        // If delta is (near) zero, fall back to the direction away from the target.
        double len = delta.length();
        if (len < 1e-6) {
            WorldPos away = this.getWorldPos().sub(target.getPositionComponent().getWorldPos());
            double aLen = away.length();
            if (aLen < 1e-6) {
                away = new WorldPos(1, 0);
                aLen = 1;
            }
            delta = away.div(aLen).mul(12);
            len = delta.length();
        }

        WorldPos start = this.getWorldPos();

        // Quick feasibility check at full delta.
        this.setWorldPos(start.add(delta));
        boolean validAtFull = !PhysicsSystem.hasCollisionWith(this.getEntity(), target)
                && !PhysicsSystem.getInstance().hasBlockingCollisionExcept(this.getEntity(), target);

        if (!validAtFull) {
            // Restore and abort if we cannot separate in the preferred direction.
            this.setWorldPos(start);
            return 0.0;
        }

        // Find the minimum displacement along delta that resolves the collision with target.
        double lo = 0.0; // invalid (still colliding)
        double hi = 1.0; // valid
        for (int i = 0; i < 12; i++) {
            double mid = (lo + hi) * 0.5;
            this.setWorldPos(start.add(delta.mul(mid)));

            boolean valid = !PhysicsSystem.hasCollisionWith(this.getEntity(), target)
                    && !PhysicsSystem.getInstance().hasBlockingCollisionExcept(this.getEntity(), target);

            if (valid) {
                hi = mid;
            } else {
                lo = mid;
            }
        }

        this.setWorldPos(start.add(delta.mul(hi)));
        // Keep rollback position consistent to avoid "teleport" artifacts in the next frame.
        this.old = this.pos;
        return hi;
    }

    /**
     * @return true if this entity attempted to move in the current update step.
     */
    public boolean hasMoved() {
        return this.movedThisFrame;
    }

    /**
     * @return Intended movement delta (before collision resolution) from the last update step.
     */
    public WorldPos getLastStepDelta() {
        return this.lastStepDelta;
    }
}
