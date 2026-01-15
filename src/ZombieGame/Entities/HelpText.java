package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;

public class HelpText extends TextElement {

    /**
     * @param pos The position in the world
     */
    public HelpText(ViewPos pos) {
        super(pos, new Color(255, 255, 255, 200), new Font("Arial", Font.PLAIN, 24));
    }

    @Override
    public String toString() {
        String res = "";
        for (Action a : Action.values()) {
            String name = a.toString();
            List<String> keys = InputSystem.getInstance().getKeyMapping(a);
            if (name != null && keys.size() > 0) {
                res += String.format("%s: %s | ", name, keys);
            }
        }
        return res;
    }
}
