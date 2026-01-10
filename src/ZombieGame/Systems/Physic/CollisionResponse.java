package ZombieGame.Systems.Physic;

public enum CollisionResponse {
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

    public static CollisionResponse CollisionMatrix(HitBox a, HitBox b) {
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