package ZombieGame.Components;

import java.util.Collection;

import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.Sprite;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Graphic.GraphicLayer;

/**
 * Draws a single sprite static
 */
public class StaticSpriteComponent extends SpriteComponent {
    private final StaticSprite sprite;

    public StaticSpriteComponent(Entity entity, StaticSprite sprite) {
        super(entity);
        this.sprite = sprite;
    }

    @Override
    public void draw() {
        ViewPos view = this.getEntity().getPositionComponent().getViewPos();
        this.sprite.draw(view);
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.GAME;
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    protected Collection<? extends Sprite> getSprites() {
        throw new UnsupportedOperationException("StaticSpriteComponent does not support getSprites() use getSprite(index) instead");
    }

    @Override
    public Sprite getSprite(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException("StaticSpriteComponent only allows index 0");
        }

        return sprite;
    }
}
