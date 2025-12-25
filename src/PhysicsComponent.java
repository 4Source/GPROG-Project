enum CollisionResponse {
    /**
     * No collision
     */
    None,
    /**
     * There is a collision but at least one of the hit boxes has the {@link HitBoxType} Overlap
     */
    Overlap,
    /**
     * There is a collision
     */
    Block;

    static CollisionResponse CollisionMatrix(HitBox a, HitBox b) {
        if (a.getCollisionType() == HitBoxType.Block && b.getCollisionType() == HitBoxType.Block) {
            return Block;
        } else if (a.getCollisionType() == HitBoxType.Block && b.getCollisionType() == HitBoxType.Overlap) {
            return Overlap;
        } else if (a.getCollisionType() == HitBoxType.Overlap && b.getCollisionType() == HitBoxType.Block) {
            return Overlap;
        } else if (a.getCollisionType() == HitBoxType.Overlap && b.getCollisionType() == HitBoxType.Overlap) {
            return Overlap;
        } else {
            System.err.println("Tried to check collision for invalid hit box type combination!");
            return None;
        }
    }
}

public abstract class PhysicsComponent extends Component implements Drawable {
    protected HitBox hitBox;

    /**
     * @param entity The entity to which the components belongs to
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     */
    protected PhysicsComponent(Entity entity, HitBox hitBox) {
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
            double dx = this.getEntity().posX + this.hitBox.getOffsetX() - other.getEntity().posX + other.hitBox.getOffsetX();
            double dy = this.getEntity().posY + this.hitBox.getOffsetY() - other.getEntity().posY + other.hitBox.getOffsetY();

            if (dx * dx + dy * dy < dist * dist) {
                return CollisionResponse.CollisionMatrix(this.hitBox, other.hitBox);
            }
            return CollisionResponse.None;
        } else if (this.hitBox instanceof RectangleHitBox && other.hitBox instanceof RectangleHitBox) {
            if (this.getEntity().posX + this.hitBox.getOffsetX() < other.getEntity().posX + other.hitBox.getOffsetX() + ((RectangleHitBox) other.hitBox).getWidth() &&
                    this.getEntity().posX + this.hitBox.getOffsetX() + ((RectangleHitBox) this.hitBox).getWidth() > other.getEntity().posX + other.hitBox.getOffsetX() &&
                    this.getEntity().posY + this.hitBox.getOffsetY() < other.getEntity().posY + other.hitBox.getOffsetY() + ((RectangleHitBox) other.hitBox).getHeight() &&
                    this.getEntity().posY + this.hitBox.getOffsetY() + ((RectangleHitBox) this.hitBox).getHeight() > other.getEntity().posY + other.hitBox.getOffsetY()) {
                return CollisionResponse.CollisionMatrix(this.hitBox, other.hitBox);
            }
            return CollisionResponse.None;
        } else if (this.hitBox instanceof RectangleHitBox && other.hitBox instanceof CircleHitBox) {
            double rectMinX = this.getEntity().posX + this.hitBox.getOffsetX() - ((RectangleHitBox) this.hitBox).getWidth() / 2;
            double rectMinY = this.getEntity().posY + this.hitBox.getOffsetY() - ((RectangleHitBox) this.hitBox).getHeight() / 2;
            double rectMaxX = this.getEntity().posX + this.hitBox.getOffsetX() + ((RectangleHitBox) this.hitBox).getWidth() / 2;
            double rectMaxY = this.getEntity().posY + this.hitBox.getOffsetY() + ((RectangleHitBox) this.hitBox).getHeight() / 2;
            double cx = other.getEntity().posX + other.hitBox.getOffsetX();
            double cy = other.getEntity().posY + other.hitBox.getOffsetY();
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
            double rectMinX = other.getEntity().posX + other.hitBox.getOffsetX() - ((RectangleHitBox) other.hitBox).getWidth() / 2;
            double rectMinY = other.getEntity().posY + other.hitBox.getOffsetY() - ((RectangleHitBox) other.hitBox).getHeight() / 2;
            double rectMaxX = other.getEntity().posX + other.hitBox.getOffsetX() + ((RectangleHitBox) other.hitBox).getWidth() / 2;
            double rectMaxY = other.getEntity().posY + other.hitBox.getOffsetY() + ((RectangleHitBox) other.hitBox).getHeight() / 2;
            double cx = this.getEntity().posX + this.hitBox.getOffsetX();
            double cy = this.getEntity().posY + this.hitBox.getOffsetY();
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
}
