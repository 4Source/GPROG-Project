package ZombieGame.Components;

import java.util.EnumMap;

import ZombieGame.Action;
import ZombieGame.ActionHandler;
import ZombieGame.InputSystem;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.Gunshot;

public class GunshotComponent extends ActionComponent {
    private double timeSinceLastShot;
    private double fireRate;

    public GunshotComponent(Entity entity, double fireRate) {
        super(entity, self -> {
            EnumMap<Action, ActionHandler> map = new EnumMap<>(Action.class);

            map.put(Action.SHOOT, new ActionHandler.Down(dt -> ((GunshotComponent) self).onShoot(dt)));

            return map;
        });
        this.timeSinceLastShot = 0;
        this.fireRate = fireRate;
    }

    /**
     * The function executed if a the weapon is shoot
     * 
     * @param deltaTime The time since last frame in seconds
     */
    public void onShoot(double deltaTime) {
        this.timeSinceLastShot += deltaTime;
        if (this.timeSinceLastShot > fireRate) {
            timeSinceLastShot = 0;

            InputSystem input = InputSystem.getInstance();
            Gunshot shot = new Gunshot(this.getEntity().getPosX(), this.getEntity().getPosY(), input.getMousePositionX() + Entity.world.worldPartX, input.getMousePositionY() + Entity.world.worldPartY);
            Entity.world.spawnEntity(shot);
        }
    }
}
