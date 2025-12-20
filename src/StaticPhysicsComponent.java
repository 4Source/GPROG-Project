import java.awt.BasicStroke;
import java.awt.Color;

public class StaticPhysicsComponent extends PhysicsComponent {

    /**
     * A Physics component which could be added to {@link Entity entities} to make it interact with the {@link PhysicsSystem Physics system}.
     * This should be only used for entities which can NOT {@link MovementComponent move}. For Dynamic entities use {@link DynamicPhysicsComponent}
     * 
     * @param entity The entity to which the components belongs to
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     */
    protected StaticPhysicsComponent(Entity entity, HitBox hitBox) {
        super(entity, hitBox);
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void draw() {
        if (PhysicsSystem.enableDebug) {
            DrawStyle style = new DrawStyle();
            if (PhysicsSystem.getInstance().hasCollision(this.entity)) {
                // #00ffff
                style.color(new Color(0, 255, 255));
            } else {
                // #009999
                style.color(new Color(0, 153, 153));
            }

            if (this.hitBox.collisionType == HitBoxType.Overlap) {
                float[] dashPattern = { 6.0f, 3.0f };
                style.stroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
            } else {
                style.stroke(new BasicStroke(2.0f));
            }

            if (this.hitBox instanceof CircleHitBox) {
                int radius = ((CircleHitBox) this.hitBox).getRadius();
                int x = (int) (entity.posX - radius - Entity.world.worldPartX);
                int y = (int) (entity.posY - radius - Entity.world.worldPartY);
                int d = (int) (radius * 2);

                GraphicSystem.getInstance().drawOval(x, y, d, d, style);
            } else if (this.hitBox instanceof RectangleHitBox) {
                int width = ((RectangleHitBox) this.hitBox).getWidth();
                int height = ((RectangleHitBox) this.hitBox).getHeight();
                int x = (int) (entity.posX - (width / 2) - Entity.world.worldPartX);
                int y = (int) (entity.posY - (height / 2) - Entity.world.worldPartY);

                GraphicSystem.getInstance().drawRect(x, y, width, height, style);
            } else {
                System.err.println("Unsupported hit box for debug visualization.");
            }
        }
    }
}
