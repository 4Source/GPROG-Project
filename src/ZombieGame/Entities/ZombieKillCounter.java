package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;

import ZombieGame.Coordinates.ViewPos;

public class ZombieKillCounter extends Counter {

    private static Font BASE_FONT;

    private static Font getPixelFont(float size) {
        try {
            if (BASE_FONT == null) {
                InputStream is = ZombieKillCounter.class.getClassLoader().getResourceAsStream("assets/fonts/PressStart2P-Regular.ttf");

                if (is == null) {
                    throw new RuntimeException("Font not found");
                }

                BASE_FONT = Font.createFont(Font.TRUETYPE_FONT, is);
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
                getPixelFont(26f), // Press Start 2P
                0);
    }

    @Override
    public String toString() {
        String display = "KILLS: ";
        display += this.getNumber();
        return display;
    }
}
