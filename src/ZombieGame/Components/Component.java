package ZombieGame.Components;

import java.util.UUID;

import ZombieGame.Entities.Entity;

public abstract class Component {
    private final UUID uuid = UUID.randomUUID();
    private final Entity entity;

    /**
     * @param entity The entity to which the components belongs to
     */
    public Component(Entity entity) {
        this.entity = entity;
    }

    /**
     * Update the component using delta time to get constant change with varying fps
     * 
     * @param deltaTime The time since last frame in seconds
     */
    public abstract void update(double deltaTime);

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Component component = (Component) object;
        return this.uuid.equals(component.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
