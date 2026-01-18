package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;

import ZombieGame.CharacterEquipment;
import ZombieGame.Components.StaticSpriteComponent;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Game;
import ZombieGame.Sprites.StaticSprite;

public class AmmunitionCounter extends Counter {
    /**
     * @param pos The position in the world
     * @param ammunition The initial number of ammunition
     */
    public AmmunitionCounter(ViewPos pos, int ammunition) {
        super(pos, new Color(255, 255, 0, 210), new Font("Arial", Font.PLAIN, 30), ammunition);
    }

    @Override
    public String toString() {
        Avatar player = Game.world.getEntity(Avatar.class).orElse(null);
        if (player == null) return "";

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
