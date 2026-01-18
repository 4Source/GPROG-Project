package ZombieGame.Systems.Input;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import ZombieGame.Coordinates.ViewPos;

enum DeviceType {
	KEYBOARD, MOUSE,
}

public class InputSystem implements KeyListener, MouseListener, MouseMotionListener {
	private static InputSystem instance;
	private final EnumMap<Action, EnumMap<DeviceType, Integer>> keyMapping = new EnumMap<>(Action.class);

	private final EnumMap<Action, Boolean> actionDown = new EnumMap<>(Action.class);
	private final EnumMap<Action, Boolean> actionPressed = new EnumMap<>(Action.class);
	private int mousePositionX;
	private int mousePositionY;

	private InputSystem() {
		this.setKeyMapping(Action.GAME_PAUSE, DeviceType.KEYBOARD, KeyEvent.VK_ESCAPE);
		this.setKeyMapping(Action.MOVE_UP, DeviceType.KEYBOARD, KeyEvent.VK_W);
		this.setKeyMapping(Action.MOVE_LEFT, DeviceType.KEYBOARD, KeyEvent.VK_A);
		this.setKeyMapping(Action.MOVE_DOWN, DeviceType.KEYBOARD, KeyEvent.VK_S);
		this.setKeyMapping(Action.MOVE_RIGHT, DeviceType.KEYBOARD, KeyEvent.VK_D);
		this.setKeyMapping(Action.INTERACT, DeviceType.KEYBOARD, KeyEvent.VK_E);
		this.setKeyMapping(Action.SHOOT, DeviceType.MOUSE, MouseEvent.BUTTON1);
		// this.setKeyMapping(Action.RELOAD, DeviceType.KEYBOARD, KeyEvent.VK_R);

		this.setKeyMapping(Action.DEBUG_PERFORMANCE, DeviceType.KEYBOARD, KeyEvent.VK_F1);
		this.setKeyMapping(Action.DEBUG_WORLD, DeviceType.KEYBOARD, KeyEvent.VK_F2);
		this.setKeyMapping(Action.DEBUG_PHYSICS, DeviceType.KEYBOARD, KeyEvent.VK_F3);
		this.setKeyMapping(Action.DEBUG_COLLISION, DeviceType.KEYBOARD, KeyEvent.VK_F4);
		this.setKeyMapping(Action.DEBUG_AI, DeviceType.KEYBOARD, KeyEvent.VK_F5);
		this.setKeyMapping(Action.DEBUG_UI, DeviceType.KEYBOARD, KeyEvent.VK_F6);

		this.setKeyMapping(Action.PRIMARY_CLICK, DeviceType.MOUSE, MouseEvent.BUTTON1);
	}

	/**
	 * @return The instance of the singleton or newly created if first access.
	 */
	public static synchronized InputSystem getInstance() {
		if (instance == null) {
			instance = new InputSystem();
		}

		return instance;
	}

	private void setKeyMapping(Action action, DeviceType type, int key) {
		this.keyMapping.putIfAbsent(action, new EnumMap<>(DeviceType.class));
		this.keyMapping.get(action).put(type, key);
	}

	private int getKeyMapping(Action action, DeviceType type) {
		EnumMap<DeviceType, Integer> actionMap = this.keyMapping.get(action);
		if (actionMap == null) {
			return -1;
		}
		return actionMap.getOrDefault(type, -1);
	}

	/**
	 * Returns the Key Mappings for the action as strings
	 * 
	 * @param action The action for which the key mappings should be returned
	 */
	public List<String> getKeyMapping(Action action) {
		ArrayList<String> keys = new ArrayList<>();
		EnumMap<DeviceType, Integer> actionMap = this.keyMapping.get(action);
		if (actionMap == null) {
			return keys;
		}

		int key = actionMap.getOrDefault(DeviceType.KEYBOARD, -1);
		if (key != -1) {
			keys.add(KeyEvent.getKeyText(key));
		}

		int button = actionMap.getOrDefault(DeviceType.MOUSE, -1);
		if (button != -1) {
			keys.add(String.format("Mouse Button %d", button));
		}

		return keys;
	}

	/**
	 * Check if the key for the action is currently hold down
	 * 
	 * @param action The action which should be checked
	 */
	public boolean isDown(Action action) {
		return this.actionDown.getOrDefault(action, false);
	}

	/**
	 * Check if the key for the action was pressed
	 * 
	 * @param action The action which should be checked
	 */
	public boolean isPressed(Action action) {
		return this.actionPressed.getOrDefault(action, false);
	}

	public ViewPos getMousePosition() {
		return new ViewPos(mousePositionX, mousePositionY);
	}

	/**
	 * Clear the pressed keys.
	 */
	public void clear() {
		for (Action a : actionPressed.keySet()) {
			actionPressed.put(a, false);
		}
	}

	public void mousePressed(MouseEvent event) {
		for (Action a : keyMapping.keySet()) {
			if (event.getButton() == getKeyMapping(a, DeviceType.MOUSE)) {
				this.actionDown.put(a, true);
			}
		}
	}

	public void mouseReleased(MouseEvent event) {
		for (Action a : keyMapping.keySet()) {
			if (event.getButton() == getKeyMapping(a, DeviceType.MOUSE)) {
				this.actionDown.put(a, false);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		for (Action a : keyMapping.keySet()) {
			if (event.getButton() == getKeyMapping(a, DeviceType.MOUSE)) {
				this.actionPressed.put(a, true);
			}
		}
	}

	public void mouseMoved(MouseEvent event) {
		this.mousePositionX = event.getX();
		this.mousePositionY = event.getY();
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		this.mousePositionX = event.getX();
		this.mousePositionY = event.getY();
	}

	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	@Override
	public void keyTyped(KeyEvent event) {
	}

	@Override
	public void keyPressed(KeyEvent event) {
		for (Action a : keyMapping.keySet()) {
			if (event.getKeyCode() == getKeyMapping(a, DeviceType.KEYBOARD)) {
				this.actionDown.put(a, true);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		for (Action a : keyMapping.keySet()) {
			if (event.getKeyCode() == getKeyMapping(a, DeviceType.KEYBOARD)) {
				this.actionDown.put(a, false);
				this.actionPressed.put(a, true);
			}
		}
	}
}
