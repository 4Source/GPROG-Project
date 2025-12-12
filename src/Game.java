
// (c) Thorsten Hasbargen

final class Game {
	private World world = null;

	public Game() {
		// Setup the window
		Frame frame = new Frame();
		frame.displayOnScreen();

		// Create a new world
		this.world = new ZombieWorld();

		this.world.setGraphicSystem(frame.getGraphicSystem());
		this.world.setInputSystem(frame.getInputSystem());

		GameObject.setWorld(this.world);
		TextObject.setWorld(this.world);
		frame.getGraphicSystem().setWorld(this.world);

		this.world.init();
		this.world.run();
	}

	public static void main(String[] args) {
		System.out.print("Starting game...");
		new Game();
		System.out.print("Game stopped...");
	}
}
