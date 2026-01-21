package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;
import ZombieGame.Components.PlayerWeaponComponent;
import ZombieGame.Components.UIComponent;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Game;
import ZombieGame.Sprites.SpriteManager;
import ZombieGame.Systems.Graphic.GraphicLayer;
import ZombieGame.Systems.Graphic.GraphicSystem;

import java.awt.image.BufferedImage;

public class AmmunitionUI extends UIElement{
    private static final String GUN_AMMUNITION = "assets/PostApocalypse_AssetPack/UI/Bullet Indicators/Small/Gun-Bullet_Small.png";
    private static final String GUN_EMPTY = "assets/PostApocalypse_AssetPack/UI/Bullet Indicators/Small/Gun-Bullet_Small_Empty.png";
    private static final String PISTOL_AMMUNITION = "assets/PostApocalypse_AssetPack/UI/Bullet Indicators/Pistol-Bullet.png";
    private static final String PISTOL_EMPTY = "assets/PostApocalypse_AssetPack/UI/Bullet Indicators/Pistol-Bullet_Empty.png";
    private static final String SHOTGUN_AMMUNITION = "assets/PostApocalypse_AssetPack/UI/Bullet Indicators/Shotgun-Bullet.png";
    private static final String SHOTGUN_EMPTY = "assets/PostApocalypse_AssetPack/UI/Bullet Indicators/Shotgun-Bullet_Empty.png";
    private static final String BAT = "assets/PostApocalypse_AssetPack/Objects/Pickable/Bat.png";

    private static final int SCALE = 3;

    public AmmunitionUI(ViewPos pos) {
        super(pos, e -> new UIComponent(e, null) {

            private final BufferedImage gunFull = SpriteManager.getSprite(GUN_AMMUNITION);
            private final BufferedImage gunEmpty = SpriteManager.getSprite(GUN_EMPTY);
            private final BufferedImage pistolFull = SpriteManager.getSprite(PISTOL_AMMUNITION);
            private final BufferedImage pistolEmpty = SpriteManager.getSprite(PISTOL_EMPTY);
            private final BufferedImage shotgunFull = SpriteManager.getSprite(SHOTGUN_AMMUNITION);
            private final BufferedImage shotgunEmpty = SpriteManager.getSprite(SHOTGUN_EMPTY);
            private final BufferedImage bat = SpriteManager.getSprite(BAT);



            @Override
            public void update(double deltaTime) {
            }

            @Override
            public void draw() {
                Avatar player = Game.world.getEntity(Avatar.class).orElse(null);
                if (player == null) return;
                if(player.getVisualComponent().getCharacterEquipment() == CharacterEquipment.HANDS){return;}

                PlayerWeaponComponent playerWeaponComponent = player.getWeaponComponent();
                EquipmentStats stats = playerWeaponComponent.getEquipmentStats();
                CharacterEquipment currentWeapon = player.getVisualComponent().getCharacterEquipment();

                BufferedImage iconToDraw;

                // Wähle Icon abhängig von Waffe und Ammo
                boolean hasAmmo = stats.getAmmunition() > 0;

                switch (currentWeapon) {
                    case BAT -> iconToDraw = bat;
                    case GUN -> iconToDraw = hasAmmo ? gunFull : gunEmpty;
                    case PISTOL -> iconToDraw = hasAmmo ? pistolFull : pistolEmpty;
                    case SHOTGUN -> iconToDraw = hasAmmo ? shotgunFull : shotgunEmpty;
                    default -> {return;}
                }

                // Sprite zeichnen
                GraphicSystem.getInstance().drawSprite(
                        iconToDraw,
                        this.getEntity().getPositionComponent().getViewPos(),
                        0, 0,
                        SCALE,
                        iconToDraw.getWidth(),
                        iconToDraw.getHeight()
                );
            }

            @Override
            public GraphicLayer getLayer() {
                return GraphicLayer.UI;
            }

            @Override
            public int getDepth() {
                return 0;
            }
        });
    }
}