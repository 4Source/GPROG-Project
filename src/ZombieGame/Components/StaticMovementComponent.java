package ZombieGame.Components;

import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;

public class StaticMovementComponent extends MovementComponent {
    /**
     * A Movement component which allows only static movement with the initial direction and speed
     * 
     * @param entity The entity to which the components belongs to
     * @param pos The position in the world
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     */
    public StaticMovementComponent(Entity entity, WorldPos pos, double alpha, double speed) {
        super(entity, pos, alpha, speed);
    }
}
