package ZombieGame.Systems.Physic;

import ZombieGame.Entities.Entity;

public record Collision(Entity entity, CollisionResponse collisionResponse) {
}