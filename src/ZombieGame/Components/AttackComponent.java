package ZombieGame.Components;

import ZombieGame.Systems.Physic.PhysicsSystem;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Entities.Entity;

public abstract class AttackComponent extends Component {
    protected final double attackRange;
    protected final Offset offset;
    private final double attackCoolDown;
    private final double attackDuration;
    private final double attackHitTime;
    private Entity target;
    private double lastAttackTimer;
    private double attackTimer;
    private boolean onHitCalled;
    private boolean attacking;

    /**
     * @param entity The entity to which the components belongs to
     * @param attackRange The range when the target is within this range the attack could be started
     * @param offset The offset of the attack range relative to the entity position
     * @param attackCoolDown The cool down time how often an attack could be done
     * @param attackDuration The duration an attack lasts
     * @param attackHitTime The time which should in between the start and the end time of the attack when the {@link #onHit(Entity)} should be called
     */
    public AttackComponent(Entity entity, double attackRange, Offset offset, double attackCoolDown, double attackDuration, double attackHitTime) {
        super(entity);
        this.attackRange = attackRange;
        this.offset = offset;
        this.attackCoolDown = attackCoolDown;
        this.attackDuration = attackDuration;
        this.attackHitTime = attackHitTime;
        this.target = null;
        this.lastAttackTimer = 0;
        this.attackTimer = 0;
        this.onHitCalled = false;
        this.attacking = false;
    }

    public boolean canAttack(Entity target) {
        // Is already performing an attack
        if (attacking) {
            return false;
        }

        // Last Attack was not long enough ago
        if (this.lastAttackTimer < this.attackCoolDown) {
            return false;
        }

        // Target is not near enough
        double dist = PhysicsSystem.distance(this.getEntity().getPositionComponent().getWorldPos().add(this.offset), target.getPositionComponent().getWorldPos());
        if (dist > this.attackRange) {
            return false;
        }

        return true;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public boolean hasTarget() {
        return this.target != null;
    }

    public void stopAttack() {
        this.attacking = false;
        this.lastAttackTimer = 0;
        this.attackTimer = 0;
        this.onHitCalled = false;
        this.onAttackEnd(this.target);
        this.target = null;
    }

    protected abstract void onAttackStart(Entity target);

    protected abstract void onHit(Entity target);

    protected abstract void onAttackEnd(Entity target);

    @Override
    public void update(double deltaTime) {

        // Is already performing an attack
        if (this.attacking) {
            this.attackTimer += deltaTime;

            // Reached the time when the hit should be applied
            if (!this.onHitCalled && this.attackTimer >= this.attackHitTime) {
                this.onHitCalled = true;
                this.onHit(this.target);
            }

            // End of attack
            if (this.attackTimer >= this.attackDuration) {
                this.stopAttack();
            }
            return;
        }

        this.lastAttackTimer += deltaTime;

        // No target to attack
        if (this.target == null) {
            return;
        }

        // Target can not be attacked
        if (!this.canAttack(this.target)) {
            return;
        }

        // Start the attack
        this.attacking = true;
        this.onAttackStart(target);
    }
}
