package ZombieGame.Components;

import ZombieGame.Entities.Entity;

public abstract class LivingComponent extends Component {
    private boolean isLiving;

    /**
     * A Component which allows a entity to die.
     * Dead entity automatically will be removed.
     * 
     * @param entity The entity to which the components belongs to
     */
    public LivingComponent(Entity entity) {
        super(entity);
        this.isLiving = true;
    }

    public boolean isLiving() {
        return this.isLiving;
    }

    public void kill() {
        this.isLiving = false;
    }
}
