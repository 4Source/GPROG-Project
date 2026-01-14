package ZombieGame.Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

import ZombieGame.EntityType;
import ZombieGame.Capabilities.Capability;
import ZombieGame.Components.Component;
import ZombieGame.Components.PositionComponent;

public abstract class Entity {
    private final UUID uuid = UUID.randomUUID();
    private HashMap<Class<? extends Component>, ArrayList<Component>> components = new HashMap<>();
    private final PositionComponent positionComponent;

    /**
     * @param <U> The type of the component extending a PositionComponent to create with factory method
     * @param posFactory A Factory method to create the component
     */
    public <T extends PositionComponent> Entity(Function<Entity, T> posFactory) {
        this.positionComponent = this.add(posFactory.apply(this));
    }

    public PositionComponent getPositionComponent() {
        return this.positionComponent;
    }

    /**
     * Add a component to the entity
     * 
     * @param <T> The type of Component which should be added
     * @param component The specific component which should be added
     * @return The specific component
     */
    public <T extends Component> T add(T component) {
        components.computeIfAbsent(component.getClass(), c -> new ArrayList<>()).add(component);
        return component;
    }

    /**
     * Get the components from the entity
     * 
     * @param <T> The type of Component which should be returned
     * @param type The class of Component which should be returned. The Class or inherited classes from it will match
     * @return A List of components which extend the type of component
     */
    public <T extends Component> ArrayList<T> getComponents(Class<T> type) {
        ArrayList<T> result = new ArrayList<>();
        for (ArrayList<Component> componentList : components.values()) {
            for (Component c : componentList) {
                if (type.isInstance(c)) {
                    result.add(type.cast(c));
                }
            }
        }
        return result;
    }

    /**
     * Get the components from the entity which implements a capability interface
     * 
     * @param <T> The type of the capability which should be returned
     * @param type The interface of capability which should be returned. The interface or inherited interfaces from it will match
     * @return A List of components which implement the capability
     */
    public <T extends Capability> ArrayList<T> getComponentsByCapability(Class<T> type) {

        ArrayList<T> result = new ArrayList<>();
        for (ArrayList<Component> componentList : components.values()) {
            for (Component c : componentList) {
                if (type.isInstance(c)) {
                    result.add(type.cast(c));
                }
            }
        }
        return result;
    }

    /**
     * Returns the type of the entity
     */
    public abstract EntityType getType();

    /**
     * Update the game object using delta time to get constant change with varying fps
     * 
     * @param deltaTime The time since last frame in seconds
     */
    public final void update(double deltaTime) {
        for (ArrayList<Component> entry : this.components.values()) {
            for (Component component : entry) {
                component.update(deltaTime);
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Entity entity = (Entity) object;
        return this.uuid.equals(entity.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
