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
    protected MovementComponent(Entity entity, double alpha, double speed) {
        super(entity);
        this.alpha = alpha;
        this.speed = speed;
    }

    @Override
    public void update(double deltaTime) {
        this.entity.getComponent(PhysicsComponent.class).ifPresent(component -> PhysicsSystem.getInstance().invalidateBufferFor(component));

        // remember old position
        this.oldX = this.entity.posX;
        this.oldY = this.entity.posY;

        // move one step
        this.entity.posX += Math.cos(this.alpha) * this.speed * deltaTime;
        this.entity.posY += Math.sin(this.alpha) * this.speed * deltaTime;
    }

    // TODO: There is possible a better way
    /**
     * Move back to the position before the move Method was called
     */
    public void moveBack() {
        this.entity.posX = this.oldX;
        this.entity.posY = this.oldY;
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
