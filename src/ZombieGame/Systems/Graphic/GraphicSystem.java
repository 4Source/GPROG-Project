package ZombieGame.Systems.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import ZombieGame.Viewport;
import ZombieGame.Capabilities.DebuggableText;
import ZombieGame.Capabilities.Drawable;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.Rotation;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;
import ZombieGame.Systems.Debug.DebugSystem;
import ZombieGame.Systems.Input.InputSystem;
import ZombieGame.Sprites.SpriteManager;

public class GraphicSystem extends JPanel implements DebuggableText {
    private static final GraphicSystem instance = new GraphicSystem();
    private EnumMap<GraphicLayer, ArrayList<Drawable>> drawables;
    private long lastTime;
    private long frameTime;
    private long drawTime;
    private long drawTimeBackground;
    private long drawTimeGame;
    private long sortTimeGame;
    private long drawTimeEffects;
    private long sortTimeEffects;
    private long drawTimeUi;
    private long drawTimeDebug;

    // GraphicsSystem variables
    private GraphicsConfiguration graphicsConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private BufferedImage imageBuffer;
    private Graphics2D graphics;

    private GraphicSystem() {
        this.setSize(Viewport.getScreenWidth(), Viewport.getScreenHeight());
        this.imageBuffer = this.graphicsConf.createCompatibleImage(this.getWidth(), this.getHeight());
        this.graphics = this.imageBuffer.createGraphics();

        // initialize Listeners
        this.addMouseListener(InputSystem.getInstance());
        this.addMouseMotionListener(InputSystem.getInstance());
        this.addKeyListener(InputSystem.getInstance());

        this.drawables = new EnumMap<>(GraphicLayer.class);
        this.lastTime = System.currentTimeMillis();
        this.frameTime = 0;
        this.drawTime = 0;
        this.drawTimeBackground = 0;
        this.drawTimeGame = 0;
        this.sortTimeGame = 0;
        this.drawTimeEffects = 0;
        this.sortTimeEffects = 0;
        this.drawTimeUi = 0;
        this.drawTimeDebug = 0;

        if (!DebugSystem.getInstance().registerDebuggable(this)) {
            System.err.println(String.format("Failed to register graphic system to debug system"));
        }
    }

    /**
     * @return The instance of the singleton or newly created if first access.
     */
    public static synchronized GraphicSystem getInstance() {
        return instance;
    }

    /**
     * Register a drawable for the visualization
     * 
     * @param drawable The drawable to register
     * @param layer The Layer the drawable should be drawn in
     * @return {@code true} if the registration was successful or if it was already registered
     */
    public boolean registerDrawable(Drawable drawable) {
        ArrayList<Drawable> list = drawables.computeIfAbsent(drawable.getLayer(), c -> new ArrayList<>());
        if (list.contains(drawable)) {
            return true;
        } else {
            return list.add(drawable);
        }
    }

    public java.awt.FontMetrics getFontMetrics(java.awt.Font font) {
        return this.graphics.getFontMetrics(font);
    }

    // Cached pause-menu help panel background (PNG)
    private BufferedImage helpPanelImage;

    /**
     * Returns the help panel background image.
     * Path is relative to the project root.
     */
    public BufferedImage getHelpPanelImage() {
        if (helpPanelImage == null) {
            helpPanelImage = SpriteManager.getSprite("assets/help_menu.png");
        }
        return helpPanelImage;
    }

    /**
     * Unregister a drawable from the visualization
     * 
     * @param drawable The drawable to unregister
     * @return {@code true} if unregistering was successful, {@code false} if not successful or not contained
     */
    public boolean unregisterDrawable(Drawable drawable) {
        ArrayList<Drawable> list = drawables.get(drawable.getLayer());
        if (list != null) {
            return list.remove(drawable);
        }
        return false;
    }

    /**
     * Clear the screen before drawing on it
     */
    public void clear() {
        this.graphics.setColor(Color.LIGHT_GRAY);
        this.graphics.fillRect(0, 0, Viewport.getScreenWidth(), Viewport.getScreenHeight());
    }

    /**
     * Set the style
     */
    private void setStyle(DrawStyle style) {
        graphics.setColor(style.color());
        graphics.setStroke(style.stroke());
        graphics.setFont(style.font());
    }

    /**
     * Draw the entities on the Screen
     */
    public void draw() {
        long start = System.nanoTime();
        drawables.getOrDefault(GraphicLayer.BACKGROUND, new ArrayList<>()).forEach(entity -> entity.draw());
        long endBackground = System.nanoTime();
        ArrayList<Drawable> game = drawables.getOrDefault(GraphicLayer.GAME, new ArrayList<>());
        game.sort(null);
        long endSortGame = System.nanoTime();
        game.forEach(entity -> entity.draw());
        long endGame = System.nanoTime();
        ArrayList<Drawable> effects = drawables.getOrDefault(GraphicLayer.EFFECTS, new ArrayList<>());
        effects.sort(null);
        long endSortEffects = System.nanoTime();
        effects.forEach(entity -> entity.draw());
        long endEffects = System.nanoTime();
        drawables.getOrDefault(GraphicLayer.UI, new ArrayList<>()).forEach(entity -> entity.draw());
        long endUi = System.nanoTime();
        DebugSystem.getInstance().draw();
        long endDebug = System.nanoTime();

        this.drawTime = endDebug - start;
        this.drawTimeBackground = endBackground - start;
        this.drawTimeGame = endGame - endBackground;
        this.sortTimeGame = endSortGame - endBackground;
        this.drawTimeEffects = endEffects - endGame;
        this.sortTimeEffects = endSortEffects - endGame;
        this.drawTimeUi = endUi - endEffects;
        this.drawTimeDebug = endDebug - endUi;
    }

    /**
     * Renders the text of the specified {@code String}, using the current text attribute state in the {@code Graphics2D} context.
     * The baseline of the first character is at position (<i>x</i>,&nbsp;<i>y</i>) in the User Space.
     * The rendering attributes applied include the {@code Clip}, {@code Transform}, {@code Paint}, {@code Font} and {@code Composite} attributes. For characters in script systems such as Hebrew and Arabic, the glyphs can be rendered from right to left, in which case the coordinate supplied is the location of the leftmost character on the baseline.
     * 
     * @param str the string to be rendered
     * @param x the x coordinate of the lower left location where the {@code String} should be rendered
     * @param y the y coordinate of the lower left location where the {@code String} should be rendered
     * @param style the style of the string to be drawn.
     */
    public void drawString(String str, ViewPos pos, DrawStyle style) {
        this.setStyle(style);
        this.graphics.drawString(str, pos.x(), pos.y());
    }

    /**
     * Draws the outline of an oval.
     * The result is a circle or ellipse that fits within the rectangle specified by the {@code x}, {@code y}, {@code width}, and {@code height} arguments.
     * <p>
     * The oval covers an area that is
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * and <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * 
     * @param x the x coordinate of the center of the oval to be filled.
     * @param y the y coordinate of the center of the oval to be filled.
     * @param width the width of the oval to be filled.
     * @param height the height of the oval to be filled.
     * @param style the style of the oval to be filled.
     */
    public void drawFillOval(ViewPos pos, int width, int height, DrawStyle style) {
        this.setStyle(style);
        this.graphics.fillOval(pos.x() - width / 2, pos.y() - height / 2, width, height);
    }

    /**
     * Fills an oval bounded by the specified rectangle with the current color.
     * 
     * @param x the x coordinate of the center of the oval to be drawn.
     * @param y the y coordinate of the center of the oval to be drawn.
     * @param width the width of the oval to be drawn.
     * @param height the height of the oval to be drawn.
     * @param style the style of the oval to be drawn.
     */
    public void drawOval(ViewPos pos, int width, int height, DrawStyle style) {
        this.setStyle(style);
        this.graphics.drawOval(pos.x() - width / 2, pos.y() - height / 2, width, height);
    }

    /**
     * Draws the outline of the specified rectangle.
     * The left and right edges of the rectangle are at {@code x} and <code>x&nbsp;+&nbsp;width</code>.
     * The top and bottom edges are at {@code y} and <code>y&nbsp;+&nbsp;height</code>.
     * The rectangle is drawn using the graphics context's current color.
     * 
     * @param x the x coordinate of the center of the rectangle to be filled.
     * @param y the y coordinate of the center of the rectangle to be filled.
     * @param width the width of the rectangle to be filled.
     * @param height the height of the rectangle to be filled.
     * @param style the style of the rectangle to be filled.
     */
    public void drawFillRect(ViewPos pos, int width, int height, DrawStyle style) {
        this.setStyle(style);
        this.graphics.fillRect(pos.x() - width / 2, pos.y() - height / 2, width, height);
    }

    /**
     * Draws the outline of the specified rectangle. The left and right edges of the rectangle are at x and x + width. The top and bottom edges are at y and y + height. The rectangle is drawn using the graphics context's current color.
     * 
     * @param x the x coordinate of the center of the rectangle to be drawn.
     * @param y the y coordinate of the center of the rectangle to be drawn.
     * @param width the width of the rectangle to be drawn.
     * @param height the height of the rectangle to be drawn.
     * @param style the style of the rectangle to be drawn.
     */
    public void drawRect(ViewPos pos, int width, int height, DrawStyle style) {
        this.setStyle(style);
        this.graphics.drawRect(pos.x() - width / 2, pos.y() - height / 2, width, height);
    }

    /**
     * Draws a line between two points
     * 
     * @param start The starting position of the line.
     * @param end The ending position of the line.
     * @param style The style to use when drawing the line.
     */
    public void drawLine(ViewPos start, ViewPos end, DrawStyle style) {
        this.setStyle(style);
        graphics.drawLine(start.x(), start.y(), end.x(), end.y());
    }

    /**
     * Draws a line starting at a position, with a given rotation and length.
     * 
     * @param pos The starting position of the line.
     * @param rotation Rotation in radians. 0 points to the right, positive counter-clockwise.
     * @param length The length of the line in pixels.
     * @param style The style to use when drawing the line.
     */
    public void drawLine(ViewPos pos, double rotation, int length, DrawStyle style) {
        this.setStyle(style);
        int x2 = (int) (pos.x() + Math.cos(rotation) * length);
        int y2 = (int) (pos.y() + Math.sin(rotation) * length);
        graphics.drawLine(pos.x(), pos.y(), x2, y2);
    }

    /**
     * Draws as much of the specified image to fit inside the specified rectangle.
     * 
     * @param sprite The sprite to draw
     * @param pos Coordinates of the center of the sprite to be drawn.
     * @param columnIndex The column at which the sprite is located on the sprite sheet
     * @param rowIndex The row at which the sprite is located on the sprite sheet
     * @param scale The scale which should be applied to the sprite
     * @param rotation Rotation around the rotationCenter
     * @param rotationCenter The center to rotate around
     * @param spriteWidth The original width of the of the sprite.
     * @param spriteHeight The original height of the of the sprite.
     * @param tint A color which could be added only where the sprite is opaque. {@code null} is also valid if no tint should be applied prefer: {@link #drawSprite(BufferedImage, ViewPos, int, int, double, Rotation, Offset, int, int) drawSprite}
     */
    public void drawSprite(BufferedImage sprite, ViewPos pos, int columnIndex, int rowIndex, double scale, Rotation rotation, Offset rotationCenter, int spriteWidth, int spriteHeight, Color tint) {
        int drawWidth = (int) (spriteWidth * scale);
        int drawHeight = (int) (spriteHeight * scale);
        ViewPos drawPos = pos.sub(drawWidth / 2, drawHeight / 2);
        AffineTransform old = this.graphics.getTransform();

        // Set rotation
        if (!rotation.isZero()) {
            ViewPos pivot = pos.sub(drawWidth / 2, drawHeight / 2).add(rotationCenter.mul(scale));
            this.graphics.rotate(rotation.radians(), pivot.x(), pivot.y());
        }

        if (tint != null) {
            BufferedImage temp = new BufferedImage(drawWidth, drawHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tempGraphics = temp.createGraphics();

            tempGraphics.drawImage(sprite, 0, 0, drawWidth, drawHeight, (columnIndex * spriteWidth), (rowIndex * spriteHeight), ((columnIndex + 1) * spriteWidth), ((rowIndex + 1) * spriteHeight), null);

            tempGraphics.setComposite(AlphaComposite.SrcAtop);
            tempGraphics.setColor(tint);
            tempGraphics.fillRect(0, 0, drawWidth, drawHeight);
            tempGraphics.dispose();

            this.graphics.drawImage(temp, drawPos.x(), drawPos.y(), null);
        } else {
            this.graphics.drawImage(sprite, drawPos.x(), drawPos.y(), drawPos.x() + drawWidth, drawPos.y() + drawHeight, (columnIndex * spriteWidth), (rowIndex * spriteHeight), ((columnIndex + 1) * spriteWidth), ((rowIndex + 1) * spriteHeight), null);
        }

        // Reset rotation
        this.graphics.setTransform(old);
    }

    /**
     * Draws as much of the specified image to fit inside the specified rectangle.
     * 
     * @param sprite The sprite to draw
     * @param pos Coordinates of the center of the sprite to be drawn.
     * @param columnIndex The column at which the sprite is located on the sprite sheet
     * @param rowIndex The row at which the sprite is located on the sprite sheet
     * @param scale The scale which should be applied to the sprite
     * @param rotation Rotation around the rotationCenter
     * @param rotationCenter The center to rotate around
     * @param spriteWidth The original width of the of the sprite.
     * @param spriteHeight The original height of the of the sprite.
     */
    public void drawSprite(BufferedImage sprite, ViewPos pos, int columnIndex, int rowIndex, double scale, Rotation rotation, Offset rotationCenter, int spriteWidth, int spriteHeight) {
        this.drawSprite(sprite, pos, columnIndex, rowIndex, scale, rotation, rotationCenter, spriteWidth, spriteHeight, null);
    }

    /**
     * Draws as much of the specified image to fit inside the specified rectangle.
     * 
     * @param sprite The sprite to draw
     * @param pos Coordinates of the center of the sprite to be drawn.
     * @param columnIndex The column at which the sprite is located on the sprite sheet
     * @param rowIndex The row at which the sprite is located on the sprite sheet
     * @param scale The scale which should be applied to the sprite
     * @param spriteWidth The original width of the of the sprite.
     * @param spriteHeight The original height of the of the sprite.
     * @param tint A color which could be added only where the sprite is opaque. {@code null} is also valid if no tint should be applied prefer: {@link #drawSprite(BufferedImage, ViewPos, int, int, double, int, int) drawSprite}
     */
    public void drawSprite(BufferedImage sprite, ViewPos pos, int columnIndex, int rowIndex, double scale, int spriteWidth, int spriteHeight, Color tint) {
        this.drawSprite(sprite, pos, columnIndex, rowIndex, scale, Rotation.ZERO, new Offset(), spriteWidth, spriteHeight, tint);
    }

    /**
     * Draws as much of the specified image to fit inside the specified rectangle.
     * 
     * @param sprite The sprite to draw
     * @param pos Coordinates of the center of the sprite to be drawn.
     * @param columnIndex The column at which the sprite is located on the sprite sheet
     * @param rowIndex The row at which the sprite is located on the sprite sheet
     * @param scale The scale which should be applied to the sprite
     * @param spriteWidth The original width of the of the sprite.
     * @param spriteHeight The original height of the of the sprite.
     */
    public void drawSprite(BufferedImage sprite, ViewPos pos, int columnIndex, int rowIndex, double scale, int spriteWidth, int spriteHeight) {
        this.drawSprite(sprite, pos, columnIndex, rowIndex, scale, spriteWidth, spriteHeight, null);
    }

    public void drawImage(BufferedImage image, ViewPos pos, int width, int height) {
        this.graphics.drawImage(image, pos.x(), pos.y(), width, height, null);
    }

    /**
     * Draw the objects to screen
     */
    public void swapBuffers() {
        long currentTime = System.nanoTime();
        this.frameTime = currentTime - lastTime;
        this.lastTime = currentTime;

        this.getGraphics().drawImage(this.imageBuffer, 0, 0, this);
    }

    public void saveAsGreyScaleImage(double[][] map, int w, int h, String filePath) {
        try {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    double v = map[y][x]; // 0..1
                    int g = (int) (v * 255.0); // 0..255
                    int rgb = (g << 16) | (g << 8) | g; // grayscale
                    img.setRGB(x, y, rgb);
                }
            }

            File file = new File(filePath);
            // Ensure parent directories exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Save image at path
            ImageIO.write(img, "png", file);
        } catch (Exception e) {
            System.err.println("Failed saving grey scale image with: " + e.getMessage());
        }
    }

    @Override
    public DebugCategoryMask getCategoryMask() {
        return new DebugCategoryMask(DebugCategory.PERFORMANCE);
    }

    @Override
    public ArrayList<String> getTextElements() {
        ArrayList<String> elements = new ArrayList<>();

        int backgroundSize = drawables.getOrDefault(GraphicLayer.BACKGROUND, new ArrayList<>()).size();
        int gameSize = drawables.getOrDefault(GraphicLayer.GAME, new ArrayList<>()).size();
        int effectsSize = drawables.getOrDefault(GraphicLayer.EFFECTS, new ArrayList<>()).size();
        int uiSize = drawables.getOrDefault(GraphicLayer.UI, new ArrayList<>()).size();

        elements.add(String.format("FPS: %d", (int) Math.round(1_000_000_000.0 / this.frameTime)));
        elements.add(String.format("Frame time: %.2f ms", this.frameTime / 1_000_000.0));
        elements.add(String.format("Draw time: %.2f ms", this.drawTime / 1_000_000.0));
        elements.add(String.format("  Background: %.2f ms", this.drawTimeBackground / 1_000_000.0));
        elements.add(String.format("  Game: %.2f ms", this.drawTimeGame / 1_000_000.0));
        elements.add(String.format("    Sort: %.2f ms", this.sortTimeGame / 1_000_000.0));
        elements.add(String.format("  Effects: %.2f ms", this.drawTimeEffects / 1_000_000.0));
        elements.add(String.format("    Sort: %.2f ms", this.sortTimeEffects / 1_000_000.0));
        elements.add(String.format("  Ui: %.2f ms", this.drawTimeUi / 1_000_000.0));
        elements.add(String.format("  Debug: %.2f ms", this.drawTimeDebug / 1_000_000.0));
        elements.add(String.format("Draw calls: %d", backgroundSize + gameSize + effectsSize + uiSize));
        elements.add(String.format("  Background: %d", backgroundSize));
        elements.add(String.format("  Game: %d", gameSize));
        elements.add(String.format("  Effects: %d", effectsSize));
        elements.add(String.format("  UI: %d", uiSize));
        return elements;
    }

    public void onViewportResize() {
        this.setSize(Viewport.getScreenWidth(), Viewport.getScreenHeight());
        this.imageBuffer = this.graphicsConf.createCompatibleImage(this.getWidth(), this.getHeight());
        this.graphics = this.imageBuffer.createGraphics();
    }
}
