package ZombieGame.Components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;

import ZombieGame.CharacterAction;
import ZombieGame.CharacterDirection;
import ZombieGame.Game;
import ZombieGame.Viewport;
import ZombieGame.Capabilities.DebuggableGeometry;
import ZombieGame.Capabilities.DebuggableText;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Avatar;
import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;

public class PlayerMovementComponent extends MovementComponent implements DebuggableText, DebuggableGeometry {

    /**
     * A Component which can move the entity via the inputs of the user.
     * 
     * @param entity The entity to which the components belongs to
     * @param pos The position in the world
     * @param speed The speed how fast to move
     */
    public PlayerMovementComponent(Avatar entity, WorldPos pos, double speed) {
        super(entity, pos, 0, speed);
    }

    @Override
    public void update(double deltaTime) {
        InputSystem input = InputSystem.getInstance();
        boolean moved = false;
        double dx = 0;
        double dy = 0;

        if (input.isDown(Action.MOVE_UP)) {
            dy -= 1;
            moved = true;
        }
        if (input.isDown(Action.MOVE_DOWN)) {
            dy += 1;
            moved = true;
        }
        if (input.isDown(Action.MOVE_LEFT)) {
            dx -= 1;
            moved = true;
        }
        if (input.isDown(Action.MOVE_RIGHT)) {
            dx += 1;
            moved = true;
        }

        if (moved) {
            this.getEntity().getVisualComponent().changeState(CharacterAction.MOVE);

            this.alpha = Math.atan2(dy, dx); // range: [-PI, PI]

            // no direction by w,a,s,d
            /*
            // RIGHT: include ±45°
            if (alpha >= -Math.PI / 4 && alpha <= Math.PI / 4) {
                this.getEntity().getVisualComponent().changeState(CharacterDirection.RIGHT);
            }
            // DOWN: strictly between 45° and 135°
            else if (alpha > Math.PI / 4 && alpha < 3 * Math.PI / 4) {
                this.getEntity().getVisualComponent().changeState(CharacterDirection.DOWN);
            }
            // UP: strictly between -135° and -45°
            else if (alpha > -3 * Math.PI / 4 && alpha < -Math.PI / 4) {
                this.getEntity().getVisualComponent().changeState(CharacterDirection.UP);
            }
            // LEFT: include ±135°
            else {
                this.getEntity().getVisualComponent().changeState(CharacterDirection.LEFT);
            }

             */

            super.update(deltaTime);
        } else if (this.getEntity().getVisualComponent().getCharacterAction() == CharacterAction.MOVE) {
            this.getEntity().getVisualComponent().changeState(CharacterAction.IDLE);
        }
    }

    @Override
    public Avatar getEntity() {
        return (Avatar) super.getEntity();
    }

    @Override
    public DebugCategoryMask getCategoryMask() {
        return new DebugCategoryMask(DebugCategory.WORLD);
    }

    @Override
    public ArrayList<String> getTextElements() {
        ArrayList<String> elements = new ArrayList<>();
        elements.add(String.format("Player Pos %s", this.getWorldPos().toString()));
        elements.add(String.format("Player Chunk %s", this.getWorldPos().toChunkIndex().toString()));
        elements.add(String.format("Center Chunk %s", Viewport.getCenter().toWorldPos(Game.world).toChunkIndex().toString()));
        return elements;
    }

    @Override
    public void drawDebug() {
        DrawStyle style = new DrawStyle().color(new Color(0, 36, 153)).stroke(new BasicStroke(2.0f));
        ViewPos view = this.getEntity().getPositionComponent().getViewPos();
        GraphicSystem.getInstance().drawLine(view, alpha, (int) (speed / 2), style);
    }
}
