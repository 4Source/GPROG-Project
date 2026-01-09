package ZombieGame.Components;

import java.awt.Color;
import java.awt.Font;

import ZombieGame.DrawStyle;
import ZombieGame.GraphicLayer;
import ZombieGame.GraphicSystem;
import ZombieGame.Capabilities.Drawable;
import ZombieGame.Entities.Entity;

public class LifeComponent extends LivingComponent implements Drawable {
    private static final double INITIAL_TIMEOUT = 0.8;

    // life wird jetzt in "Halbherzen" gezählt:
    // 10 = 5 Herzen
    private int life;
    private final int maxLife;

    private double damageTextTimeout;
    private String damageText;

    public LifeComponent(Entity entity, int halfHearts) {
        super(entity);
        this.life = halfHearts;
        this.maxLife = halfHearts;
        this.damageTextTimeout = 0.0;
        this.damageText = "";
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

    /** Für alten Code: life == half-hearts */
    public int getLife() {
        return this.life;
    }

    /** Für alten Code: maxLife == maxHalfHearts */
    public int getMaxLife() {
        return this.maxLife;
    }

    public double getLifeRatio() {
        if (this.maxLife <= 0) return 0.0;
        return Math.max(0.0, Math.min(1.0, this.life / (double) this.maxLife));
    }

    /**
     * Damage in HALF-HEARTS.
     * 1 = 1/2 Herz, 2 = 1 Herz, ...
     */
    public void takeDamage(int halfHeartsDamage) {
        if (halfHeartsDamage <= 0) return;

        this.life -= halfHeartsDamage;
        this.damageTextTimeout = INITIAL_TIMEOUT;

        // schöner Text: -½ oder -1, -1½ etc.
        if (halfHeartsDamage == 1) {
            this.damageText = "-½";
        } else if (halfHeartsDamage % 2 == 0) {
            this.damageText = "-" + (halfHeartsDamage / 2);
        } else {
            this.damageText = "-" + (halfHeartsDamage / 2) + "½";
        }

        if (this.life <= 0) {
            this.life = 0;
            this.kill();
        }
    }

    /**
     * Heal in HALF-HEARTS.
     * 1 = 1/2 Herz
     */
    public void restoreHealth(int halfHeartsHeal) {
        if (halfHeartsHeal <= 0) return;

        this.life += halfHeartsHeal;
        if (this.life > this.maxLife) {
            this.life = this.maxLife;
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (this.damageTextTimeout > 0.0) {
            this.damageTextTimeout -= deltaTime;
        }
    }

    @Override
    public void draw() {
        if (this.damageTextTimeout > 0.0) {
            GraphicSystem.getInstance().drawString(
                damageText, 
                (int) Entity.world.worldToViewPosX(this.getEntity().getPosX()), 
                (int) Entity.world.worldToViewPosY(this.getEntity().getPosY()), 
                new DrawStyle().color(Color.RED).font(new Font("Arial", Font.PLAIN, 16)));
        }
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.EFFECTS;
    }

    @Override
    public int getDepth() {
        return (int) this.getEntity().getPosY();
    }
}
