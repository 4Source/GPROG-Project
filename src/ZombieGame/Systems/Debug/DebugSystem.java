package ZombieGame.Systems.Debug;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import ZombieGame.Viewport;
import ZombieGame.Capabilities.Debuggable;
import ZombieGame.Capabilities.DebuggableGeometry;
import ZombieGame.Capabilities.DebuggableText;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;

public class DebugSystem {
    private static final DebugSystem instance = new DebugSystem();
    private Map<DebugCategory, ArrayList<DebuggableGeometry>> debuggablesGeometry;
    private Map<DebugCategory, ArrayList<DebuggableText>> debuggablesText;
    private Map<DebugCategory, Boolean> enabledCategories;

    private DebugSystem() {
        this.debuggablesGeometry = new EnumMap<>(DebugCategory.class);
        this.debuggablesText = new EnumMap<>(DebugCategory.class);
        this.enabledCategories = new EnumMap<>(DebugCategory.class);
    }

    /**
     * @return The instance of the singleton
     */
    public static synchronized DebugSystem getInstance() {
        return instance;
    }

    private <T extends Debuggable> boolean registerDebuggable(T debuggable, Map<DebugCategory, ArrayList<T>> debuggables) {
        boolean success = true;

        for (DebugCategory category : debuggable.getCategories()) {
            ArrayList<T> list = debuggables.computeIfAbsent(category, d -> new ArrayList<>());

            if (list.contains(debuggable)) {
                continue;
            }

            if (!list.add(debuggable)) {
                success = false;
            }
        }

        return success;
    }

    /**
     * Register a debuggable for to its categories
     * 
     * @param debuggable The debuggable to register
     * @return {@code true} if the registration was successful or if it was already registered
     */
    public boolean registerDebuggable(DebuggableText debuggable) {
        return registerDebuggable(debuggable, this.debuggablesText);
    }

    /**
     * Register a debuggable for to its categories
     * 
     * @param debuggable The debuggable to register
     * @return {@code true} if the registration was successful or if it was already registered
     */
    public boolean registerDebuggable(DebuggableGeometry debuggable) {
        return registerDebuggable(debuggable, this.debuggablesGeometry);
    }

    private <T extends Debuggable> boolean unregisterDebuggable(T debuggable, Map<DebugCategory, ArrayList<T>> debuggables) {
        boolean success = true;

        for (DebugCategory category : debuggable.getCategories()) {
            ArrayList<T> list = debuggables.computeIfAbsent(category, d -> new ArrayList<>());

            // Expected category is missing
            if (list == null) {
                success = false;
                continue;
            }

            // Not contained
            if (!list.remove(debuggable)) {
                success = false;
            }
        }

        return success;
    }

    /**
     * Unregister a debuggable from the visualization
     * 
     * @param debuggable The debuggable to unregister
     * @return {@code true} if unregistering was successful, {@code false} if not successful or not contained
     */
    public boolean unregisterDebuggable(DebuggableText debuggable) {
        return unregisterDebuggable(debuggable, this.debuggablesText);
    }

    /**
     * Unregister a debuggable from the visualization
     * 
     * @param debuggable The debuggable to unregister
     * @return {@code true} if unregistering was successful, {@code false} if not successful or not contained
     */
    public boolean unregisterDebuggable(DebuggableGeometry debuggable) {
        return unregisterDebuggable(debuggable, this.debuggablesGeometry);
    }

    public void draw() {
        ViewPos base = Viewport.getTopLeft().add(20, 60);
        int yIndex = 0;
        DrawStyle textStyle = new DrawStyle().color(Color.WHITE);

        for (Map.Entry<DebugCategory, Boolean> entry : enabledCategories.entrySet()) {
            DebugCategory key = entry.getKey();
            boolean value = entry.getValue();

            if (value && this.debuggablesGeometry.containsKey(key)) {
                ArrayList<DebuggableGeometry> list = this.debuggablesGeometry.get(key);

                for (DebuggableGeometry d : list) {
                    d.drawDebug();
                }
            }
            if (value && this.debuggablesText.containsKey(key)) {
                ArrayList<DebuggableText> list = this.debuggablesText.get(key);

                for (DebuggableText debuggable : list) {
                    for (String text : debuggable.getTextElements()) {
                        GraphicSystem.getInstance().drawString(text, base.add(0, yIndex * 30), textStyle);
                        yIndex++;
                    }
                }
            }
        }
    }

    public void update() {
        if (InputSystem.getInstance().isPressed(Action.DEBUG_PERFORMANCE)) {
            this.enabledCategories.put(DebugCategory.PERFORMANCE, !this.enabledCategories.computeIfAbsent(DebugCategory.PERFORMANCE, c -> {
                return false;
            }));
            System.out.println("Toggle DEBUG_PERFORMANCE " + this.enabledCategories.get(DebugCategory.PERFORMANCE));
        }
        if (InputSystem.getInstance().isPressed(Action.DEBUG_WORLD)) {
            this.enabledCategories.put(DebugCategory.WORLD, !this.enabledCategories.computeIfAbsent(DebugCategory.WORLD, c -> {
                return false;
            }));
            System.out.println("Toggle DEBUG_WORLD " + this.enabledCategories.get(DebugCategory.WORLD));
        }
        if (InputSystem.getInstance().isPressed(Action.DEBUG_PHYSICS)) {
            this.enabledCategories.put(DebugCategory.PHYSICS, !this.enabledCategories.computeIfAbsent(DebugCategory.PHYSICS, c -> {
                return false;
            }));
            System.out.println("Toggle DEBUG_PHYSICS " + this.enabledCategories.get(DebugCategory.PHYSICS));
        }
        if (InputSystem.getInstance().isPressed(Action.DEBUG_COLLISION)) {
            this.enabledCategories.put(DebugCategory.COLLISION, !this.enabledCategories.computeIfAbsent(DebugCategory.COLLISION, c -> {
                return false;
            }));
            System.out.println("Toggle DEBUG_COLLISION " + this.enabledCategories.get(DebugCategory.COLLISION));
        }
        if (InputSystem.getInstance().isPressed(Action.DEBUG_AI)) {
            this.enabledCategories.put(DebugCategory.AI, !this.enabledCategories.computeIfAbsent(DebugCategory.AI, c -> {
                return false;
            }));
            System.out.println("Toggle DEBUG_AI " + this.enabledCategories.get(DebugCategory.AI));
        }
    }
}
