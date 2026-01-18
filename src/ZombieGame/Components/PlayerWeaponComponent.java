package ZombieGame.Components;

import ZombieGame.*;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.*;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;

import java.util.EnumMap;
import java.util.Optional;

public class PlayerWeaponComponent extends ActionComponent{

    private double timeSinceLastShot;
    private CharacterEquipment characterEquipment;
    private EquipmentStats equipmentStats;


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
    }

    public void setEquipment(CharacterEquipment equipment, EquipmentStats equipmentStats) {
        this.characterEquipment = equipment;
        this.equipmentStats = equipmentStats;
        this.timeSinceLastShot = equipmentStats.getFireRate();
    }

    public EquipmentStats getEquipmentStats() {
        return equipmentStats;
    }

    public void onShoot(double deltaTime) {
        if (equipmentStats.getAmmunition() <= 0) { return; }

        if (timeSinceLastShot < equipmentStats.getFireRate()) { return;} {

            Avatar player = getEntity();

            this.getEntity().getVisualComponent().changeState(CharacterAction.ATTACK);

            timeSinceLastShot = 0;
            equipmentStats.shoot();

            InputSystem input = InputSystem.getInstance();

            WorldPos shooterPos = this.getEntity()
                    .getPositionComponent()
                    .getWorldPos();

            WorldPos mousePos = input.getMousePosition()
                    .toWorldPos(Game.world);

            double baseAngle = Math.atan2(
                    mousePos.y() - shooterPos.y(),
                    mousePos.x() - shooterPos.x()
            );

            double spreadRad = Math.toRadians(equipmentStats.getAngleOfBullets());
            double startAngle = baseAngle - spreadRad * (equipmentStats.getAmountOfBullets() - 1) / 2.0;

            for (int i = 0; i < equipmentStats.getAmountOfBullets(); i++) {

                double angle = startAngle + spreadRad * i;

                double dirX = Math.cos(angle);
                double dirY = Math.sin(angle);

                WorldPos target = new WorldPos(
                        shooterPos.x() + dirX * 1000,
                        shooterPos.y() + dirY * 1000
                );

                Gunshot shot = new Gunshot(
                        this.getEntity(),
                        shooterPos,
                        target
                );

                Game.world.spawnEntity(shot);
            }
        }
    }

    public void restockAmmunition(int amount) {
        equipmentStats.addAmmunition(amount);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        timeSinceLastShot += deltaTime;

        if(equipmentStats != null) {
            Optional<AmmunitionCounter> opt = Game.world.getUIElement(AmmunitionCounter.class);
            if (!opt.isEmpty()) {
                AmmunitionCounter ammunitionUI = opt.get();
                ammunitionUI.setNumber(equipmentStats.getAmmunition());
            }
        }
    }

    @Override
    public Avatar getEntity() {
        return (Avatar) super.getEntity();
    }
}