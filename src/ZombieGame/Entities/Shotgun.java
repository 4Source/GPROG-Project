package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.RectangleHitBox;

public class Shotgun extends Equipment {

    public Shotgun(WorldPos pos, EquipmentStats equipmentStats) {
        super(pos, CharacterEquipment.SHOTGUN, equipmentStats, new RectangleHitBox(HitBoxType.Overlap, 21, 16));
    }

    public Shotgun(WorldPos pos) {
        this(pos, null);
    }
}