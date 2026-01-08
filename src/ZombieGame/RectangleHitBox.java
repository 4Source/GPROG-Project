package ZombieGame;

public class RectangleHitBox extends HitBox {
    private int width, height;

    /**
     * @param type The type of collision the hit box can have
     * @param width The width of the hit box
     * @param height The height of the hit box
     * @param offsetX The offset in x from the entity position to the hit box position
     * @param offsetY The offset in y from the entity position to the hit box position
     */
    public RectangleHitBox(HitBoxType type, int width, int height, int offsetX, int offsetY) {
        super(type, offsetX, offsetY);
        this.width = width;
        this.height = height;
    }

    /**
     * @param type The type of collision the hit box can have
     * @param width The width of the hit box
     * @param height The height of the hit box
     */
    public RectangleHitBox(HitBoxType type, int width, int height) {
        super(type);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
