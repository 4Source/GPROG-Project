package ZombieGame.Components;

import ZombieGame.Game;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;

public class WorldPositionComponent extends PositionComponent {
    protected WorldPos pos;

    /**
     * @param entity The entity to which the components belongs to
     * @param pos The position in the world
     */
    public WorldPositionComponent(Entity entity, WorldPos pos) {
        super(entity);
        this.pos = pos;
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public final WorldPos getWorldPos() {
        return this.pos;
    }

    @Override
    public final ViewPos getViewPos() {
        return this.pos.toViewPos(Game.world);
    }

    @Override
    public final void setWorldPos(WorldPos pos) {
        this.pos = pos;
    }

    @Override
    public final void setViewPos(ViewPos pos) {
        this.pos = pos.toWorldPos(Game.world);
    }
}
