package ZombieGame.Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ZombieGame.EntityType;
import ZombieGame.World;
import ZombieGame.Capabilities.Capability;
import ZombieGame.Components.Component;

public abstract class Entity {
    protected double posX, posY;
    public static World world;
    private Map<Class<? extends Component>, ArrayList<Component>> components = new HashMap<>();

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
        this.components.forEach((key, componentList) -> {
            componentList.forEach(component -> {
                component.update(deltaTime);
            });
        });
    }

    public final double getPosX() {
        return this.posX;
    }

    public final void setPosX(double x) {
        this.posX = x;
    }

    public final double getPosY() {
        return this.posY;
    }

    public final void setPosY(double y) {
        this.posY = y;
    }

    /**
     * Set the world where the game objects are belonging to
     * 
     * @param world The world to which it should be set
     */
    public static void setWorld(World world) {
        Entity.world = world;
    }
}
