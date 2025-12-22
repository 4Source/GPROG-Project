public interface Drawable extends Capability {
    /**
     * Draw the component in the graphics system.
     */
    public void draw();

    /**
     * Specifies the Layer to which the component should be drawn
     */
    public GraphicLayer getLayer();
}
