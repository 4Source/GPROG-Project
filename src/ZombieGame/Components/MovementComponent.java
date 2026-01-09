package ZombieGame.Components;

import ZombieGame.PhysicsSystem;
import ZombieGame.Entities.Entity;

public abstract class MovementComponent extends Component {
    protected double alpha;
    protected double speed;
    protected double oldX, oldY;

    /**
     * A Component which allows the entity to move.
     * 
     * @param entity The entity to which the components belongs to
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    public MovementComponent(Entity entity, double alpha, double speed) {
        super(entity);
        this.alpha = alpha;
        this.speed = speed;

        // Initialize rollback position so that if a collision happens before the
        // first call to update(), moveBack() keeps the entity at its spawn
        // position instead of teleporting to (0,0).
        this.oldX = entity.getPosX();
        this.oldY = entity.getPosY();
    }

    @Override
    public void update(double deltaTime) {
        this.getEntity().getComponents(PhysicsComponent.class).forEach(component -> PhysicsSystem.getInstance().invalidateBufferFor(component));

        // remember old position
        this.oldX = this.getEntity().getPosX();
        this.oldY = this.getEntity().getPosY();

        // move one step
        this.getEntity().setPosX(this.getEntity().getPosX() + Math.cos(this.alpha) * this.speed * deltaTime);
        this.getEntity().setPosY(this.getEntity().getPosY() + Math.sin(this.alpha) * this.speed * deltaTime);
    }

    // TODO: There is possible a better way
    /**
     * Move back to the position before the move Method was called
     */
    public void moveBack() {
        this.getEntity().setPosX(this.oldX);
        this.getEntity().setPosY(this.oldY);
    }

    /**
     * Move object "back" reverse alpha until it just does not collide
     * 
     * @param entity The object to move
     */
    // public void moveBack() {
    // double dx = Math.cos(this.alpha);
    // double dy = Math.sin(this.alpha);

    // while (true) {
    // entity.posX -= dx;
    // entity.posY -= dy;

    // if (!PhysicsSystem.getInstance().hasCollision(entity)) {
    // break;
    // }
    // }
    // }
}
