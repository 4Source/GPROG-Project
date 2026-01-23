package ZombieGame.Components;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

import ZombieGame.Game;
import ZombieGame.EntityType;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Entities.BloodSplat;

import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.Sprite;

public class LifeComponent extends LivingComponent {
    private static final double INITIAL_TIMEOUT = 0.1;
    private final int maxLife;
    private int life;

    private double damageFlashTimeout;

    /**
     * @param entity The entity to which the components belongs to
     * @param halfHearts The number of hearts which are max allowed and the initial number of hearts
     * @param onDie A callback function which gets called when the component is marked as dead
     */
    public LifeComponent(Entity entity, int halfHearts, Runnable onDie) {
        super(entity, onDie);
        this.life = halfHearts;
        this.maxLife = halfHearts;
        this.damageFlashTimeout = 0.0;
    }

    /**
     * @param entity The entity to which the components belongs to
     * @param halfHearts The number of hearts which are max allowed and the initial number of hearts
     */
    public LifeComponent(Entity entity, int halfHearts) {
        this(entity, halfHearts, () -> {});
    }

    /** Current half-hearts */
    public int getHalfHearts() {
        return this.life;
    }

    /** Max half-hearts */
    public int getMaxHalfHearts() {
        return this.maxLife;
    }

    /** Current full hearts (rounded up/down je nach Bedarf) */
    public int getHearts() {
        return (int) Math.ceil(this.life / 2.0);
    }

    public int getMaxHearts() {
        return (int) Math.ceil(this.maxLife / 2.0);
    }

    /**
     * Make damage to the entity
     * 
     * @param halfHeartsDamage The damage in amount of half hearts
     */
    public void takeDamage(int halfHeartsDamage) {
        if (halfHeartsDamage <= 0)
            return;

        this.life -= halfHeartsDamage;
        this.damageFlashTimeout = INITIAL_TIMEOUT;

        for (SpriteComponent c : this.getEntity().getComponents(SpriteComponent.class)) {
            for (Sprite s : c.getSprites()) {
                if(s != null){
                    s.setTint(new Color(200, 0, 0, 50));
                }
            }
        }



        // Spawn a short blood effect for zombies
        if (Game.world != null && this.getEntity().getType() == EntityType.ZOMBIE) {
            WorldPos p = this.getEntity().getPositionComponent().getWorldPos();
            int drops = Math.min(6, 2 + halfHeartsDamage * 2);

            for (int i = 0; i < drops; i++) {
                double ox = ThreadLocalRandom.current().nextDouble(-10, 10);
                double oy = ThreadLocalRandom.current().nextDouble(-10, 10);
                int r = ThreadLocalRandom.current().nextInt(2, 5);
                double lt = ThreadLocalRandom.current().nextDouble(0.2, 0.55);

                Game.world.spawnEntity(new BloodSplat(p.add(ox, oy), lt, r, new Color(180, 0, 0, 180)));
            }
        }
        if (this.life <= 0) {
            this.life = 0;
            this.kill();
        }
    }

    /**
     * Restore health of the entity
     * 
     * @param halfHeartsHeal The heal in amount of hearts
     */
    public void restoreHealth(int halfHeartsHeal) {
        if (halfHeartsHeal <= 0)
            return;

        this.life += halfHeartsHeal;
        if (this.life > this.maxLife) {
            this.life = this.maxLife;
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if (this.damageFlashTimeout > 0.0) {
            this.damageFlashTimeout -= deltaTime;

            // Timeout expired remove the tint
            if (this.damageFlashTimeout <= 0.0) {
                for (SpriteComponent c : this.getEntity().getComponents(SpriteComponent.class)) {
                    for (Sprite s : c.getSprites()) {
                        if(s != null){
                            s.setTint(null);
                        }
                    }
                }
            }
        }
    }
}
