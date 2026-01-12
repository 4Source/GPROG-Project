package ZombieGame.Components;

import ZombieGame.*;
import ZombieGame.Entities.AmmunitionCounter;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.Gunshot;

import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Optional;

public class PlayerWeaponComponent extends ActionComponent{
    private static final Map<CharacterEquipment, WeaponStats> weaponStatsMap = new HashMap<>();

    static {
        weaponStatsMap.put(CharacterEquipment.BAT, new WeaponStats(0, 0));       // Nahkampf, keine Munition
        weaponStatsMap.put(CharacterEquipment.GUN, new WeaponStats(0.2, 30));
        weaponStatsMap.put(CharacterEquipment.PISTOL, new WeaponStats(0.4, 15));
        weaponStatsMap.put(CharacterEquipment.SHOTGUN, new WeaponStats(1.0, 8));
    }

    private static class WeaponStats {
        double fireRate;
        int ammunition;

        WeaponStats(double fireRate, int ammunition) {
            this.fireRate = fireRate;
            this.ammunition = ammunition;
        }
    }

    private double timeSinceLastShot;
    private double fireRate;
    private int ammunitionCount;

    public PlayerWeaponComponent(Entity entity) {
        super(entity, self -> {
            EnumMap<Action, ActionHandler> map = new EnumMap<>(Action.class);

            map.put(Action.SHOOT, new ActionHandler.Down(dt -> {
                Avatar player = (Avatar) self.getEntity();
                if(player.getVisualComponent().getCharacterEquipment()!=CharacterEquipment.HANDS){
                    player.getWeaponComponent().onShoot(dt);
                }
            }));

            return map;
        });

        this.timeSinceLastShot = 0;
        this.fireRate = 0;
        this.ammunitionCount = 0;
    }

    public void setWeapon(CharacterEquipment equipment) {
        WeaponStats stats = weaponStatsMap.getOrDefault(equipment, new WeaponStats(0, 0));
        this.timeSinceLastShot = stats.fireRate;
        this.fireRate = stats.fireRate;
        this.ammunitionCount = stats.ammunition;
    }

    private void updateGunDirection() {
        Avatar player = getEntity();
        InputSystem input = InputSystem.getInstance();

        double mx = input.getMousePositionX() + Entity.world.worldPartX - player.getPosX();
        double my = input.getMousePositionY() + Entity.world.worldPartY - player.getPosY();
        double angle = Math.atan2(my, mx);

        CharacterDirection gunDir = getDirFromAngle(angle);
        CharacterAction currentAction = player.getVisualComponent().getCharacterAction();
        CharacterEquipment currentEquipment = player.getVisualComponent().getCharacterEquipment();

        player.getVisualComponent().changeState(
                CharacterPart.HANDS,
                new CharacterAnimationKey(currentAction, gunDir, currentEquipment)
        );
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

    public void onShoot(double deltaTime) {
        if (ammunitionCount <= 0) { return; }

        if (timeSinceLastShot >= fireRate) {

            Avatar player = getEntity();

            this.getEntity().getVisualComponent().changeState(CharacterAction.ATTACK);

            timeSinceLastShot = 0;
            ammunitionCount--;

            InputSystem input = InputSystem.getInstance();
            Gunshot shot = new Gunshot(
                    this.getEntity().getPosX(),
                    this.getEntity().getPosY(),
                    input.getMousePositionX() + Entity.world.worldPartX,
                    input.getMousePositionY() + Entity.world.worldPartY);
            Entity.world.spawnEntity(shot);
        }
    }

    public void restockAmmunition(int amount) {
        this.ammunitionCount += amount;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        timeSinceLastShot += deltaTime;

        updateGunDirection();

        Optional<AmmunitionCounter> opt = Entity.world.getUIElement(AmmunitionCounter.class);
        if (!opt.isEmpty()) {
            AmmunitionCounter ammunitionUI = opt.get();
            ammunitionUI.setNumber(ammunitionCount);
        }
    }

    @Override
    public Avatar getEntity() {
        return (Avatar) super.getEntity();
    }
}