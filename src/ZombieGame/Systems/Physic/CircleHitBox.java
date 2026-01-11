package ZombieGame.Systems.Physic;

import ZombieGame.Coordinates.Offset;

public class CircleHitBox extends HitBox {
    private int radius;

    /**
     * @param type The type of collision the hit box can have
     * @param radius The size of the hit box
     * @param offset The offset from the entity position to the hit box position
     */
    public CircleHitBox(HitBoxType type, int radius, Offset offset) {
        super(type, offset);
        this.radius = radius;
    }

    /**
     * @param type The type of collision the hit box can have
     * @param radius The size of the hit box
     */
    public CircleHitBox(HitBoxType type, int radius) {
        super(type);
        this.radius = radius;
    }

    public int getRadius() {
        return this.radius;
    }
}
