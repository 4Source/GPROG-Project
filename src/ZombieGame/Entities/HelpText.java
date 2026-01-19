package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.List;

import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;

/**
 * Help text shown in the pause menu.
 *
 * Rendering (panel, header centering, left-aligned body) is handled in TextComponent
 * for HelpText instances.
 */
public class HelpText extends TextElement {

    private static Font HELP_FONT;

    private static Font getHelpFont(float size) {
        try {
            if (HELP_FONT == null) {
                HELP_FONT = Font.createFont(
                        Font.TRUETYPE_FONT,
                        new File("assets/fonts/PixelOperator-Bold.ttf")
                );
            }
            return HELP_FONT.deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Monospaced", Font.PLAIN, (int) size);
        }
    }

    /**
     * @param pos Center position of the help panel.
     */
    public HelpText(ViewPos pos) {
        super(pos, new Color(245, 245, 235, 230), getHelpFont(28f));
    }

    @Override
    public String toString() {
        return
                "HOW TO PLAY\n\n" +
                        "Move Player:  WASD\n" +
                        "Shoot:  Left Click\n" +
                        "Pause Game:  Esc\n";
    }

}
