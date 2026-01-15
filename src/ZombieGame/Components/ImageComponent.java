package ZombieGame.Components;

import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Graphic.GraphicLayer;

public class ImageComponent extends VisualComponent {
    private final StaticSprite image;
    private GraphicLayer layer;

    public ImageComponent(Entity entity, StaticSprite image, GraphicLayer layer) {
        super(entity);

        this.layer = layer;
        this.image = image;
    }

    @Override
    public GraphicLayer getLayer() {
        return this.layer;
    }

    @Override
    public void draw() {
        image.draw(this.getEntity().getPositionComponent().getViewPos());
    }

    @Override
    public void update(double deltaTime) {
    }
}
