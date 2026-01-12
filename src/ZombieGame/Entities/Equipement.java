package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;
import ZombieGame.Components.LifetimeComponent;
import ZombieGame.Components.StaticSpriteComponent;
import ZombieGame.Constants;
import ZombieGame.HitBoxType;
import ZombieGame.RectangleHitBox;
import ZombieGame.Sprites.StaticSprite;

import java.awt.*;
import java.util.Random;

public class Equipement extends Item {
    private LifetimeComponent lifetimeComponent;
    private final CharacterEquipment equipment;

    /**
     * @param posX The initial position in x of the grenade
     * @param posY The initial position in y of the grenade
     */
    public Equipement(double posX, double posY) {
        super(posX, posY, new RectangleHitBox(HitBoxType.Overlap, 21, 16), Color.ORANGE, e -> new StaticSpriteComponent(e));
        this.lifetimeComponent = this.add(new LifetimeComponent(this, Constants.LIFE_GRENADE));

        Random r = new Random();
        int random = r.nextInt(4);

        switch (random) {
            case 0 -> {
                equipment = CharacterEquipment.BAT;
                this.getVisualComponent().addSprite(
                        new StaticSprite("assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Bat.png", 1, 1, 3, 0, 0)
                );
            }
            case 1 -> {
                equipment = CharacterEquipment.GUN;
                this.getVisualComponent().addSprite(
                        new StaticSprite("assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Gun.png", 1, 1, 3, 0, 0)
                );
            }
            case 2 -> {
                equipment = CharacterEquipment.PISTOL;
                this.getVisualComponent().addSprite(
                        new StaticSprite("assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Pistol.png", 1, 1, 3, 0, 0)
                );
            }
            case 3 -> {
                equipment = CharacterEquipment.SHOTGUN;
                this.getVisualComponent().addSprite(
                        new StaticSprite("assets\\PostApocalypse_AssetPack\\Objects\\Pickable\\Shotgun.png", 1, 1, 3, 0, 0)
                );
            }
            default -> throw new IllegalStateException();
        }
    }

    public LifetimeComponent getLifetimeComponent() {
        return this.lifetimeComponent;
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

        avatar.getVisualComponent().changeState(equipment);

        // new weapon
        avatar.getWeaponComponent().setWeapon(equipment);
    }
}
