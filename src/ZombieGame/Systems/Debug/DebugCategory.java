package ZombieGame.Systems.Debug;

public enum DebugCategory {
    /**
     * Performance and engine diagnostics.
     * Includes:
     * <ul>
     * <li>Frame time and FPS</li>
     * <li>Update and render durations</li>
     * <li>Chunk generation time</li>
     * <li>Entity and component counts</li>
     * <li>Memory and allocation tracking</li>
     * </ul>
     */
    PERFORMANCE(1 << 0),
    /**
     * World generation, streaming, and spatial structure.
     * Includes:
     * <ul>
     * <li>Chunk boundaries and coordinates</li>
     * <li>Loaded / unloaded chunk state</li>
     * <li>Procedural generation markers</li>
     * <li>Tile grids, terrain, world partitions</li>
     * </ul>
     */
    WORLD(1 << 1),
    /**
     * Physics simulation and spatial representation.
     * Includes:
     * <ul>
     * <li>Collision shapes (hitboxes, bounding boxes, sensors)</li>
     * <li>Velocity, acceleration, and forces</li>
     * <li>Static vs dynamic bodies</li>
     * <li>Movement constraints and penetration resolution</li>
     * <li>Physics-related debug geometry</li>
     * </ul>
     */
    PHYSICS(1 << 2),
    /**
     * Collision detection and interaction between objects.
     * Includes:
     * <ul>
     * <li>Contact points</li>
     * <li>Overlap tests</li>
     * <li>Collision pairs</li>
     * <li>Trigger and sensor activation</li>
     * <li>Hit events (who touched whom)</li>
     * *
     * </ul>
     */
    COLLISION(1 << 3),
    /**
     * Artificial intelligence state and decision logic.
     * Includes:
     * <ul>
     * <li>Current AI state (idle, chasing, attacking, fleeing)</li>
     * <li>Pathfinding, targets, waypoints</li>
     * <li>Vision cones, detection ranges, aggro logic</li>
     * <li>Behavior tree or state machine data</li>
     * </ul>
     */
    AI(1 << 4);

    public final int bit;

    private DebugCategory(int bit) {
        this.bit = bit;
    }

    public static int size() {
        return values().length;
    }
}
