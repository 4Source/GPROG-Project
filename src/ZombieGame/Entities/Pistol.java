package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.HitBoxType;

public class Pistol extends Equipment {

    public Pistol(WorldPos pos, EquipmentStats equipmentStats) {
        super(pos, CharacterEquipment.PISTOL, equipmentStats, new CircleHitBox(HitBoxType.Overlap, 16));
    }

    public Pistol(WorldPos pos) {
        this(pos, null);
    }
}