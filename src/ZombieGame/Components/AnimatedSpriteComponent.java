package ZombieGame.Components;

import ZombieGame.GraphicLayer;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.Sprite;

/**
 * Draws a single sprite (static or animated) and updates it each frame.
 */
public class AnimatedSpriteComponent extends SpriteComponent {
    private final Sprite sprite;

    public AnimatedSpriteComponent(Entity entity, Sprite sprite) {
        super(entity);
        this.sprite = sprite;
    }

    @Override
    public void draw() {
        double posX = this.getEntity().getPosX() - Entity.world.worldPartX;
        double posY = this.getEntity().getPosY() - Entity.world.worldPartY;
        this.sprite.draw(posX, posY);
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
