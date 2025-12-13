import java.awt.Color;

public abstract class Entity extends GameObject {
    // TODO: Components list
    // protected ArrayList<Component> components;

    protected boolean isLiving = true;

    protected int radius = 7;
    protected Color color;

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param radius The size of the entity
     * @param color The color of the entity
     */
    public Entity(double posX, double posY, int radius, Color color) {
        super(posX, posY);
        this.radius = radius;
        this.color = color;
    }

    /**
     * @return Returns the type of the entity
     */
    public abstract int getType();

    @Override
    public void draw() {
        int x = (int) (this.posX - this.radius - GameObject.world.worldPartX);
        int y = (int) (this.posY - this.radius - GameObject.world.worldPartY);
        int d = (int) (this.radius * 2);

        GraphicSystem.getInstance().graphics.setColor(this.color);
        GraphicSystem.getInstance().graphics.fillOval(x, y, d, d);
        GraphicSystem.getInstance().graphics.setColor(Color.DARK_GRAY);
        GraphicSystem.getInstance().graphics.drawOval(x, y, d, d);
    }
}
