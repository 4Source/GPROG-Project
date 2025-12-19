import java.awt.Color;

public abstract class Item extends Entity {
    protected CircleComponent circleComponent;
    protected PhysicsComponent physicsComponent;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param radius The size of the Item
     * @param color The color of the Item
     */
    public Item(double posX, double posY, int radius, Color color) {
        super(posX, posY);
        this.circleComponent = this.add(new CircleComponent(this, radius, color));
        this.physicsComponent = this.add(new StaticPhysicsComponent(this, new CircleHitBox(HitBoxType.Overlap, radius)));
    }
}
