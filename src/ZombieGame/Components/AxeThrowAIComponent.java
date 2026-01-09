package ZombieGame.Components;

import java.util.Optional;

import ZombieGame.PhysicsSystem;
import ZombieGame.Entities.Avatar;
import ZombieGame.Entities.Entity;
import ZombieGame.Entities.Zombie;

/**
 * Small helper AI for AXE zombies: if they still have an axe and the
 * player is within throw range (but not in melee range), start an attack.
 * The actual throw is handled by the ZombieAttackComponent's onHit handler.
 */
public class AxeThrowAIComponent extends Component {
    private final Zombie zombie;
    private final double minThrowDistance;
    private final double maxThrowDistance;

    public AxeThrowAIComponent(Zombie zombie, double minThrowDistance, double maxThrowDistance) {
        super(zombie);
        this.zombie = zombie;
        this.minThrowDistance = minThrowDistance;
        this.maxThrowDistance = maxThrowDistance;
    }

    @Override
    public void update(double deltaTime) {
        if (!zombie.isAxeZombie() || !zombie.hasAxe()) {
            return;
        }
        if (zombie.isAttacking()) {
            return;
        }

        Optional<Avatar> avatarOpt = Entity.world.getEntity(Avatar.class);
        if (avatarOpt.isEmpty()) {
            return;
        }

        Avatar avatar = avatarOpt.get();
        double dist = PhysicsSystem.distance(zombie.getPosX(), zombie.getPosY(), avatar.getPosX(), avatar.getPosY());

        // Only throw if not too close (melee) and not too far.
        if (dist >= minThrowDistance && dist <= maxThrowDistance) {
            zombie.tryStartAttack(avatar);
        }
    }
}
