package ZombieGame.Components;

import java.awt.Color;

import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.Sprite;

public class LifeComponent extends LivingComponent {
    private static final double INITIAL_TIMEOUT = 0.1;
    private final int maxLife;
    private int life;

    private double damageFlashTimeout;

    public LifeComponent(Entity entity, int halfHearts) {
        super(entity);
        this.life = halfHearts;
        this.maxLife = halfHearts;
        this.damageFlashTimeout = 0.0;
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
     * @param halfHeartsDamage The damage in amount of hearts
     */
    public void takeDamage(int halfHeartsDamage) {
        if (halfHeartsDamage <= 0)
            return;

        this.life -= halfHeartsDamage;
        this.damageFlashTimeout = INITIAL_TIMEOUT;

        for (SpriteComponent c : this.getEntity().getComponents(SpriteComponent.class)) {
            for (Sprite s : c.getSprites()) {
                s.setTint(new Color(200, 0, 0, 50));
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
                        s.setTint(null);
                    }
                }
            }
        }
    }
}
