enum HitBoxType {
    Block, Overlap
}

public abstract class HitBox {
    protected HitBoxType collisionType;

    /**
     * @param type The type of the collision the HitBox allows
     */
    protected HitBox(HitBoxType type) {
        this.collisionType = type;
    }

    public HitBoxType getCollisionType() {
        return this.collisionType;
    }
}
