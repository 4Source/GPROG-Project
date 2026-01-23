package ZombieGame.Entities;

import java.awt.Color;

import ZombieGame.EntityType;
import ZombieGame.Components.CircleComponent;
import ZombieGame.Components.LifetimeComponent;
import ZombieGame.Components.WorldPositionComponent;
import ZombieGame.Coordinates.WorldPos;

/**
 * Small short-lived visual effect used for zombie blood.
 */
public class BloodSplat extends Entity {

    public BloodSplat(WorldPos pos, double lifetimeSeconds, int radiusPx, Color color) {
        super(e -> new WorldPositionComponent(e, pos));
        this.add(new CircleComponent(this, radiusPx, color));
        this.add(new LifetimeComponent(this, lifetimeSeconds));
    }

    public BloodSplat(WorldPos pos) {
        this(pos, 0.35, 3, new Color(180, 0, 0, 180));
    }

    @Override
    public EntityType getType() {
        return EntityType.EFFECT;
    }
}
