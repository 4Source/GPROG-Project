package ZombieGame.Entities;

import java.util.Optional;

import ZombieGame.AIState;
import ZombieGame.CharacterAction;
import ZombieGame.CharacterAnimationKey;
import ZombieGame.CharacterDirection;
import ZombieGame.CharacterPart;
import ZombieGame.EntityType;
import ZombieGame.Game;
import ZombieGame.ZombieType;
import ZombieGame.Components.AIMovementComponent;
import ZombieGame.Components.AxeThrowAIComponent;
import ZombieGame.Components.CharacterSpriteComponent;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Components.ZombieAttackComponent;
import ZombieGame.Sprites.LoopingSprite;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.CollisionResponse;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Systems.Physic.RectangleHitBox;
import ZombieGame.Sprites.OneShotSprite;
import ZombieGame.Systems.Physic.PhysicsSystem;

public class Zombie extends Character {
	private final ZombieType type;
	private final ZombieAttackComponent attackComponent;
	private boolean hasAxe = false;

	/**
	 * Spawns a zombie with a specific type (BIG / SMALL / AXE).
	 */
	public Zombie(WorldPos start, ZombieType type) {
		super(e -> new CharacterSpriteComponent(e, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null)),
				bodyCircleHitBoxFor(type),
				e -> new DynamicPhysicsComponent(e, physicsRectHitBoxFor(type), PhysicsCollisionLayer.ZOMBIE_CHARACTER, new PhysicsCollisionMask(PhysicsCollisionLayer.PROJECTILE)),
				e -> new AIMovementComponent((Zombie) e, start, 0, movementSpeedFor(type)),
				e -> new LifeComponent(e, maxHealthFor(type), () -> {
					// Increase zombie counter
					Optional<ZombieKillCounter> optZ = Game.world.getUIElement(ZombieKillCounter.class);
					if (optZ.isEmpty()) {
						System.err.println("Could not find ZombieKillCounter");
					} else {
						ZombieKillCounter counter = optZ.get();
						counter.increment();
					}
				}));
		this.type = type;

		// Attack params depend on the type
		int contactDamage = contactDamageFor(type);
		long cooldownMs = attackCooldownFor(type);
		double range = attackRangeFor(type);
		double hitTime = attackHitTimeFor(type);

		if (type == ZombieType.AXE) {
			this.hasAxe = true;
			// AXE zombies have two ranges:
			// - melee range (after throwing, or if the player is too close)
			// - throw range (only while they still have the axe)
			final double meleeRange = 65;
			final double minThrowDistance = 90;
			final double maxThrowDistance = 240;

			// IMPORTANT: The ZombieAttackComponent only calls the onHit handler if the
			// target is within its hitRange at hit-time. For throws we therefore set
			// hitRange to maxThrowDistance and decide inside the handler whether we
			// throw or do melee.
			range = maxThrowDistance;

			// For AXE zombies the attack "hit" spawns a thrown axe once.
			this.attackComponent = this.add(new ZombieAttackComponent(
					this,
					contactDamage,
					cooldownMs,
					range,
					hitTime,
					(z, target) -> {
						double dist = PhysicsSystem.distance(z.getPositionComponent().getWorldPos(), target.getPositionComponent().getWorldPos());

						// If we still have the axe: throw it only in mid-range.
						if (z.hasAxe) {
							if (dist >= minThrowDistance && dist <= maxThrowDistance) {
								Game.world.spawnEntity(new AxeProjectile(
										this,
										z.getPositionComponent().getWorldPos(),
										target.getPositionComponent().getWorldPos()));
								z.hasAxe = false;
								z.replaceAxeSpritesNoAxe();
								return;
							}
							// Too close to throw → melee strike with axe.
							if (dist <= meleeRange) {
								target.getLifeComponent().takeDamage(contactDamage);
							}
							return;
						}

						// After throwing: only weak melee, and only if actually close.
						if (dist <= meleeRange) {
							target.getLifeComponent().takeDamage(1);
						}
					}));
			// Start ranged throws when the player is in mid range.
			this.add(new AxeThrowAIComponent(this, minThrowDistance, maxThrowDistance));
		} else {
			this.attackComponent = this.add(new ZombieAttackComponent(this, contactDamage, cooldownMs, range, hitTime));
		}

		addSpritesForType(type);
	}

	private static int maxHealthFor(ZombieType type) {
		switch (type) {
			case SMALL:
				return 10;
			case AXE:
				return 12;
			case BIG:
			default:
				return 15;
		}
	}

	private static int movementSpeedFor(ZombieType type) {
		switch (type) {
			case SMALL:
				return 92;
			case AXE:
				return 62;
			case BIG:
			default:
				return 50;
		}
	}

	private static int contactDamageFor(ZombieType type) {
		switch (type) {
			case SMALL:
				return 1;
			case AXE:
				return 1;
			case BIG:
			default:
				return 2;
		}
	}

	private static long attackCooldownFor(ZombieType type) {
		switch (type) {
			case SMALL:
				return 950;
			case AXE:
				return 900;
			case BIG:
			default:
				return 650;
		}
	}

	// TODO: Maybe make axes drop at some point an axe zombies will try to get to nearby axes if they do not have any
	public boolean isAxeZombie() {
		return this.type == ZombieType.AXE;
	}

	public boolean hasAxe() {
		return this.hasAxe;
	}

	public boolean isAttacking() {
		return this.attackComponent.isAttacking();
	}

	public void tryStartAttack(Avatar avatar) {
		this.attackComponent.tryStartAttack(avatar);
	}

	private static double attackRangeFor(ZombieType type) {
		switch (type) {
			case SMALL:
				return 55;
			case AXE:
				return 75;
			case BIG:
			default:
				return 65;
		}
	}

	private static double attackHitTimeFor(ZombieType type) {
		// Apply damage roughly mid animation
		switch (type) {
			case SMALL:
				return 0.20;
			case AXE:
				return 0.30;
			case BIG:
			default:
				return 0.35;
		}
	}

	private static CircleHitBox bodyCircleHitBoxFor(ZombieType type) {
		switch (type) {
			case SMALL:
				return new CircleHitBox(HitBoxType.Block, 10, new Offset(0, 15));
			case AXE:
				return new CircleHitBox(HitBoxType.Block, 14, new Offset(0, 20));
			case BIG:
			default:
				return new CircleHitBox(HitBoxType.Block, 18, new Offset(0, 25));
		}
	}

	private static RectangleHitBox physicsRectHitBoxFor(ZombieType type) {
		switch (type) {
			case SMALL:
				return new RectangleHitBox(HitBoxType.Block, 22, 28);
			case AXE:
				return new RectangleHitBox(HitBoxType.Block, 22, 35);
			case BIG:
			default:
				return new RectangleHitBox(HitBoxType.Block, 30, 40);
		}
	}

	private void addSpritesForType(ZombieType type) {
		final double animationFrameTime = 0.1;

		final String base;
		final String prefix;
		final int moveFrames;
		final int attackFrames;
		final double scale;

		switch (type) {
			case SMALL:
				base = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Small\\";
				prefix = "Zombie_Small";
				moveFrames = 6;
				attackFrames = 4;
				scale = 2.6;
				break;
			case AXE:
				base = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Axe\\";
				prefix = "Zombie_Axe";
				moveFrames = 8;
				attackFrames = 7;
				scale = 3.0;
				break;
			case BIG:
			default:
				base = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\";
				prefix = "Zombie_Big";
				moveFrames = 8;
				attackFrames = 8;
				scale = 3.0;
				break;
		}

		// IDLE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null),
				new LoopingSprite(base + prefix + "_Down_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, null),
				new LoopingSprite(base + prefix + "_Side_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, null),
				new LoopingSprite(base + prefix + "_Side-left_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, null),
				new LoopingSprite(base + prefix + "_Up_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));

		// MOVE
		String downWalkFile;
		if (type == ZombieType.SMALL) {
			// The asset pack uses a lowercase 'walk' for this one file.
			downWalkFile = base + prefix + "_Down_walk-Sheet6.png";
		} else {
			downWalkFile = base + prefix + "_Down_Walk-Sheet" + moveFrames + ".png";
		}

		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, null),
				new LoopingSprite(downWalkFile, moveFrames, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, null),
				new LoopingSprite(base + prefix + "_Side_Walk-Sheet" + moveFrames + ".png", moveFrames, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, null),
				new LoopingSprite(base + prefix + "_Side-left_Walk-Sheet" + moveFrames + ".png", moveFrames, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, null),
				new LoopingSprite(base + prefix + "_Up_Walk-Sheet" + moveFrames + ".png", moveFrames, 1, scale, animationFrameTime));

		// ATTACK (One-shot) - "First Attack" sheets
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, null),
				new OneShotSprite(base + prefix + "_Down_First-Attack-Sheet" + attackFrames + ".png", attackFrames, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, null),
				new OneShotSprite(base + prefix + "_Side_First-Attack-Sheet" + attackFrames + ".png", attackFrames, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, null),
				new OneShotSprite(base + prefix + "_Side-left_First-Attack-Sheet" + attackFrames + ".png", attackFrames, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, null),
				new OneShotSprite(base + prefix + "_Up_First-Attack-Sheet" + attackFrames + ".png", attackFrames, 1, scale, animationFrameTime, () -> onAttackEnd()));
	}

	/**
	 * Replace the AXE zombie sprites with the "No-Axe" variants once it has thrown its axe.
	 */
	private void replaceAxeSpritesNoAxe() {
		if (this.type != ZombieType.AXE) {
			return;
		}

		final double animationFrameTime = 0.1;
		final double scale = 3.0;

		final String base = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Axe\\No-Axe\\";
		final String prefix = "Zombie_Axe_No-axe";
		final int moveFrames = 8;
		final int attackFrames = 7;

		// IDLE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null),
				new LoopingSprite(base + prefix + "_Down_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, null),
				new LoopingSprite(base + prefix + "_Side_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, null),
				new LoopingSprite(base + prefix + "_Side-left_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, null),
				new LoopingSprite(base + prefix + "_Up_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));

		// MOVE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, null),
				new LoopingSprite(base + prefix + "_Down_Walk-Sheet" + moveFrames + ".png", moveFrames, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, null),
				new LoopingSprite(base + prefix + "_Side_Walk-Sheet" + moveFrames + ".png", moveFrames, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, null),
				new LoopingSprite(base + prefix + "_Side-left_Walk-Sheet" + moveFrames + ".png", moveFrames, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, null),
				new LoopingSprite(base + prefix + "_Up_Walk-Sheet" + moveFrames + ".png", moveFrames, 1, scale, animationFrameTime));

		// ATTACK
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, null),
				new OneShotSprite(base + prefix + "_Down_First-Attack-Sheet" + attackFrames + ".png", attackFrames, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, null),
				new OneShotSprite(base + prefix + "_Side_First-Attack-Sheet" + attackFrames + ".png", attackFrames, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, null),
				new OneShotSprite(base + prefix + "_Side-left_First-Attack-Sheet" + attackFrames + ".png", attackFrames, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, null),
				new OneShotSprite(base + prefix + "_Up_First-Attack-Sheet" + attackFrames + ".png", attackFrames, 1, scale, animationFrameTime, () -> onAttackEnd()));
	}

	@Override
	public CharacterSpriteComponent getVisualComponent() {
		return (CharacterSpriteComponent) super.getVisualComponent();
	}

	@Override
	public AIMovementComponent getPositionComponent() {
		return (AIMovementComponent) super.getPositionComponent();
	}

	public ZombieAttackComponent getAttackComponent() {
		return this.attackComponent;
	}

	@Override
	public EntityType getType() {
		return EntityType.ZOMBIE;
	}

	@Override
	protected void onMovementCollisionStart(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Block) {
			EntityType entityType = collision.entity().getType();

			// if object is avatar, start attack
			if (entityType == EntityType.AVATAR) {
				this.attackComponent.tryStartAttack((Avatar) collision.entity());
			}

			// if object is zombie, step back
			if (entityType == EntityType.ZOMBIE) {
				this.getPositionComponent().resolveCollision(collision.entity());
				this.getPositionComponent().setState(AIState.STUCK);
				return;
			}

			// if Object is a tree, move back one step
			if (entityType == EntityType.TREE) {
				this.getPositionComponent().resolveCollision(collision.entity());
				this.getPositionComponent().setState(AIState.STUCK);
				return;
			}
		}
	}

	@Override
	protected void onMovementCollisionStay(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Block) {
			EntityType entityType = collision.entity().getType();
			if (entityType == EntityType.AVATAR) {
				this.attackComponent.tryStartAttack((Avatar) collision.entity());
			}
		}
	}

	@Override
	protected void onMovementCollisionEnd(Collision collision) {
	}

	protected void onAttackEnd() {
		this.getAttackComponent().stopAttack();
		this.getVisualComponent().changeState(CharacterAction.IDLE);
		this.getPositionComponent().setState(AIState.HUNTING);
	}
}
