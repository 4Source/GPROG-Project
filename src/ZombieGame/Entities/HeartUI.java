package ZombieGame.Entities;

import java.awt.image.BufferedImage;

import ZombieGame.Game;
import ZombieGame.Systems.Graphic.GraphicLayer;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Components.UIComponent;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Sprites.SpriteManager;

/**
 * Heart-based health UI for the {@link Avatar}.
 * Uses full/half/empty heart icons from the asset pack.
 * Life is interpreted as half-hearts (10 = 5 full hearts).
 */
public class HeartUI extends UIElement {

    private static final String HEART_FULL = "assets/PostApocalypse_AssetPack/UI/HP/Heart_Full.png";
    private static final String HEART_HALF = "assets/PostApocalypse_AssetPack/UI/HP/Heart_Half.png";
    private static final String HEART_EMPTY = "assets/PostApocalypse_AssetPack/UI/HP/Heart_Empty.png";

    private static final int HEART_SIZE = 28; // draw size in pixels
    private static final int HEART_GAP = 6; // spacing in pixels

    public HeartUI(ViewPos pos) {
        super(pos, e -> new UIComponent(e, null) {

            private final BufferedImage full = SpriteManager.getSprite(HEART_FULL);
            private final BufferedImage half = SpriteManager.getSprite(HEART_HALF);
            private final BufferedImage empty = SpriteManager.getSprite(HEART_EMPTY);

            @Override
            public void update(double deltaTime) {
                // UI element; nothing to update
            }

            @Override
            public void draw() {
                Avatar player = Game.world.getEntity(Avatar.class).orElse(null);
                if (player == null) {
                    System.err.println("Could not find Avatar");
                    return;
                }

                LifeComponent life = player.getLifeComponent();
                if (life == null) {
                    System.err.println("Could not find LifeComponent in Avatar");
                    return;
                }

                int halfHearts = life.getLife(); // current half-hearts
                int maxHalfHearts = life.getMaxLife(); // max half-hearts
                int hearts = (int) Math.ceil(maxHalfHearts / 2.0);

                ViewPos base = this.getEntity().getPositionComponent().getViewPos();

                for (int i = 0; i < hearts; i++) {
                    int remaining = halfHearts - (i * 2);

                    BufferedImage img;
                    if (remaining >= 2)
                        img = full;
                    else if (remaining == 1)
                        img = half;
                    else
                        img = empty;

                    GraphicSystem.getInstance().drawSprite(
                            img,
                            base.add(i * (HEART_SIZE + HEART_GAP), 0),
                            HEART_SIZE, HEART_SIZE,
                            new ViewPos(),
                            img.getWidth(), img.getHeight());
                }
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
