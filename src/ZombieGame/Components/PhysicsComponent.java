package ZombieGame.Components;

import ZombieGame.CircleHitBox;
import ZombieGame.CollisionResponse;
import ZombieGame.GraphicLayer;
import ZombieGame.HitBox;
import ZombieGame.RectangleHitBox;
import ZombieGame.Capabilities.Drawable;
import ZombieGame.Entities.Entity;

public abstract class PhysicsComponent extends Component implements Drawable {
    protected HitBox hitBox;

    /**
     * @param entity The entity to which the components belongs to
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     */
    public PhysicsComponent(Entity entity, HitBox hitBox) {
        super(entity);
        this.hitBox = hitBox;
    }

    /**
     * Checks if the HitBox collides with the HitBox of the other Physics Component.
     * 
     * @param other The other Physics component to check against
     * @return Collision, overlapping or no collision
     */
    public CollisionResponse checkCollision(PhysicsComponent other) {
        if (this.hitBox instanceof CircleHitBox && other.hitBox instanceof CircleHitBox) {
            double dist = ((CircleHitBox) this.hitBox).getRadius() + ((CircleHitBox) other.hitBox).getRadius();
            double dx = (this.getEntity().getPosX() + this.hitBox.getOffsetX()) - (other.getEntity().getPosX() + other.hitBox.getOffsetX());
            double dy = (this.getEntity().getPosY() + this.hitBox.getOffsetY()) - (other.getEntity().getPosY() + other.hitBox.getOffsetY());

            if (dx * dx + dy * dy < dist * dist) {
                return CollisionResponse.CollisionMatrix(this.hitBox, other.hitBox);
            }
            return CollisionResponse.None;
        } else if (this.hitBox instanceof RectangleHitBox && other.hitBox instanceof RectangleHitBox) {
            if (this.getEntity().getPosX() + this.hitBox.getOffsetX() < other.getEntity().getPosX() + other.hitBox.getOffsetX() + ((RectangleHitBox) other.hitBox).getWidth() &&
                    this.getEntity().getPosX() + this.hitBox.getOffsetX() + ((RectangleHitBox) this.hitBox).getWidth() > other.getEntity().getPosX() + other.hitBox.getOffsetX() &&
                    this.getEntity().getPosY() + this.hitBox.getOffsetY() < other.getEntity().getPosY() + other.hitBox.getOffsetY() + ((RectangleHitBox) other.hitBox).getHeight() &&
                    this.getEntity().getPosY() + this.hitBox.getOffsetY() + ((RectangleHitBox) this.hitBox).getHeight() > other.getEntity().getPosY() + other.hitBox.getOffsetY()) {
                return CollisionResponse.CollisionMatrix(this.hitBox, other.hitBox);
            }
            return CollisionResponse.None;
        } else if (this.hitBox instanceof RectangleHitBox && other.hitBox instanceof CircleHitBox) {
            double rectMinX = this.getEntity().getPosX() + this.hitBox.getOffsetX() - ((RectangleHitBox) this.hitBox).getWidth() / 2;
            double rectMinY = this.getEntity().getPosY() + this.hitBox.getOffsetY() - ((RectangleHitBox) this.hitBox).getHeight() / 2;
            double rectMaxX = this.getEntity().getPosX() + this.hitBox.getOffsetX() + ((RectangleHitBox) this.hitBox).getWidth() / 2;
            double rectMaxY = this.getEntity().getPosY() + this.hitBox.getOffsetY() + ((RectangleHitBox) this.hitBox).getHeight() / 2;
            double cx = other.getEntity().getPosX() + other.hitBox.getOffsetX();
            double cy = other.getEntity().getPosY() + other.hitBox.getOffsetY();
            double r = ((CircleHitBox) other.hitBox).getRadius();

            double closestX = Math.max(rectMinX, Math.min(cx, rectMaxX));
            double closestY = Math.max(rectMinY, Math.min(cy, rectMaxY));

            double dx = cx - closestX;
            double dy = cy - closestY;

            if ((dx * dx + dy * dy) <= r * r) {
                return CollisionResponse.CollisionMatrix(this.hitBox, other.hitBox);
            }
            return CollisionResponse.None;
        } else if (this.hitBox instanceof CircleHitBox && other.hitBox instanceof RectangleHitBox) {
            double rectMinX = other.getEntity().getPosX() + other.hitBox.getOffsetX() - ((RectangleHitBox) other.hitBox).getWidth() / 2;
            double rectMinY = other.getEntity().getPosY() + other.hitBox.getOffsetY() - ((RectangleHitBox) other.hitBox).getHeight() / 2;
            double rectMaxX = other.getEntity().getPosX() + other.hitBox.getOffsetX() + ((RectangleHitBox) other.hitBox).getWidth() / 2;
            double rectMaxY = other.getEntity().getPosY() + other.hitBox.getOffsetY() + ((RectangleHitBox) other.hitBox).getHeight() / 2;
            double cx = this.getEntity().getPosX() + this.hitBox.getOffsetX();
            double cy = this.getEntity().getPosY() + this.hitBox.getOffsetY();
            double r = ((CircleHitBox) this.hitBox).getRadius();

            double closestX = Math.max(rectMinX, Math.min(cx, rectMaxX));
            double closestY = Math.max(rectMinY, Math.min(cy, rectMaxY));

            double dx = cx - closestX;
            double dy = cy - closestY;

            if ((dx * dx + dy * dy) <= r * r) {
                return CollisionResponse.CollisionMatrix(this.hitBox, other.hitBox);
            }
            return CollisionResponse.None;
        }

        System.err.println("Tried to check collision for invalid hit box combination!");
        return CollisionResponse.None;
    }

    /**
     * Draw the hit box in the graphics system if {@link PhysicsComponent}.enableDebug is true.
     */
    public abstract void draw();

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.UI;
    }

    @Override
    public int getDepth() {
        return (int) this.getEntity().getPosY();
    }
}
