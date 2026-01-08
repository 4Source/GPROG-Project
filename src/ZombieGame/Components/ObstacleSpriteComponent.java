package ZombieGame.Components;

import java.util.ArrayList;

import ZombieGame.GraphicLayer;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.StaticSprite;

public class ObstacleSpriteComponent extends SpriteComponent {
    private ArrayList<StaticSprite> sprites;

    public ObstacleSpriteComponent(Entity entity) {
        super(entity);

        this.sprites = new ArrayList<>();
    }

    @Override
    public void draw() {
        double posX = this.getEntity().getPosX() - Entity.world.worldPartX;
        double posY = this.getEntity().getPosY() - Entity.world.worldPartY;

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
