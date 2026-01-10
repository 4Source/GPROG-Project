package ZombieGame.Systems.Physic;

import ZombieGame.Coordinates.Offset;

public abstract class HitBox {
    private HitBoxType collisionType;
    private Offset offset;

    /**
     * @param type The type of the collision the HitBox allows
     * @param offset The offset from the entity position to the hit box position
     */
    protected HitBox(HitBoxType type, Offset offset) {
        this.collisionType = type;
        this.offset = offset;
    }

    /**
     * @param type The type of the collision the HitBox allows
     */
    protected HitBox(HitBoxType type) {
        this.collisionType = type;
        this.offset = new Offset();
    }

    public HitBoxType getCollisionType() {
        return this.collisionType;
    }

    public Offset getOffset() {
        return this.offset;
    }
}
