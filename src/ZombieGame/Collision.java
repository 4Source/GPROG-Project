package ZombieGame;

import ZombieGame.Entities.Entity;

public record Collision(Entity entity, CollisionResponse collisionResponse) {
}