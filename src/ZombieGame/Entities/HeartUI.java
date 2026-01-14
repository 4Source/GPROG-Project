package ZombieGame.Entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import ZombieGame.Game;
import ZombieGame.Systems.Graphic.DrawStyle;
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

    private static final int SCALE = 3; // draw size in pixels
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

                int halfHearts = life.getHalfHearts(); // current half-hearts
                int maxHalfHearts = life.getMaxHalfHearts(); // max half-hearts
                int hearts = (int) Math.ceil(maxHalfHearts / 2.0);
                int heartSize = full.getWidth() * SCALE;

                ViewPos base = this.getEntity().getPositionComponent().getViewPos();

                // If more than 40 half hearts switch to one heart + number of half hearts
                if (maxHalfHearts > 40) {
                    GraphicSystem.getInstance().drawSprite(
                            full,
                            base.add((heartSize / 2), (heartSize / 2)),
                            0, 0,
                            SCALE,
                            full.getWidth(), full.getHeight());
                    GraphicSystem.getInstance().drawString(Integer.toString(halfHearts), base.add(heartSize + HEART_GAP, (3 * heartSize / 4)), new DrawStyle().color(new Color(145, 40, 61)).font(new Font("Arial", Font.PLAIN, 30)));
                    return;
                }

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
                            base.add(i * (heartSize + HEART_GAP) + (heartSize / 2), (heartSize / 2)),
                            0, 0,
                            SCALE,
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
