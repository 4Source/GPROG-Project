import java.awt.Color;

public abstract class Obstacle extends Entity {

    /**
     * @param posX The position in x direction
     * @param posY The position in y direction
     * @param radius The size of the Obstacle
     * @param color The color of the Obstacle
     */
    public Obstacle(double posX, double posY, int radius, Color color) {
        super(posX, posY, radius, color);
    }
}
