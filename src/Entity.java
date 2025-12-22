import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Entity {
    protected double posX, posY;
    protected static World world;
    private Map<Class<? extends Component>, Component> components = new HashMap<>();

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     */
    public Entity(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Add a component to the entity
     * 
     * @param <T> The type of Component which should be added
     * @param component The specific component which should be added
     * @return The specific component
     */
    public <T extends Component> T add(T component) {
        if (components.containsKey(component.getClass())) {
            System.out.println("Tried to add the same component twice. Will be overwritten!");
        }
        components.put(component.getClass(), component);
        return component;
    }

    /**
     * Get a component from the entity
     * 
     * @param <T> The type of Component which should be returned
     * @param type The class of Component which should be returned. The Class or inherited classes from it will match
     * @return A optional which could hold the Component with the class if it existed in the entity
     */
    public <T extends Component> Optional<T> getComponent(Class<T> type) {
        for (Component c : components.values()) {
            if (type.isInstance(c)) {
                return Optional.of(type.cast(c));
            }
        }
        return Optional.empty();
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
        for (Component c : components.values()) {
            if (type.isInstance(c)) {
                result.add(type.cast(c));
            }
        }
        return result;
    }

    /**
     * Get a component from the entity which implements a capability interface
     * 
     * @param <T> The type of the capability which should be returned
     * @param type The interface of capability which should be returned. The interface or inherited interfaces from it will match
     * @return An optional which could hold the Component with the class if it existed in the entity
     */
    public <T extends Capability> Optional<T> getComponentByCapability(Class<T> type) {
        for (Component c : components.values()) {
            if (type.isInstance(c)) {
                return Optional.of(type.cast(c));
            }
        }
        return Optional.empty();
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
        for (Component c : components.values()) {
            if (type.isInstance(c)) {
                result.add(type.cast(c));
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
        this.components.forEach((key, component) -> {
            component.update(deltaTime);
        });
    }

    /**
     * Set the world where the game objects are belonging to
     * 
     * @param world The world to which it should be set
     */
    static void setWorld(World world) {
        Entity.world = world;
    }
}
