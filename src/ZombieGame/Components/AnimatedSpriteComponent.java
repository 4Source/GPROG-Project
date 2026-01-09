package ZombieGame.Components;

import ZombieGame.GraphicLayer;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.AnimatedSprite;

/**
 * Draws a single sprite (static or animated) and updates it each frame.
 */
public class AnimatedSpriteComponent extends SpriteComponent {
    private final AnimatedSprite sprite;

    public AnimatedSpriteComponent(Entity entity, AnimatedSprite sprite) {
        super(entity);
        this.sprite = sprite;
    }

    @Override
    public void draw() {
        ViewPos view = this.getEntity().getPositionComponent().getViewPos();
        this.sprite.draw(view);
    }

    @Override
    public void update(double deltaTime) {
        this.sprite.update(deltaTime);
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.GAME;
    }
}
