package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.RectangleHitBox;

public class Gun extends Equipment {

    public Gun(WorldPos pos, EquipmentStats equipmentStats) {
        super(pos, CharacterEquipment.GUN, equipmentStats, new RectangleHitBox(HitBoxType.Overlap, 21, 16));
    }

    public Gun(WorldPos pos) {
        this(pos, null);
    }
}
