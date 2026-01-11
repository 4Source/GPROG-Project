package ZombieGame.Components;

import ZombieGame.Systems.Graphic.GraphicLayer;

import java.util.Collections;
import java.util.Set;

import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.AnimatedSprite;

/**
 * Draws a single sprite animated and updates it each frame.
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

    @Override
    public Set<AnimatedSprite> getSprite() {
        return Collections.singleton(this.sprite);
    }
}
