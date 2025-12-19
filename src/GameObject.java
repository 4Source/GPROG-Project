public abstract class GameObject {
    protected double posX, posY;

    protected static World world;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     */
    public GameObject(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Update the game object using delta time to get constant change with varying fps
     * 
     * @param deltaTime The time since last frame
     */
    public abstract void update(double deltaTime);

    /**
     * Draw the game object in the graphics system. If visual representation is available
     */
    public abstract void draw();

    /**
     * Set the world where the game objects are belonging to
     * 
     * @param world The world to which it should be set
     */
    static void setWorld(World world) {
        GameObject.world = world;
    }
}
