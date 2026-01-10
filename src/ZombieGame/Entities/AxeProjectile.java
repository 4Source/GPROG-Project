package ZombieGame.Entities;

import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.CollisionResponse;
import ZombieGame.EntityType;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Components.AnimatedSpriteComponent;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifetimeComponent;
import ZombieGame.Components.PhysicsComponent;
import ZombieGame.Components.StaticMovementComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Sprites.LoopingSprite;

/**
 * Projectile thrown by AXE zombies.
 * Uses the asset pack's thrown-axe sprite so it's clearly visible.
 */
public class AxeProjectile extends Entity {
    private final LifetimeComponent lifetimeComponent;
    private final PhysicsComponent physicsComponent;

    public AxeProjectile(WorldPos pos, WorldPos dest, int damage) {
        super(e -> new StaticMovementComponent(e, pos, Math.atan2(dest.sub(pos).y(), dest.sub(pos).x()), 340));

        // Pick a fitting thrown-axe animation depending on the flight direction.
        WorldPos d = dest.sub(pos);
        double alpha = Math.atan2(d.y(), d.x());
        String spritePath;
        // RIGHT: include ±45°
        if (alpha >= -Math.PI / 4 && alpha <= Math.PI / 4) {
            spritePath = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Axe\\Axe\\Axe_Side_Thrown-Sheet9.png";
        }
        // DOWN: strictly between 45° and 135°
        else if (alpha > Math.PI / 4 && alpha < 3 * Math.PI / 4) {
            spritePath = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Axe\\Axe\\Axe_Vertical_Thrown-Sheet9.png";
        }
        // UP: strictly between -135° and -45°
        else if (alpha > -3 * Math.PI / 4 && alpha < -Math.PI / 4) {
            spritePath = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Axe\\Axe\\Axe_Vertical_Thrown-Sheet9.png";
        }
        // LEFT: include ±135°
        else {
            spritePath = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Axe\\Axe\\Axe_Side-left_Thrown-Sheet9.png";
        }

        // 9 frames in a row, each 14x14px. Scale up so it's readable.
        // Use a looping sprite so the axe keeps spinning for its whole flight time.
        this.add(new AnimatedSpriteComponent(this, new LoopingSprite(spritePath, 9, 1, 3.0, 0.09)));

        // Lifetime so it doesn't fly forever.
        this.lifetimeComponent = this.add(new LifetimeComponent(this, 1.6));

        // Collide with obstacles and the player.
        physicsComponent = this.add(
                new DynamicPhysicsComponent(
                        this,
                        new CircleHitBox(HitBoxType.Block, 10),
                        PhysicsCollisionLayer.PROJECTILE,
                        new PhysicsCollisionMask(PhysicsCollisionLayer.OBSTACLES, PhysicsCollisionLayer.PLAYER_CHARACTER),
                        c -> onCollision(c, damage),
                        c -> {}));
    }

    public DynamicPhysicsComponent getPhysicsComponent() {
        return (DynamicPhysicsComponent) this.physicsComponent;
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
