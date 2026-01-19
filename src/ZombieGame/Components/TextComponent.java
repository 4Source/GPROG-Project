package ZombieGame.Components;

import java.awt.*;
import java.awt.image.BufferedImage;

import ZombieGame.Entities.TextElement;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicSystem;

public abstract class TextComponent extends UIComponent {
    protected Font font;

    /**
     * @param entity The entity to which the components belongs to
     * @param color The color of the ui object
     * @param font The font to use to print the text to the screen
     */
    public TextComponent(TextElement entity, Color color, Font font) {
        super(entity, color);
        this.font = font;
    }

    public void draw() {
        String text = this.toString();
        var pos = this.getEntity().getPositionComponent().getViewPos();

        GraphicSystem g = GraphicSystem.getInstance();

        // Special rendering for the pause-menu help panel
        if (this.getEntity() instanceof ZombieGame.Entities.HelpText) {
            drawHelpPanel(g, pos, text);
            return;
        }

        // shadow (right bottom)
        DrawStyle shadowStyle = new DrawStyle()
                .color(new Color(57, 55, 55, 160)) // grey
                .font(this.font);

        DrawStyle textStyle = new DrawStyle()
                .color(this.color)
                .font(this.font);

        // draw shadow(2px right & bottom)
        g.drawString(text, pos.add(2, 2), shadowStyle);

        // maintext
        g.drawString(text, pos, textStyle);
    }

    /**
     * Draw the pause-menu help panel using a PNG background.
     * The panel is centered on {@code centerPos}. The first line is rendered as a centered header,
     * remaining lines are left-aligned with padding.
     */
    private void drawHelpPanel(GraphicSystem g, ZombieGame.Coordinates.ViewPos centerPos, String text) {
        String[] lines = text.split("\n", -1);
        if (lines.length == 0) {
            return;
        }

        // Layout config inside the PNG
        final int padding = 14;
        final int lineGap = 4;

        FontMetrics fm = g.getFontMetrics(this.font);
        int lineHeight = fm.getHeight();
        int ascent = fm.getAscent();

        // Load PNG panel background
        BufferedImage panel = g.getHelpPanelImage();
        if (panel == null) {
            return;
        }

        // Panel size comes from the PNG
        float scale = 0.3f; // size 0.5 = half size

        int panelW = Math.round(panel.getWidth() * scale);
        int panelH = Math.round(panel.getHeight() * scale);


        // Top-left for drawing the image and text placement
        int panelX = centerPos.x() - (panelW / 2);
        int panelY = centerPos.y() - (panelH / 2);

        // Draw the PNG (no generated colors)
        g.drawImage(panel, new ZombieGame.Coordinates.ViewPos(panelX, panelY), panelW, panelH);

        int leftX = panelX + padding+10;
        int topY = panelY + padding;

        // Text styles (keep the right-bottom shadow you already like)
        DrawStyle shadowStyle = new DrawStyle()
                .color(new Color(57, 55, 55, 160))
                .font(this.font);
        DrawStyle textStyle = new DrawStyle()
                .color(this.color)
                .font(this.font);

        // Header (first line centered)
        String header = lines[0];
        int headerW = fm.stringWidth(header);
        int headerX = centerPos.x() - (headerW / 2);
        int y = topY + ascent;

        // Shadow + header
        g.drawString(header, new ZombieGame.Coordinates.ViewPos(headerX + 2, y + 2), shadowStyle);
        g.drawString(header, new ZombieGame.Coordinates.ViewPos(headerX, y), textStyle);

        // Body lines (left aligned)
        y += lineHeight + lineGap;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line == null) line = "";

            // Skip leading empty lines nicely (still advances y)
            if (!line.isEmpty()) {
                g.drawString(line, new ZombieGame.Coordinates.ViewPos(leftX + 2, y + 2), shadowStyle);
                g.drawString(line, new ZombieGame.Coordinates.ViewPos(leftX, y), textStyle);
            }

            y += lineHeight + lineGap;
        }
    }



    /**
     * Returns the string which should be displayed on the screen
     * 
     * @return The string to display
     */
    public abstract String toString();

    @Override
    public TextElement getEntity() {
        return (TextElement) super.getEntity();
    }
}
