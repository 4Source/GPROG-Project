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
     * Show hit boxes
     */
    SHOW_HIT_BOXES(null),
    /**
     * Show fps
     */
    SHOW_FPS(null);

    private final String label;

    Action(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
