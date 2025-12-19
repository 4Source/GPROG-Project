import java.awt.Color;

public abstract class Creature extends Entity {
    protected CircleComponent circleComponent;
    protected PhysicsComponent physicsComponent;
    protected MovementComponent movementComponent;
    protected LifeComponent lifeComponent;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param radius The size of the game object
     * @param color The color of the game object
     */
    public Creature(double posX, double posY, int radius, Color color) {
        super(posX, posY);
        this.circleComponent = this.add(new CircleComponent(this, radius, color));
        this.physicsComponent = this.add(new DynamicPhysicsComponent(this, new CircleHitBox(HitBoxType.Block, radius), collision -> onCollisionStart(collision), collision -> onCollisionEnd(collision)));
    }

    /**
     * The Callback function which gets executed if a collision with another entity starts
     * 
     * @param collision The collision which started
     */
    protected abstract void onCollisionStart(Collision collision);

    /**
     * The Callback function which gets executed if a collision with another entity ends
     * 
     * @param collision The collision which ended
     */
    protected abstract void onCollisionEnd(Collision collision);
}
