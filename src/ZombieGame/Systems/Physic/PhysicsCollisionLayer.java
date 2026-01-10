package ZombieGame.Systems.Physic;

public enum PhysicsCollisionLayer {
    /**
     * Obstacles which could be placed in the world like: trees, walls, ...
     */
    OBSTACLES(1 << 0),
    /**
     * Creatures which could be players or zombies
     */
    CHARACTER(1 << 1),
    /**
     * The Player
     */
    PLAYER(1 << 2),
    /**
     * The Player, will also be registered as CHARACTER
     */
    PLAYER_CHARACTER((1 << 2) | (1 << 1)),
    /**
     * A Zombie
     */
    ZOMBIE(1 << 3),
    /**
     * A Zombie, will also be registered as CHARACTER
     */
    ZOMBIE_CHARACTER((1 << 3) | (1 << 1)),
    /**
     * Projectiles like: bullets, throwables, ...
     */
    PROJECTILE(1 << 4), // bullets
    /**
     * Items which could be collected like: guns, ammo, ...
     */
    ITEM(1 << 5);

    public final int bit;

    PhysicsCollisionLayer(int bit) {
        this.bit = bit;
    }
}
