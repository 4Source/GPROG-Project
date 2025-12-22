public enum Action {
    MOVE_UP("Move up"), 
    MOVE_DOWN("Move down"), 
    MOVE_LEFT("Move left"), 
    MOVE_RIGHT("Move right"), 
    SHOOT("Shoot"), 
    RELOAD("Reload"), 
    INTERACT("Interact"), 
    THROW_GRENADE("Throw grenade"), 
    GAME_PAUSE("Game pause"), 
    PRIMARY_CLICK(null);

    private final String label;

    Action(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
