package ZombieGame.Systems.Input;

public enum Action {
    /**
     * Move up
     */
    MOVE_UP("Move up"),
    /**
     * Move down
     */
    MOVE_DOWN("Move down"),
    /**
     * Move left
     */
    MOVE_LEFT("Move left"),
    /**
     * Move right
     */
    MOVE_RIGHT("Move right"),
    /**
     * Shoot
     */
    SHOOT("Shoot"),
    /**
     * Reload
     */
    RELOAD("Reload"),
    /**
     * Interact
     */
    INTERACT("Interact"),
    /**
     * Game pause
     */
    GAME_PAUSE("Game pause"),
    /**
     * Primary click
     */
    PRIMARY_CLICK(null),
    /**
     * Show debug physics info
     */
    DEBUG_PHYSICS(null),
    /**
     * Show debug AI info
     */
    DEBUG_AI(null),
    /**
     * Show debug world info
     */
    DEBUG_WORLD(null),
    /**
     * Show debug collision info
     */
    DEBUG_COLLISION(null),
    /**
     * Show debug performance info
     */
    DEBUG_PERFORMANCE(null),
    /**
     * Show debug ui info
     */
    DEBUG_UI(null);

    private final String label;

    Action(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
