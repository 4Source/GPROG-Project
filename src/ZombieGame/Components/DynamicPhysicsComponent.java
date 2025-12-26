package ZombieGame.Components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.function.Consumer;

import ZombieGame.CircleHitBox;
import ZombieGame.Collision;
import ZombieGame.DrawStyle;
import ZombieGame.GraphicSystem;
import ZombieGame.HitBox;
import ZombieGame.HitBoxType;
import ZombieGame.PhysicsSystem;
import ZombieGame.RectangleHitBox;
import ZombieGame.Entities.Entity;

public class DynamicPhysicsComponent extends PhysicsComponent {
    public final Consumer<Collision> onEnter;
    public final Consumer<Collision> onExit;

    /**
     * A Physics component which could be added to {@link Entity entities} to make it interact with the {@link PhysicsSystem Physics system}.
     * This should be only used for entities which can {@link MovementComponent move}. For Static entities use {@link StaticPhysicsComponent}
     * 
     * @param entity The entity to which the components belongs to
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     * @param onEnter The function to execute if a collision with another component starts
     * @param onExit The function to execute if a collision with another component ends
     */
    public DynamicPhysicsComponent(Entity entity, HitBox hitBox, Consumer<Collision> onEnter, Consumer<Collision> onExit) {
        super(entity, hitBox);
        this.onEnter = onEnter;
        this.onExit = onExit;
    }

    /**
     * @param entity The entity to which the components belongs to
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     */
    public DynamicPhysicsComponent(Entity entity, HitBox hitBox) {
        super(entity, hitBox);
        this.onEnter = collision -> {};
        this.onExit = collision -> {};
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void draw() {
        if (PhysicsSystem.enableDebug) {
            DrawStyle style = new DrawStyle();
            if (PhysicsSystem.getInstance().hasCollision(this.getEntity())) {
                // #00ff00
                style.color(new Color(0, 255, 0));
            } else {
                // #009900
                style.color(new Color(0, 153, 0));
            }

            if (this.hitBox.getCollisionType() == HitBoxType.Overlap) {
                float[] dashPattern = { 6.0f, 3.0f };
                style.stroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
            } else {
                style.stroke(new BasicStroke(2.0f));
            }

            if (this.hitBox instanceof CircleHitBox) {
                int radius = ((CircleHitBox) this.hitBox).getRadius();
                int x = (int) (this.getEntity().getPosX() + this.hitBox.getOffsetX() - Entity.world.worldPartX);
                int y = (int) (this.getEntity().getPosY() + this.hitBox.getOffsetY() - Entity.world.worldPartY);
                int d = (int) (radius * 2);

                GraphicSystem.getInstance().drawOval(x, y, d, d, style);
            } else if (this.hitBox instanceof RectangleHitBox) {
                int width = ((RectangleHitBox) this.hitBox).getWidth();
                int height = ((RectangleHitBox) this.hitBox).getHeight();
                int x = (int) (this.getEntity().getPosX() + this.hitBox.getOffsetX() - Entity.world.worldPartX);
                int y = (int) (this.getEntity().getPosY() + this.hitBox.getOffsetY() - Entity.world.worldPartY);

                GraphicSystem.getInstance().drawRect(x, y, width, height, style);
            } else {
                System.err.println("Unsupported hit box for debug visualization.");
            }
        }
    }
}
