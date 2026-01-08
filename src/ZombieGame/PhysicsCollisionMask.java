package ZombieGame;

public final class PhysicsCollisionMask {
    public final int bit;

    public PhysicsCollisionMask(PhysicsCollisionLayer layer, PhysicsCollisionLayer... layers) {
        int value = layer.bit;
        for (PhysicsCollisionLayer l : layers) {
            value |= l.bit;
        }
        this.bit = value;
    }

    private PhysicsCollisionMask(int bit) {
        this.bit = bit;
    }

    /**
     * All Layers will match
     */
    public static PhysicsCollisionMask ALL() {
        return new PhysicsCollisionMask(-1);
    }
}