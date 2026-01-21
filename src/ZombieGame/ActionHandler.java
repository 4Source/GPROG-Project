package ZombieGame;

import java.util.function.Consumer;

public sealed interface ActionHandler permits ActionHandler.Down, ActionHandler.Pressed {

    /**
     * The function which should be executed if Action key is pressed
     */
    record Down(Consumer<Double> handler) implements ActionHandler {
    }

    /**
     * The function which should be executed if Action key is currently hold down
     */
    record Pressed(Consumer<Double> handler) implements ActionHandler {
    }


}