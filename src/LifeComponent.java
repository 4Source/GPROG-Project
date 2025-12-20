import java.awt.Color;
import java.awt.Font;

public class LifeComponent extends LivingComponent implements Drawable {
    protected double life;
    private double damageTextTimeout;
    private String damageText;
    private static final double INITIAL_TIMEOUT = 0.15;

    /**
     * A component which provides life to the entity. Allows to take damage, heal, or die if life falls below 0.
     * 
     * @param entity The entity to which the components belongs to
     * @param life The initial life of the component
     */
    protected LifeComponent(Entity entity, double life) {
        super(entity);
        this.life = life;
        this.damageTextTimeout = 0;
    }

    /**
     * Reduce the life by damage
     */
    public void takeDamage(double damage) {
        // every shot decreases life
        this.life -= damage;
        this.damageTextTimeout = LifeComponent.INITIAL_TIMEOUT;
        this.damageText = Double.toString(damage);

        if (this.life <= 0) {
            this.kill();
        }
    }

    /**
     * Increases the life by health
     */
    public void restoreHealth(double health) {
        this.life += health;
    }

    @Override
    public void update(double deltaTime) {
        if (this.damageTextTimeout > 0) {
            this.damageTextTimeout -= deltaTime;
        }
    }

    @Override
    public void draw() {
        if (this.damageTextTimeout > 0) {
            GraphicSystem.getInstance().drawString(damageText, (int) (this.entity.posX - Entity.world.worldPartX), (int) (this.entity.posY - Entity.world.worldPartY), new DrawStyle().color(Color.RED).font(new Font("Arial", Font.PLAIN, 16)));
        }
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.EFFECTS;
    }
}
