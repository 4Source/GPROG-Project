package ZombieGame.Components;

import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Entity;

public abstract class PositionComponent extends Component {

    public PositionComponent(Entity entity) {
        super(entity);
    }

    public abstract WorldPos getWorldPos();

    public abstract ViewPos getViewPos();

    public abstract void setWorldPos(WorldPos pos);

    public abstract void setViewPos(ViewPos pos);
}
