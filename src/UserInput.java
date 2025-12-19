
// (c) Thorsten Hasbargen

// TODO: Create a proper input System ?
final class UserInput {
	// everything a user can press on keyboard or mouse
	int mousePressedX, mousePressedY, mouseMovedX, mouseMovedY, mouseButton;

	char keyPressed;

	// if Mouse was clicked, Key was pressed or Mouse is still hold down
	boolean isMouseEvent, isKeyEvent, isMousePressed;

	// ... is returned as a data set
	UserInput() {
		this.clear();
	}

	final void clear() {
		this.isMouseEvent = false;
		this.isKeyEvent = false;
	}
}
