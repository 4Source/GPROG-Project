package ZombieGame.Components;

import java.util.Collections;
import java.util.Set;

import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
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
    public Set<StaticSprite> getSprite() {
        return Collections.singleton(this.sprite);
    }
}
