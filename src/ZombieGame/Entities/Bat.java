package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.RectangleHitBox;

public class Bat extends Equipment {

    public Bat(WorldPos pos, EquipmentStats equipmentStats) {
        super(pos, CharacterEquipment.BAT, equipmentStats, new RectangleHitBox(HitBoxType.Overlap, 21, 16));
    }

    public Bat(WorldPos pos) {
        this(pos, null);
    }
}