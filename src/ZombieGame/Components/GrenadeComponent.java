package ZombieGame.Components;

import java.util.EnumMap;
import java.util.Optional;

import ZombieGame.Action;
import ZombieGame.ActionHandler;
import ZombieGame.InputSystem;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.GrenadesCounter;
import ZombieGame.Entities.Gunshot;

public class GrenadeComponent extends ActionComponent {

    public GrenadeComponent(Entity entity) {
        super(entity, self -> {
            EnumMap<Action, ActionHandler> map = new EnumMap<>(Action.class);

            map.put(Action.THROW_GRENADE, new ActionHandler.Pressed(dt -> ((GrenadeComponent) self).onThrow(dt)));

            return map;
        });
    }

    /**
     * The function executed if a grenade should be thrown
     * 
     * @param deltaTime The time since last frame in seconds
     */
    public void onThrow(double deltaTime) {
        Optional<GrenadesCounter> opt = Entity.world.getUIElement(GrenadesCounter.class);
        if (opt.isEmpty()) {
            System.err.println("Could not find GrenadesCounter");
            return;
        }

        GrenadesCounter grenades = opt.get();
        if (grenades.getNumber() <= 0) {
            return;
        }

        InputSystem input = InputSystem.getInstance();

        // throw grenade
        for (int i = 0; i < 100; i++) {
            double alfa = Math.random() * Math.PI * 2;
            double speed = 50 + Math.random() * 200;
            double time = 0.2 + Math.random() * 0.4;
            Gunshot shot = new Gunshot(input.getMousePositionX() + Entity.world.worldPartX, input.getMousePositionY() + Entity.world.worldPartY, alfa, speed, time);
            Entity.world.spawnEntity(shot);
        }

        // inform counter
        grenades.decrement();
    }
}
