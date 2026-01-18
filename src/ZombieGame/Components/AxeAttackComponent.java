package ZombieGame.Components;

import java.awt.Color;
import java.util.function.Consumer;

import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Physic.PhysicsSystem;
import ZombieGame.AIState;
import ZombieGame.CharacterAction;
import ZombieGame.Game;
import ZombieGame.Capabilities.DebuggableGeometry;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Entities.AxeProjectile;
import ZombieGame.Entities.AxeZombie;
import ZombieGame.Entities.Entity;

/**
 * Small helper AI for AXE zombies: if they still have an axe and the
 * player is within throw range (but not in melee range), start an attack.
 * The actual throw is handled by the ZombieAttackComponent's onHit handler.
 */
public class AxeAttackComponent extends AttackComponent implements DebuggableGeometry {
    private final double minThrowDistance;
    private final Consumer<Entity> onAttackStart;
    private final Consumer<Entity> onHit;
    private final Consumer<Entity> onAttackEnd;

    /**
     * @param axeZombie The entity to which the components belongs to
     * @param minThrowDistance The range when the target is to close to perform this attack
     * @param maxThrowDistance The range when the target is within this range the attack could be started
     * @param offset The offset of the attack range relative to the entity position
     * @param attackCoolDown The cool down time how often an attack could be done
     * @param attackDuration The duration an attack lasts
     * @param attackHitTime The time which should in between the start and the end time of the attack when the {@link #onHit(Entity)} should be called
     * @param onAttackStart The Callback function which gets executed if an attack starts
     * @param onHit The Callback function which gets executed when the attackHitTime is reached. (When the axe should be spawned)
     * @param onAttackEnd The Callback function which gets executed if an attack ends
     */
    public AxeAttackComponent(AxeZombie axeZombie, double minThrowDistance, double maxThrowDistance, Offset offset, double attackCoolDown, double attackDuration, double attackHitTime, Consumer<Entity> onAttackStart, Consumer<Entity> onHit, Consumer<Entity> onAttackEnd) {
        super(axeZombie, maxThrowDistance, offset, attackCoolDown, attackDuration, attackHitTime);

        this.minThrowDistance = minThrowDistance;
        this.onAttackStart = onAttackStart;
        this.onHit = onHit;
        this.onAttackEnd = onAttackEnd;
    }

    @Override
    public AxeZombie getEntity() {
        return (AxeZombie) super.getEntity();
    }

    @Override
    public boolean canAttack(Entity target) {
        if (!super.canAttack(target)) {
            return false;
        }

        // Zombie has no axe
        if (!this.getEntity().hasAxe()) {
            return false;
        }

        // Target is to close
        double dist = PhysicsSystem.distance(this.getEntity().getPositionComponent().getWorldPos().add(this.offset), target.getPositionComponent().getWorldPos());
        if (dist < this.minThrowDistance) {
            return false;
        }

        return true;
    }

    @Override
    protected void onAttackStart(Entity target) {
        this.onAttackStart.accept(target);
        this.getEntity().getPositionComponent().setState(AIState.ATTACKING);
        this.getEntity().getVisualComponent().changeState(CharacterAction.SPECIAL_ATTACK);
        this.getEntity().useAxe();
    }

    @Override
    protected void onHit(Entity target) {
        Game.world.spawnEntity(new AxeProjectile(this.getEntity(), this.getEntity().getPositionComponent().getWorldPos(), target.getPositionComponent().getWorldPos(), 200, 1.6, 3));

        this.onHit.accept(target);
    }

    @Override
    protected void onAttackEnd(Entity target) {
        this.onAttackEnd.accept(target);
        this.getEntity().getPositionComponent().setState(AIState.HUNTING);
        this.getEntity().getVisualComponent().changeState(CharacterAction.IDLE);
    }

    @Override
    public DebugCategoryMask getCategoryMask() {
        return new DebugCategoryMask(DebugCategory.AI);
    }

    @Override
    public void drawDebug() {
        GraphicSystem.getInstance().drawOval(this.getEntity().getPositionComponent().getViewPos().add(this.offset), (int) (this.attackRange * 2), (int) (this.attackRange * 2), new DrawStyle().color(Color.YELLOW));
        GraphicSystem.getInstance().drawOval(this.getEntity().getPositionComponent().getViewPos().add(this.offset), (int) (this.minThrowDistance * 2), (int) (this.minThrowDistance * 2), new DrawStyle().color(Color.YELLOW));
    }
}
