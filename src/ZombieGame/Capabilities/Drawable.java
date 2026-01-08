package ZombieGame.Capabilities;

import ZombieGame.GraphicLayer;

public interface Drawable extends Capability, Comparable<Drawable> {
    /**
     * Draw the component in the graphics system.
     */
    public void draw();

    /**
     * Specifies the Layer to which the component should be drawn
     */
    public GraphicLayer getLayer();

    public int getDepth();

    @Override
    default int compareTo(Drawable o) {
        return Integer.compare(this.getDepth(), o.getDepth());
    }
}
