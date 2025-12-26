
import java.util.ArrayList;

public class ObstacleSpriteComponent extends SpriteComponent {
    private ArrayList<StaticSprite> sprites;

    protected ObstacleSpriteComponent(Entity entity) {
        super(entity);

        this.sprites = new ArrayList<>();
    }

    @Override
    public void draw() {
        double posX = this.getEntity().posX - Entity.world.worldPartX;
        double posY = this.getEntity().posY - Entity.world.worldPartY;

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
