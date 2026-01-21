package ZombieGame.Systems.Physic;

public enum PhysicsCollisionLayer {
    /**
     * Obstacles which could be placed in the world like: trees, walls, ...
     */
    OBSTACLES(1 << 0),
    /**
     * Bodies which could collide during movement
     */
    BODY(1 << 1),
    /**
     * Can Take damage
     */
    HURTBOX(1 << 2),
    /**
     * Projectiles like: bullets, throwables, ...
     */
    PROJECTILE(1 << 3),
    /**
     * vision, triggers, attack ranges
     */
    SENSOR(1 << 4),
    /**
     * Items which could be collected like: guns, ammo, ...
     */
    ITEM(1 << 5);

    public final int bit;

    PhysicsCollisionLayer(int bit) {
        this.bit = bit;
    }
}