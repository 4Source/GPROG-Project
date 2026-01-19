package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import ZombieGame.Coordinates.ViewPos;

public class ZombieKillCounter extends Counter {

    private static Font BASE_FONT;

    private static Font getPixelFont(float size) {
        try {
            if (BASE_FONT == null) {
                BASE_FONT = Font.createFont(
                        Font.TRUETYPE_FONT,
                        new File("assets/fonts/PressStart2P-Regular.ttf")
                );
            }
            return BASE_FONT.deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Monospaced", Font.BOLD, (int) size);
        }
    }

    /**
     * @param pos The position in the world
     */
    public ZombieKillCounter(ViewPos pos) {
        super(
                pos,
                new Color(245, 245, 235, 230), // hell (gut lesbar)
                getPixelFont(26f),            // Press Start 2P
                0
        );
    }

    @Override
    public String toString() {
        String display = "KILLS: ";
        display += this.getNumber();
        return display;
    }
}
