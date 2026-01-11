package ZombieGame.Components;

import ZombieGame.AIState;
import ZombieGame.CharacterAction;
import ZombieGame.Systems.Physic.PhysicsSystem;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Zombie;

import java.util.function.BiConsumer;

/**
 * Handles zombie melee attacks so damage is applied when the attack animation hits,
 * not immediately on contact.
 */
public class ZombieAttackComponent extends Component {
    // Config
    private final int damage;
    private final double hitRange;
    private final double hitTimeSeconds;
    private final long coolDownMs;

    /**
     * Optional handler executed at hit-time (if target is still in range).
     * If present, it replaces the default "apply melee damage" behavior.
     */
    private final BiConsumer<Zombie, Avatar> onHit;

    private long lastAttackMillis = 0;
    private boolean attacking = false;
    private boolean damageApplied = false;
    private double attackTimer = 0;
    private Avatar target;

    public ZombieAttackComponent(Zombie zombie, int damage, long coolDownMs, double hitRange, double hitTimeSeconds) {
        this(zombie, damage, coolDownMs, hitRange, hitTimeSeconds, null);
    }

    public ZombieAttackComponent(Zombie zombie, int damage, long coolDownMs, double hitRange, double hitTimeSeconds, BiConsumer<Zombie, Avatar> onHit) {
        super(zombie);
        this.damage = damage;
        this.coolDownMs = coolDownMs;
        this.hitRange = hitRange;
        this.hitTimeSeconds = hitTimeSeconds;
        this.onHit = onHit;
    }

    /** Try to start an attack against a target. */
    public void tryStartAttack(Avatar target) {
        long now = System.currentTimeMillis();
        if (attacking) {
            return;
        }
        if (now - lastAttackMillis < coolDownMs) {
            return;
        }

        this.lastAttackMillis = now;
        this.attacking = true;
        this.damageApplied = false;
        this.attackTimer = 0;
        this.target = target;

        // Pause movement and play attack animation.
        this.getEntity().getPositionComponent().setState(AIState.IDLING);
        this.getEntity().getPositionComponent().stopMoving();
        this.getEntity().getVisualComponent().changeState(CharacterAction.ATTACK);
    }

    public void stopAttack() {
        attacking = false;
        target = null;
    }

    @Override
    public void update(double deltaTime) {
        if (!attacking) {
            return;
        }

        attackTimer += deltaTime;

        // Apply damage once at the hit moment, if still in range.
        if (!damageApplied && attackTimer >= hitTimeSeconds) {
            damageApplied = true;
            if (target != null) {
                double dist = PhysicsSystem.distance(this.getEntity().getPositionComponent().getWorldPos(), target.getPositionComponent().getWorldPos());
                if (dist <= hitRange) {
                    if (onHit != null) {
                        onHit.accept(this.getEntity(), target);
                    } else {
                        target.getLifeComponent().takeDamage(damage);
                    }
                }
            }
        }
    }

    @Override
    public Zombie getEntity() {
        return (Zombie) super.getEntity();
    }

    public boolean isAttacking() {
        return attacking;
    }
}
