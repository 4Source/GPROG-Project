enum HitBoxType {
    Block, Overlap
}

public abstract class HitBox {
    private HitBoxType collisionType;
    private int offsetX, offsetY;

    /**
     * @param type The type of the collision the HitBox allows
     * @param offsetX The offset in x from the entity position to the hit box position
     * @param offsetY The offset in y from the entity position to the hit box position
     */
    protected HitBox(HitBoxType type, int offsetX, int offsetY) {
        this.collisionType = type;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * @param type The type of the collision the HitBox allows
     */
    protected HitBox(HitBoxType type) {
        this.collisionType = type;
        this.offsetX = 0;
        this.offsetY = 0;
    }

    public HitBoxType getCollisionType() {
        return this.collisionType;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }
}
