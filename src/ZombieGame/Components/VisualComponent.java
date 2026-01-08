package ZombieGame.Components;

import ZombieGame.Capabilities.Drawable;
import ZombieGame.Entities.Entity;

public abstract class VisualComponent extends Component implements Drawable {
    /**
     * A Component which allows the graphic system to visualize the entity.
     * 
     * @param entity The entity to which the components belongs to
     */
    public VisualComponent(Entity entity) {
        super(entity);
    }

    /**
     * Draw the component in the graphics system.
     */
    public abstract void draw();

    @Override
    public int getDepth() {
        return (int) this.getEntity().getPosY();
    }
}
