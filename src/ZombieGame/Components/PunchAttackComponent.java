package ZombieGame.Components;

import java.awt.Color;
import java.util.function.Consumer;

import ZombieGame.AIState;
import ZombieGame.CharacterAction;
import ZombieGame.Capabilities.DebuggableGeometry;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.Zombie;
import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;
import ZombieGame.Systems.Graphic.DrawStyle;
import ZombieGame.Systems.Graphic.GraphicSystem;

public class PunchAttackComponent extends AttackComponent implements DebuggableGeometry {
    private final int damage;
    private final Consumer<Entity> onAttackStart;
    private final Consumer<Entity> onHit;
    private final Consumer<Entity> onAttackEnd;

    /**
     * @param zombie The entity to which the components belongs to
     * @param damage The damage in amount of half hearts which should be applied
     * @param attackRange The range when the target is within this range the attack could be started
     * @param attackCoolDown The cool down time how often an attack could be done
     * @param attackDuration The duration an attack lasts
     * @param attackHitTime The time which should in between the start and the end time of the attack when the {@link #onHit(Entity)} should be called
     * @param onAttackStart The Callback function which gets executed if an attack starts
     * @param onHit The Callback function which gets executed when the attackHitTime is reached
     * @param onAttackEnd The Callback function which gets executed if an attack ends
     */
    public PunchAttackComponent(Zombie zombie, int damage, double attackRange, double attackCoolDown, double attackDuration, double attackHitTime, Consumer<Entity> onAttackStart, Consumer<Entity> onHit, Consumer<Entity> onAttackEnd) {
        super(zombie, attackRange, attackCoolDown, attackDuration, attackHitTime);

        this.damage = damage;
        this.onAttackStart = onAttackStart;
        this.onHit = onHit;
        this.onAttackEnd = onAttackEnd;
    }

    @Override
    public Zombie getEntity() {
        return (Zombie) super.getEntity();
    }

    @Override
    protected void onAttackStart(Entity target) {
        this.onAttackStart.accept(target);
        this.getEntity().getPositionComponent().setState(AIState.ATTACKING);
        this.getEntity().getVisualComponent().changeState(CharacterAction.ATTACK);
    }

    @Override
    protected void onHit(Entity target) {
        for (LifeComponent component : target.getComponents(LifeComponent.class)) {
            component.takeDamage(damage);
        }
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
        GraphicSystem.getInstance().drawOval(this.getEntity().getPositionComponent().getViewPos(), (int) (this.attackRange * 2), (int) (this.attackRange * 2), new DrawStyle().color(Color.RED));
    }
}
