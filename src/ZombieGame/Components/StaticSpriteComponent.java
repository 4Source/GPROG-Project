package ZombieGame.Components;

import java.util.ArrayList;

import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Graphic.GraphicLayer;

public class StaticSpriteComponent extends SpriteComponent {
    private ArrayList<StaticSprite> sprites;

    public StaticSpriteComponent(Entity entity) {
        super(entity);

        this.sprites = new ArrayList<>();
    }

    @Override
    public void draw() {
        ViewPos view = this.getEntity().getPositionComponent().getViewPos();

        this.sprites.forEach(s -> {
            s.draw(view);
        });
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.GAME;
    }

    @Override
    public void update(double deltaTime) {
    }

    public void addSprite(StaticSprite sprite) {
        this.sprites.add(sprite);
    }
}
