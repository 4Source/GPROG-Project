import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.function.Function;

public abstract class ActionComponent extends Component {
    private final EnumMap<Action, ActionHandler> actionHandlers;

    /**
     * @param entity The entity to which the components belongs to
     * @param action The action to to react on
     */
    protected ActionComponent(Entity entity, Function<ActionComponent, EnumMap<Action, ActionHandler>> factory) {
        super(entity);

        this.actionHandlers = factory.apply(this);

        if (this.actionHandlers.isEmpty()) {
            throw new IllegalArgumentException("At least one action must be bound for a ActionComponent");
        }
    }

    @Override
    public void update(double deltaTime) {
        InputSystem input = InputSystem.getInstance();

        for (Entry<Action, ActionHandler> entry : this.actionHandlers.entrySet()) {
            Action action = entry.getKey();
            ActionHandler handler = entry.getValue();

            if (handler instanceof ActionHandler.Down d && input.isDown(action)) {
                d.handler().accept(deltaTime);
            }

            if (handler instanceof ActionHandler.Pressed d && input.isPressed(action)) {
                d.handler().accept(deltaTime);
            }
        }
    }
}
