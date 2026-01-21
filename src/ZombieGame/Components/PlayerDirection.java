package ZombieGame.Components;

import ZombieGame.*;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Entity;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;

import java.util.EnumMap;


public class PlayerDirection extends ActionComponent{

    public PlayerDirection(Entity entity) {
        super(entity, self -> {

            EnumMap<Action, ActionHandler> map =
                    new EnumMap<>(Action.class);

            return map;
        });
    }

    private CharacterDirection getDirFromAngle(double angle) {

        double step = Math.PI / 4; // 45°

        if (angle >= -step / 2 && angle < step / 2)
            return CharacterDirection.RIGHT;

        if (angle >= step / 2 && angle < 3 * step / 2)
            return CharacterDirection.DOWN_RIGHT;

        if (angle >= 3 * step / 2 && angle < 5 * step / 2)
            return CharacterDirection.DOWN;

        if (angle >= 5 * step / 2 && angle < 7 * step / 2)
            return CharacterDirection.DOWN_LEFT;

        if (angle >= -3 * step / 2 && angle < -step / 2)
            return CharacterDirection.UP_RIGHT;

        if (angle >= -5 * step / 2 && angle < -3 * step / 2)
            return CharacterDirection.UP;

        if (angle >= -7 * step / 2 && angle < -5 * step / 2)
            return CharacterDirection.UP_LEFT;

        return CharacterDirection.LEFT;
    }


    private void updatePlayerDirection() {
        Avatar player = getEntity();
        InputSystem input = InputSystem.getInstance();

        WorldPos m = input.getMousePosition().toWorldPos(Game.world).sub(player.getPositionComponent().getWorldPos());

        double angle = Math.atan2(m.y(), m.x());

        CharacterDirection playerDir = getDirFromAngle(angle);
        CharacterAction currentAction = player.getVisualComponent().getCharacterAction();
        CharacterEquipment currentEquipment = player.getVisualComponent().getCharacterEquipment();
        CharacterAttackState attackState = player.getVisualComponent().getCharacterAttackState();

        player.getVisualComponent().changeState(playerDir);

        /*
        player.getVisualComponent().changeState(
                CharacterPart.HANDS,
                new CharacterAnimationKey(currentAction, playerDir, currentEquipment, attackState)
        );

         */
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        updatePlayerDirection();
    }

    @Override
    public Avatar getEntity() {
        return (Avatar) super.getEntity();
    }
}
