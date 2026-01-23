package ZombieGame.Components;

import java.awt.BasicStroke;
import java.awt.Color;

import ZombieGame.Capabilities.DebuggableGeometry;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicSystem;

public class TargetMovementComponent extends MovementComponent implements DebuggableGeometry {
    protected WorldPos dest;
    protected boolean hasDestination;

    /**
     * A movement component which will move to a given destination.
     * 
     * @param entity The entity to which the components belongs to
     * @param pos The position in the world
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    public TargetMovementComponent(Entity entity, WorldPos pos, double alpha, double speed) {
        super(entity, pos, alpha, speed);
        this.hasDestination = false;
    }

    /**
     * @param entity The entity to which the components belongs to
     * @param pos The position in the world
     * @param speed The speed how fast to move
     * @param destination The world position where to move
     */
    public TargetMovementComponent(Entity entity, WorldPos pos, double speed, WorldPos destination) {
        super(entity, pos, Math.atan2(destination.y() - entity.getPositionComponent().getWorldPos().y(), destination.x() - entity.getPositionComponent().getWorldPos().x()), speed);
        this.hasDestination = true;
    }

    @Override
    public void update(double deltaTime) {
        // Default: no movement this frame (important for collision logic).
        this.lastStepDelta = new WorldPos(0, 0);
        this.movedThisFrame = false;

        // move if object has a destination
        if (this.hasDestination) {
            // stop if destination is reached
            WorldPos diff = this.getEntity().getPositionComponent().getWorldPos().sub(this.dest).abs();
            if (diff.x() < 3 && diff.y() < 3) {
                this.hasDestination = false;
                return;
            }

            super.update(deltaTime);
        }
    }

    /**
     * Set a point in the world as destination
     * 
     * @param destination The world position where to move
     */
    public void setDestination(WorldPos destination) {
        this.hasDestination = true;
        this.dest = destination;

        this.alpha = Math.atan2(destination.y() - this.getEntity().getPositionComponent().getWorldPos().y(), destination.x() - this.getEntity().getPositionComponent().getWorldPos().x());
    }

    /**
     * Set the location of an object as destination
     * 
     * @param destination The object where to move to
     */
    public void setDestination(Entity destination) {
        setDestination(destination.getPositionComponent().getWorldPos());
    }

    @Override
    public DebugCategoryMask getCategoryMask() {
        return new DebugCategoryMask(DebugCategory.AI);
    }

    @Override
    public void drawDebug() {
        DrawStyle style = new DrawStyle().color(new Color(153, 0, 0)).stroke(new BasicStroke(2.0f));
        ViewPos view = this.getEntity().getPositionComponent().getViewPos();
        GraphicSystem.getInstance().drawLine(view, alpha, (int) (speed / 2), style);
    }
}
