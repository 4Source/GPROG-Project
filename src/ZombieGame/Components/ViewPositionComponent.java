package ZombieGame.Components;

import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;

public class ViewPositionComponent extends PositionComponent {
    protected ViewPos pos;

    /**
     * @param entity The entity to which the components belongs to
     * @param pos The position on the screen
     */
    public ViewPositionComponent(Entity entity, ViewPos pos) {
        super(entity);
        this.pos = pos;
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public final WorldPos getWorldPos() {
        return this.pos.toWorldPos(Entity.world);
    }

    @Override
    public final ViewPos getViewPos() {
        return this.pos;
    }

    @Override
    public final void setWorldPos(WorldPos pos) {
        this.pos = pos.toViewPos(Entity.world);
    }

    @Override
    public final void setViewPos(ViewPos pos) {
        this.pos = pos;
    }
}
