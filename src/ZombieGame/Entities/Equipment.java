package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;
import ZombieGame.Components.LifetimeComponent;
import ZombieGame.Components.StaticSpriteComponent;
import ZombieGame.Constants;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Game;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Physic.HitBox;

import java.awt.*;

public abstract class Equipment extends Item {
    private final CharacterEquipment equipment;
    EquipmentStats equipmentStats;


    public Equipment(WorldPos pos, CharacterEquipment equipment, EquipmentStats equipmentStats, HitBox hitBox) {
        super(pos, hitBox, Color.ORANGE, e -> {

            String path;

            switch (equipment) {
                case BAT -> {
                    path = "assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Bat.png";
                }
                case GUN -> {
                    path = "assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Gun.png";
                }
                case PISTOL -> {
                    path = "assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Pistol.png";
                }
                case SHOTGUN -> {
                    path = "assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Shotgun.png";
                }
                default -> throw new IllegalStateException();
            }

            return new StaticSpriteComponent(e,new StaticSprite(path, 1, 1, 3, 0, 0));
        });

        this.equipment = equipment;
        this.equipmentStats = equipmentStats != null ? equipmentStats : new EquipmentStats(equipment);

    }

    private void dropEquipment(Avatar avatar){
        if(avatar.getVisualComponent().getCharacterEquipment() == CharacterEquipment.HANDS) {return;}

        WorldPos dropPos = avatar
                .getPositionComponent()
                .getWorldPos();

        EquipmentStats stats = avatar.getWeaponComponent().getEquipmentStats();

        switch (avatar.getVisualComponent().getCharacterEquipment()) {
            case BAT -> {
                Game.world.spawnEntity(new Bat(dropPos,stats));
            }
            case GUN -> {
                Game.world.spawnEntity(new Gun(dropPos,stats));
            }
            case PISTOL -> {
                Game.world.spawnEntity(new Pistol(dropPos,stats));
            }
            case SHOTGUN -> {
                Game.world.spawnEntity(new Shotgun(dropPos,stats));
            }
            default -> throw new IllegalStateException();
        }
    }

    @Override
    public StaticSpriteComponent getVisualComponent() {
        return (StaticSpriteComponent) super.getVisualComponent();
    }

    @Override
    public void pickUp(Entity entity) {
        super.pickUp(entity);

        if (!(entity instanceof Avatar avatar)) {
            return;
        }

        dropEquipment(avatar);

        avatar.getVisualComponent().changeState(equipment);

        // new weapon
        avatar.getWeaponComponent().setEquipment(equipment,equipmentStats);

    }
}
