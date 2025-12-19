public class RectangleHitBox extends HitBox {
    private int width, height;

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
