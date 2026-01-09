package ZombieGame.Entities;

import java.awt.Color;

import ZombieGame.DrawStyle;
import ZombieGame.GraphicLayer;
import ZombieGame.GraphicSystem;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Components.UIComponent;

/**
 * Simple UI health bar for the {@link Avatar}.
 * Draws in screen coordinates.
 */
public class HealthBar extends UIElement {
	/**
	 * @param posX   Center x position on screen
	 * @param posY   Center y position on screen
	 * @param width  Bar width in pixels
	 * @param height Bar height in pixels
	 */
	public HealthBar(double posX, double posY, int width, int height) {
		super(posX, posY, e -> new UIComponent(e, new Color(0, 0, 0, 0)) {
			@Override
			public void update(double deltaTime) {
				// UI only
			}

			@Override
			public void draw() {
				Avatar avatar = Entity.world.getEntity(Avatar.class).orElse(null);
				if (avatar == null) {
					return;
				}

				LifeComponent life = avatar.getLifeComponent();
				double ratio = life.getLifeRatio();

				int cx = (int) this.getEntity().getPosX();
				int cy = (int) this.getEntity().getPosY();

				// Background
				GraphicSystem.getInstance().drawFillRect(cx, cy, width, height, new DrawStyle().color(new Color(20, 20, 20, 200)));

				// Fill (keep centered, but reduce width)
				int fillW = (int) Math.round(width * ratio);
				if (fillW > 0) {
					// Shift so the fill starts at the left edge while still using center-based drawing.
					int left = cx - width / 2;
					int fillCenterX = left + fillW / 2;
					GraphicSystem.getInstance().drawFillRect(fillCenterX, cy, fillW, height, new DrawStyle().color(new Color(200, 40, 40, 230)));
				}

				// Outline
				GraphicSystem.getInstance().drawRect(cx, cy, width, height, new DrawStyle().color(new Color(255, 255, 255, 220)));
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
