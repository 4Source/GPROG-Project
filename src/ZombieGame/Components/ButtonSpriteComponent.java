package ZombieGame.Components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import ZombieGame.Capabilities.DebuggableGeometry;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.Sprite;
import ZombieGame.Sprites.StaticSprite;
import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicLayer;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;

public class ButtonSpriteComponent extends SpriteComponent implements DebuggableGeometry {
    private final StaticSprite button;
    private final StaticSprite buttonActive;
    private final Runnable onClick;
    private boolean isHovered;

    public ButtonSpriteComponent(Entity entity, StaticSprite button, StaticSprite buttonActive, Runnable onClick) {
        super(entity);

        this.button = button;
        this.buttonActive = buttonActive;
        this.onClick = onClick;
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.UI;
    }

    @Override
    protected Sprite getSprite(int index) {
        if (index == 0) {
            return button;
        } else if (index == 1) {
            return buttonActive;
        } else {
            throw new IndexOutOfBoundsException("ButtonSpriteComponent only allows index 0 (button) 1 (button active)");
        }
    }

    public double getHeight() {
        return this.button.getDrawHeight();
    }

    public double getWidth() {
        return this.button.getDrawWidth();
    }

    @Override
    protected Collection<? extends Sprite> getSprites() {
        ArrayList<StaticSprite> list = new ArrayList<>();
        list.add(button);
        list.add(buttonActive);
        return list;
    }

    @Override
    public void draw() {
        if (this.isHovered) {
            this.buttonActive.draw(this.getEntity().getPositionComponent().getViewPos());
        } else {
            button.draw(this.getEntity().getPositionComponent().getViewPos());
        }
    }

    @Override
    public void update(double deltaTime) {
        ViewPos mousePosition = InputSystem.getInstance().getMousePosition();

        if (!mousePosition.isInsideViewport()) {
            return;
        }

        ViewPos topLeft = this.getEntity().getPositionComponent().getViewPos().sub(new Offset(this.button.getDrawWidth() / 2, this.button.getDrawHeight() / 2));
        ViewPos bottomRight = this.getEntity().getPositionComponent().getViewPos().add(new Offset(this.button.getDrawWidth() / 2, this.button.getDrawHeight() / 2));

        if (mousePosition.x() > topLeft.x() && mousePosition.x() < bottomRight.x() && mousePosition.y() > topLeft.y() && mousePosition.y() < bottomRight.y()) {
            this.isHovered = true;

            if (this.isHovered && InputSystem.getInstance().isDown(Action.PRIMARY_CLICK)) {
                this.onClick.run();
            }
        } else {
            this.isHovered = false;
        }
    }

    @Override
    public DebugCategoryMask getCategoryMask() {
        return new DebugCategoryMask(DebugCategory.UI);
    }

    @Override
    public void drawDebug() {
        ViewPos center = this.getEntity().getPositionComponent().getViewPos();

        GraphicSystem.getInstance().drawRect(center, (int) this.button.getDrawWidth(), (int) this.button.getDrawHeight(), new DrawStyle().color(Color.BLUE));
    }

}
