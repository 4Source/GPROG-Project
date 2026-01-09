package ZombieGame.Components;

import java.awt.BasicStroke;
import java.awt.Color;

import ZombieGame.CircleHitBox;
import ZombieGame.DrawStyle;
import ZombieGame.GraphicSystem;
import ZombieGame.HitBox;
import ZombieGame.HitBoxType;
import ZombieGame.PhysicsCollisionLayer;
import ZombieGame.PhysicsCollisionMask;
import ZombieGame.PhysicsSystem;
import ZombieGame.RectangleHitBox;
import ZombieGame.Entities.Entity;

public class StaticPhysicsComponent extends PhysicsComponent {

    /**
     * A Physics component which could be added to {@link Entity entities} to make it interact with the {@link PhysicsSystem Physics system}.
     * This should be only used for entities which can NOT {@link MovementComponent move}. For Dynamic entities use {@link DynamicPhysicsComponent}
     * 
     * @param entity The entity to which the components belongs to
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     * @param layer The layer on which the PhysicsComponent should belong
     * @param mask The layers which the PhysicsComponent could interact with
     */
    public StaticPhysicsComponent(Entity entity, HitBox hitBox, PhysicsCollisionLayer layer, PhysicsCollisionMask mask) {
        super(entity, hitBox, layer, mask);
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void draw() {
        if (PhysicsSystem.enableDebug) {
            DrawStyle style = new DrawStyle();
            if (PhysicsSystem.getInstance().hasCollision(this)) {
                // #00ffff
                style.color(new Color(0, 255, 255));
            } else {
                // #009999
                style.color(new Color(0, 153, 153));
            }

            if (this.hitBox.getCollisionType() == HitBoxType.Overlap) {
                float[] dashPattern = { 6.0f, 3.0f };
                style.stroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
            } else {
                style.stroke(new BasicStroke(2.0f));
            }

            if (this.hitBox instanceof CircleHitBox) {
                int radius = ((CircleHitBox) this.hitBox).getRadius();
                int x = (int) Entity.world.worldToViewPosX(this.getEntity().getPosX() + this.hitBox.getOffsetX());
                int y = (int) Entity.world.worldToViewPosY(this.getEntity().getPosY() + this.hitBox.getOffsetY());
                int d = (int) (radius * 2);

                GraphicSystem.getInstance().drawOval(x, y, d, d, style);
            } else if (this.hitBox instanceof RectangleHitBox) {
                int width = ((RectangleHitBox) this.hitBox).getWidth();
                int height = ((RectangleHitBox) this.hitBox).getHeight();
                int x = (int) Entity.world.worldToViewPosX(this.getEntity().getPosX() + this.hitBox.getOffsetX());
                int y = (int) Entity.world.worldToViewPosY(this.getEntity().getPosY() + this.hitBox.getOffsetY());

                GraphicSystem.getInstance().drawRect(x, y, width, height, style);
            } else {
                System.err.println("Unsupported hit box for debug visualization.");
            }
        }
    }
}
