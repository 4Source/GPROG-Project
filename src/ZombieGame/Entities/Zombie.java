package ZombieGame.Entities;

import ZombieGame.AIState;
import ZombieGame.CharacterAction;
import ZombieGame.CharacterAnimationKey;
import ZombieGame.CharacterDirection;
import ZombieGame.CharacterPart;
import ZombieGame.EntityType;
import ZombieGame.Components.AIMovementComponent;
import ZombieGame.Components.CharacterSpriteComponent;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Sprites.LoopingSprite;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.CollisionResponse;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Systems.Physic.RectangleHitBox;

public class Zombie extends Character {
	/**
	 * Spawns a zombie
	 * 
	 * @param startX The position in x of the zombie where is should be at game start
	 * @param startY The position in y of the zombie where is should be at game start
	 */
	public Zombie(WorldPos start) {
		super(e -> new CharacterSpriteComponent(e, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null)), new CircleHitBox(HitBoxType.Block, 18, new Offset(0, 25)), e -> new DynamicPhysicsComponent(e, new RectangleHitBox(HitBoxType.Block, 30, 40), PhysicsCollisionLayer.ZOMBIE_CHARACTER, new PhysicsCollisionMask(PhysicsCollisionLayer.PROJECTILE)), e -> new AIMovementComponent(e, start, 0, 60), e -> new LifeComponent(e, 100));

		double animationFrameTime = 0.1;
		double scale = 3;

		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\Zombie_Big_Down_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\Zombie_Big_Side_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\Zombie_Big_Side-left_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\Zombie_Big_Up_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));

		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\Zombie_Big_Down_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\Zombie_Big_Side_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\Zombie_Big_Side-left_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\Zombie_Big_Up_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
	}

	@Override
	public CharacterSpriteComponent getVisualComponent() {
		return (CharacterSpriteComponent) super.getVisualComponent();
	}

	@Override
	public AIMovementComponent getPositionComponent() {
		return (AIMovementComponent) super.getPositionComponent();
	}

	@Override
	public EntityType getType() {
		return EntityType.ZOMBIE;
	}

	@Override
	protected void onMovementCollisionStart(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Block) {
			EntityType type = collision.entity().getType();

			// if object is avatar, game over
			if (type == EntityType.AVATAR) {
				this.getPositionComponent().moveBack();
				// TODO: Start attack here. By starting attack animation and adding temporary DynamicPhysics component which represent the hitbox where the zombie must hit the player
				((Avatar) collision.entity()).getLifeComponent().takeDamage(10);
			}

			// if object is zombie, step back
			if (type == EntityType.ZOMBIE) {
				this.getPositionComponent().moveBack();
				this.getPositionComponent().setState(AIState.STUCK);
				;
				return;
			}

			// if Object is a tree, move back one step
			if (type == EntityType.TREE) {
				this.getPositionComponent().moveBack();
				this.getPositionComponent().setState(AIState.STUCK);
				;
				return;
			}
		}

	}

	@Override
	protected void onMovementCollisionEnd(Collision collision) {
	}
}
