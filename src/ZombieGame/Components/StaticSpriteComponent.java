package ZombieGame.Components;

import java.util.ArrayList;

import ZombieGame.GraphicLayer;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.StaticSprite;

public class StaticSpriteComponent extends SpriteComponent {
    private ArrayList<StaticSprite> sprites;

    public StaticSpriteComponent(Entity entity) {
        super(entity);

        this.sprites = new ArrayList<>();
    }

    @Override
    public void draw() {
        double posX = Entity.world.worldToViewPosX(this.getEntity().getPosX());
        double posY = Entity.world.worldToViewPosY(this.getEntity().getPosY());

        this.sprites.forEach(s -> {
            s.draw(posX, posY);
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
