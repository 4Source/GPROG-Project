package ZombieGame.Entities;

import ZombieGame.CircleHitBox;
import ZombieGame.Collision;
import ZombieGame.CollisionResponse;
import ZombieGame.EntityType;
import ZombieGame.HitBoxType;
import ZombieGame.PhysicsCollisionLayer;
import ZombieGame.PhysicsCollisionMask;
import ZombieGame.Components.AnimatedSpriteComponent;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifetimeComponent;
import ZombieGame.Components.MovementComponent;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Components.StaticMovementComponent;
import ZombieGame.Sprites.LoopingSprite;

/**
 * Projectile thrown by AXE zombies.
 * Uses the asset pack's thrown-axe sprite so it's clearly visible.
 */
public class AxeProjectile extends Entity {
    private final LifetimeComponent lifetimeComponent;

    public AxeProjectile(double posX, double posY, double destX, double destY, int damage) {
        super(posX, posY);

        // Pick a fitting thrown-axe animation depending on the flight direction.
        double dx = destX - posX;
        double dy = destY - posY;
        String spritePath;
        if (Math.abs(dx) >= Math.abs(dy)) {
            // Sideways throw
            spritePath = dx < 0
                    ? "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Axe\\Axe\\Axe_Side-left_Thrown-Sheet9.png"
                    : "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Axe\\Axe\\Axe_Side_Thrown-Sheet9.png";
        } else {
            // Vertical throw
            spritePath = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Axe\\Axe\\Axe_Vertical_Thrown-Sheet9.png";
        }

        // 9 frames in a row, each 14x14px. Scale up so it's readable.
        // Use a looping sprite so the axe keeps spinning for its whole flight time.
        this.add(new AnimatedSpriteComponent(this, new LoopingSprite(spritePath, 9, 1, 3.0, 0.05, 0, 0)));

        // Lifetime so it doesn't fly forever.
        this.lifetimeComponent = this.add(new LifetimeComponent(this, 1.6));

        // Move towards target direction.
        double alpha = Math.atan2(dy, dx);
        MovementComponent movementComponent = this.add(new StaticMovementComponent(this, alpha, 340));

        // Collide with obstacles and the player.
        PhysicsComponent physicsComponent = this.add(
                new DynamicPhysicsComponent(
                        this,
                        new CircleHitBox(HitBoxType.Block, 10),
                        PhysicsCollisionLayer.PROJECTILE,
                        new PhysicsCollisionMask(PhysicsCollisionLayer.OBSTACLES, PhysicsCollisionLayer.PLAYER_CHARACTER),
                        c -> onCollision(c, damage),
                        c -> {}
                )
        );
    }

    private void onCollision(Collision collision, int damage) {
        if (collision.collisionResponse() != CollisionResponse.Block) {
            return;
        }

        EntityType type = collision.entity().getType();
        if (type == EntityType.TREE) {
            this.lifetimeComponent.kill();
            return;
        }

        if (type == EntityType.AVATAR) {
            Avatar avatar = (Avatar) collision.entity();
            avatar.getLifeComponent().takeDamage(damage);
            this.lifetimeComponent.kill();
        }
    }

    @Override
    public EntityType getType() {
        // We reuse SHOT to avoid touching other systems.
        return EntityType.SHOT;
    }
}
