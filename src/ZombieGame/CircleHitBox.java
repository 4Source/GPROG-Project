package ZombieGame;

public class CircleHitBox extends HitBox {
    private int radius;

    /**
     * @param type The type of collision the hit box can have
     * @param radius The size of the hit box
     * @param offsetX The offset in x from the entity position to the hit box position
     * @param offsetY The offset in y from the entity position to the hit box position
     */
    public CircleHitBox(HitBoxType type, int radius, int offsetX, int offsetY) {
        super(type, offsetX, offsetY);
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
