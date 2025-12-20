
// (c) Thorsten Hasbargen

final class Constants {
	// size of the world
	static final int WORLD_WIDTH = 5000;
	static final int WORLD_HEIGHT = 4000;
	// size of the displayed part of the world
	static final int WORLDPART_WIDTH = 1920;
	static final int WORLDPART_HEIGHT = 1080;
	// border: when to scroll
	static final int SCROLL_BOUNDS = 300;

	static final double SPAWN_INTERVAL = 0.2;
	static final double SPAWN_GRENADE = 10.0;
	static final double LIFE_GRENADE = 15.0;
}

enum EntityType {
	AVATAR, TEXT, TREE, ZOMBIE, SHOT, GRENADE_ITEM, UI
}