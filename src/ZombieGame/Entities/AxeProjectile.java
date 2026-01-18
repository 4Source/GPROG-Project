package ZombieGame.Entities;

import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.EntityType;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Components.AnimatedSpriteComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Sprites.LoopingSprite;

/**
 * Projectile thrown by AXE zombies.
 * Uses the asset pack's thrown-axe sprite so it's clearly visible.
 */
public class AxeProjectile extends Projectile {

    /**
     * @param owner The entity which spawned this Projectile, the projectile will not collide with owner.
     * @param pos The position in the world
     * @param alpha The angle of rotation in radian
     * @param speed The speed how fast to move
     * @param lifetime The duration the component live before being destroyed
     * @param damage The damage it makes in half-hearts (1 = 1/2 Heart, 2 = 1 Heart)
     */
    public AxeProjectile(Entity owner, WorldPos pos, double alpha, double speed, double lifetime, int damage) {
        super(owner, pos, alpha, speed, lifetime, damage, new CircleHitBox(HitBoxType.Block, 10), new PhysicsCollisionMask(PhysicsCollisionLayer.OBSTACLES, PhysicsCollisionLayer.PLAYER_CHARACTER), e -> {
            // Pick a fitting thrown-axe animation depending on the flight direction.
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

            return new AnimatedSpriteComponent(e, new LoopingSprite(spritePath, 9, 1, 3.0, 0.09));
        });
    }

    /**
     * @param owner The entity which spawned this Projectile, the projectile will not collide with owner.
     * @param pos The position in the world
     * @param dest The target direction of the axe
     * @param speed The speed how fast to move
     * @param lifetime The duration the component live before being destroyed
     * @param damage The damage it makes in half-hearts (1 = 1/2 Heart, 2 = 1 Heart)
     */
    public AxeProjectile(Entity owner, WorldPos pos, WorldPos dest, double speed, double lifetime, int damage) {
        this(owner, pos, Math.atan2(dest.y() - pos.y(), dest.x() - pos.x()), speed, lifetime, damage);
    }

    @Override
    public EntityType getType() {
        // We reuse SHOT to avoid touching other systems.
        return EntityType.SHOT;
    }
}
