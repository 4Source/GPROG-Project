package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.RectangleHitBox;

public class Pistol extends Equipment {

    public Pistol(WorldPos pos, EquipmentStats equipmentStats) {
        super(pos, CharacterEquipment.PISTOL, equipmentStats, new RectangleHitBox(HitBoxType.Overlap, 21, 16));
    }

    public Pistol(WorldPos pos) {
        this(pos, null);
    }
}