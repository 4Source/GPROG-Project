package ZombieGame.Components;

import ZombieGame.Capabilities.Drawable;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Systems.Graphic.GraphicLayer;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.CollisionResponse;
import ZombieGame.Systems.Physic.HitBox;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Systems.Physic.RectangleHitBox;

public abstract class PhysicsComponent extends Component implements Drawable {
    protected HitBox hitBox;
    protected final int layer;
    protected final int mask;

    /**
     * @param entity The entity to which the components belongs to
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     * @param layer The layer on which the PhysicsComponent should belong
     * @param mask The layers which the PhysicsComponent could interact with
     */
    public PhysicsComponent(Entity entity, HitBox hitBox, PhysicsCollisionLayer layer, PhysicsCollisionMask mask) {
        super(entity);
        this.hitBox = hitBox;
        this.layer = layer.bit;
        this.mask = mask.bit;
    }

    /**
     * Checks if the HitBox collides with the HitBox of the other Physics Component.
     * 
     * @param other The other Physics component to check against
     * @return Collision, overlapping or no collision
     */
    public CollisionResponse checkCollision(PhysicsComponent other) {
        if (this == other) {
            // No Collision with is self
            return CollisionResponse.None;
        }

        if (this.getEntity() == other.getEntity()) {
            // No Collision between components of same entity
            return CollisionResponse.None;
        }

        if ((this.layer & other.mask) == 0 && (other.layer & this.mask) == 0) {
            // Components can not collide
            return CollisionResponse.None;
        }

        if (this.hitBox instanceof CircleHitBox && other.hitBox instanceof CircleHitBox) {
            double dist = ((CircleHitBox) this.hitBox).getRadius() + ((CircleHitBox) other.hitBox).getRadius();
            WorldPos d2 = this.getEntity().getPositionComponent().getWorldPos().add(this.hitBox.getOffset()).sub(other.getEntity().getPositionComponent().getWorldPos().add(other.hitBox.getOffset())).pow2();

            if (d2.x() + d2.y() < dist * dist) {
                return CollisionResponse.CollisionMatrix(this.hitBox, other.hitBox);
            }
            return CollisionResponse.None;
        } else if (this.hitBox instanceof RectangleHitBox && other.hitBox instanceof RectangleHitBox) {
            WorldPos thisPos = this.getEntity().getPositionComponent().getWorldPos().add(this.hitBox.getOffset());
            WorldPos otherPos = other.getEntity().getPositionComponent().getWorldPos().add(other.hitBox.getOffset());

            if (thisPos.x() < otherPos.x() + ((RectangleHitBox) other.hitBox).getWidth() && thisPos.x() + ((RectangleHitBox) this.hitBox).getWidth() > otherPos.x() &&
                    thisPos.y() < otherPos.y() + ((RectangleHitBox) other.hitBox).getHeight() && thisPos.y() + ((RectangleHitBox) this.hitBox).getHeight() > otherPos.y()) {
                return CollisionResponse.CollisionMatrix(this.hitBox, other.hitBox);
            }
            return CollisionResponse.None;
        } else if (this.hitBox instanceof RectangleHitBox && other.hitBox instanceof CircleHitBox) {
            WorldPos rectMin = this.getEntity().getPositionComponent().getWorldPos().add(this.hitBox.getOffset()).sub(new Offset(((RectangleHitBox) this.hitBox).getWidth() / 2, ((RectangleHitBox) this.hitBox).getHeight() / 2));
            WorldPos rectMax = this.getEntity().getPositionComponent().getWorldPos().add(this.hitBox.getOffset()).add(new Offset(((RectangleHitBox) this.hitBox).getWidth() / 2, ((RectangleHitBox) this.hitBox).getHeight() / 2));
            WorldPos c = other.getEntity().getPositionComponent().getWorldPos().add(other.hitBox.getOffset());
            double r = ((CircleHitBox) other.hitBox).getRadius();

            WorldPos closes = new WorldPos(Math.max(rectMin.x(), Math.min(c.x(), rectMax.x())), Math.max(rectMin.y(), Math.min(c.y(), rectMax.y())));

            WorldPos d2 = c.sub(closes).pow2();

            if ((d2.x() + d2.y()) <= r * r) {
                return CollisionResponse.CollisionMatrix(this.hitBox, other.hitBox);
            }
            return CollisionResponse.None;
        } else if (this.hitBox instanceof CircleHitBox && other.hitBox instanceof RectangleHitBox) {
            WorldPos rectMin = other.getEntity().getPositionComponent().getWorldPos().add(other.hitBox.getOffset()).sub(new Offset(((RectangleHitBox) other.hitBox).getWidth() / 2, ((RectangleHitBox) other.hitBox).getHeight() / 2));
            WorldPos rectMax = other.getEntity().getPositionComponent().getWorldPos().add(other.hitBox.getOffset()).add(new Offset(((RectangleHitBox) other.hitBox).getWidth() / 2, ((RectangleHitBox) other.hitBox).getHeight() / 2));
            WorldPos c = this.getEntity().getPositionComponent().getWorldPos().add(this.hitBox.getOffset());
            double r = ((CircleHitBox) this.hitBox).getRadius();

            WorldPos closes = new WorldPos(Math.max(rectMin.x(), Math.min(c.x(), rectMax.x())), Math.max(rectMin.y(), Math.min(c.y(), rectMax.y())));

            WorldPos d2 = c.sub(closes).pow2();

            if ((d2.x() + d2.y()) <= r * r) {
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
        return (int) this.getEntity().getPositionComponent().getViewPos().y();
    }
}
