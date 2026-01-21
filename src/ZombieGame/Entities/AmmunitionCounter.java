package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;

import ZombieGame.CharacterEquipment;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Game;

public class AmmunitionCounter extends Counter {
    /**
     * @param pos The position in the world
     * @param ammunition The initial number of ammunition
     */

    public AmmunitionCounter(ViewPos pos, int ammunition) {
        super(pos, new Color(245, 245, 235, 230), getPixelFont(26f), ammunition);
    }

    private static Font BASE_FONT;

    private static Font getPixelFont(float size) {
        try {
            if (BASE_FONT == null) {
                InputStream is = AmmunitionCounter.class.getClassLoader().getResourceAsStream("assets/fonts/PressStart2P-Regular.ttf");

                if (is == null) {
                    throw new RuntimeException("Font not found");
                }

                BASE_FONT = Font.createFont(Font.TRUETYPE_FONT, is);
            }
            return BASE_FONT.deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Monospaced", Font.BOLD, (int) size); // Fallback
        }
    }

    @Override
    public String toString() {
        Avatar player = Game.world.getEntity(Avatar.class).orElse(null);
        if (player == null)
            return "";

        CharacterEquipment equipment = player.getVisualComponent().getCharacterEquipment();

        switch (equipment) {
            case BAT -> {
                return "∞";
            }
            case HANDS -> {
                return "";
            }
            default -> {
                return String.valueOf(this.getNumber());
            }
        }
    }
}
