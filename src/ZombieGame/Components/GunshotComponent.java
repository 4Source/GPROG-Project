package ZombieGame.Components;

import java.util.EnumMap;
import java.util.Optional;

import ZombieGame.Action;
import ZombieGame.ActionHandler;
import ZombieGame.InputSystem;
import ZombieGame.Entities.AmmunitionCounter;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.Gunshot;

public class GunshotComponent extends ActionComponent {
    private double timeSinceLastShot;
    private double fireRate;
    private int ammunitionCount;

    public GunshotComponent(Entity entity, double fireRate, int ammunitionCount) {
        super(entity, self -> {
            EnumMap<Action, ActionHandler> map = new EnumMap<>(Action.class);

            map.put(Action.SHOOT, new ActionHandler.Down(dt -> ((GunshotComponent) self).onShoot(dt)));

            return map;
        });
        this.timeSinceLastShot = 0;
        this.fireRate = fireRate;
        this.ammunitionCount = ammunitionCount;
    }

    /**
     * The function executed if a the weapon is shoot
     * 
     * @param deltaTime The time since last frame in seconds
     */
    public void onShoot(double deltaTime) {
        this.timeSinceLastShot += deltaTime;

        if (ammunitionCount <= 0) {
            return;
        }

        if (this.timeSinceLastShot > fireRate) {
            timeSinceLastShot = 0;
            ammunitionCount--;

            InputSystem input = InputSystem.getInstance();
            Gunshot shot = new Gunshot(this.getEntity().getPosX(), this.getEntity().getPosY(), Entity.world.viewToWorldPosX(input.getMousePositionX()), Entity.world.viewToWorldPosY(input.getMousePositionY()));
            Entity.world.spawnEntity(shot);

        }
    }

    /**
     * Add ammunition to the weapon
     * 
     * @param amount The amount of ammunition added
     */
    public void restockAmmunition(int amount) {
        this.ammunitionCount += amount;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        Optional<AmmunitionCounter> opt = Entity.world.getUIElement(AmmunitionCounter.class);
        if (!opt.isEmpty()) {
            AmmunitionCounter ammunitionUI = opt.get();
            ammunitionUI.setNumber(ammunitionCount);
        }

    }
}
