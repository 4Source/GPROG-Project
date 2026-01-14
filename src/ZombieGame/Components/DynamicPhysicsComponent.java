package ZombieGame.Components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.function.Consumer;

import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.HitBox;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Systems.Physic.PhysicsSystem;
import ZombieGame.Systems.Physic.RectangleHitBox;

public class DynamicPhysicsComponent extends PhysicsComponent {
    public final Consumer<Collision> onEnter;
    public final Consumer<Collision> onStay;
    public final Consumer<Collision> onExit;

    /**
     * A Physics component which could be added to {@link Entity entities} to make it interact with the {@link PhysicsSystem Physics system}.
     * This should be only used for entities which can {@link MovementComponent move}. For Static entities use {@link StaticPhysicsComponent}
     * 
     * @param entity The entity to which the components belongs to
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     * @param layer The layer on which the PhysicsComponent should belong
     * @param mask The layers which the PhysicsComponent could interact with
     * @param onEnter The function to execute if a collision with another component starts
     * @param onStay The function to execute while a collision with another component continues
     * @param onExit The function to execute if a collision with another component ends
     */
    public DynamicPhysicsComponent(Entity entity, HitBox hitBox, PhysicsCollisionLayer layer, PhysicsCollisionMask mask, Consumer<Collision> onEnter, Consumer<Collision> onStay, Consumer<Collision> onExit) {
        super(entity, hitBox, layer, mask);
        this.onEnter = onEnter;
        this.onStay = onStay;
        this.onExit = onExit;
    }

    /**
     * @param entity The entity to which the components belongs to
     * @param hitBox The hit box against which the {@link PhysicsSystem physics system} checks for collisions
     * @param layer The layer on which the PhysicsComponent should belong
     * @param mask The layers which the PhysicsComponent could interact with
     */
    public DynamicPhysicsComponent(Entity entity, HitBox hitBox, PhysicsCollisionLayer layer, PhysicsCollisionMask mask) {
        super(entity, hitBox, layer, mask);
        this.onEnter = collision -> {};
        this.onStay = collision -> {};
        this.onExit = collision -> {};
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void drawDebug() {
        DrawStyle style = new DrawStyle();
        if (PhysicsSystem.getInstance().hasCollision(this)) {
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
            ViewPos view = this.getEntity().getPositionComponent().getViewPos().add(this.hitBox.getOffset());
            int d = (int) (radius * 2);

            GraphicSystem.getInstance().drawOval(view, d, d, style);
        } else if (this.hitBox instanceof RectangleHitBox) {
            int width = ((RectangleHitBox) this.hitBox).getWidth();
            int height = ((RectangleHitBox) this.hitBox).getHeight();
            ViewPos view = this.getEntity().getPositionComponent().getViewPos().add(this.hitBox.getOffset());

            GraphicSystem.getInstance().drawRect(view, width, height, style);
        } else {
            System.err.println("Unsupported hit box for debug visualization.");
        }
    }
}
