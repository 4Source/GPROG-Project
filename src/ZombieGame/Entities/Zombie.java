package ZombieGame.Entities;

import java.util.Optional;
import java.util.function.Function;

import ZombieGame.AIState;
import ZombieGame.CharacterAction;
import ZombieGame.CharacterAnimationKey;
import ZombieGame.CharacterDirection;
import ZombieGame.CharacterEquipment;
import ZombieGame.EntityType;
import ZombieGame.Game;
import ZombieGame.Components.AIMovementComponent;
import ZombieGame.Components.CharacterSpriteComponent;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Components.AttackComponent;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.CollisionResponse;
import ZombieGame.Systems.Physic.HitBox;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;

public abstract class Zombie extends Character {
	private final AttackComponent primaryAttackComponent;
	private final AttackComponent secondaryAttackComponent;
	private final DynamicPhysicsComponent primaryAttackPhysicsComponent;

	/**
	 * @param visualFactory A Factory method to create the component
	 */
	public Zombie(WorldPos start, HitBox movementHitBox, HitBox damageHitBox, HitBox primaryAttackHitBox, double movementSpeed, int maxHealth, CharacterEquipment equipment, Function<Entity, AttackComponent> primaryAttackFactory, Function<Entity, AttackComponent> secondaryAttackFactory) {
		super(
				e -> new CharacterSpriteComponent(e, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, equipment, null)),
				movementHitBox,
				e -> new DynamicPhysicsComponent(e, damageHitBox, PhysicsCollisionLayer.HURTBOX, new PhysicsCollisionMask(PhysicsCollisionLayer.PROJECTILE),
						collision -> {
							((Zombie) e).onPrimaryAttackCollisionStart(collision);
						},
						collision -> {
							((Zombie) e).onPrimaryAttackCollisionStay(collision);
						}, collision -> {
							((Zombie) e).onPrimaryAttackCollisionEnd(collision);
						}),
				e -> new AIMovementComponent((Zombie) e, start, 0, movementSpeed),
				e -> new LifeComponent(e, maxHealth, () -> ((Zombie) e).onDie()));

		this.primaryAttackComponent = this.add(primaryAttackFactory.apply(this));
		this.secondaryAttackComponent = this.add(secondaryAttackFactory.apply(this));
		this.primaryAttackPhysicsComponent = this.add(new DynamicPhysicsComponent(this, primaryAttackHitBox, PhysicsCollisionLayer.SENSOR, new PhysicsCollisionMask(PhysicsCollisionLayer.BODY)));
	}

	public Zombie(WorldPos start, HitBox movementHitBox, HitBox damageHitBox, HitBox primaryAttackHitBox, double movementSpeed, int maxHealth, CharacterEquipment equipment, Function<Entity, AttackComponent> primaryAttackFactory) {
		this(start, movementHitBox, damageHitBox, primaryAttackHitBox, movementSpeed, maxHealth, equipment, primaryAttackFactory, e -> null);
	}

	@Override
	public CharacterSpriteComponent getVisualComponent() {
		return (CharacterSpriteComponent) super.getVisualComponent();
	}

	@Override
	public AIMovementComponent getPositionComponent() {
		return (AIMovementComponent) super.getPositionComponent();
	}

	public DynamicPhysicsComponent getPrimaryAttackPhysicsComponent() {
		return this.primaryAttackPhysicsComponent;
	}

	public AttackComponent getPrimaryAttackComponent() {
		return this.primaryAttackComponent;
	}

	public AttackComponent getSecondaryAttackComponent() {
		return this.secondaryAttackComponent;
	}

	@Override
	public EntityType getType() {
		return EntityType.ZOMBIE;
	}

	@Override
	protected void onMovementCollisionStart(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Block) {
			EntityType entityType = collision.entity().getType();



			// Avatar should never overlap the zombie. We resolve OUR movement (rollback) when we walk into the avatar.
			// Attack state is driven by the attack component, not by body collision.
			if (entityType == EntityType.AVATAR) {
				if (this.getPositionComponent().hasMoved()) {
					this.getPositionComponent().resolveCollision(collision.entity());
				}
				return;
			}

			// Zombie vs Zombie: do NOT enter STUCK (it gets re-triggered every frame and causes freezes).
			// Instead, separate the two bodies so they can keep moving and pushing each other.
			if (entityType == EntityType.ZOMBIE) {
				separateFromDynamicBody(collision.entity());
				return;
			}

			// if entity is a tree, move back one step
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


			// Avatar body collision: keep bodies separated, but do not change AI state here.
			if (entityType == EntityType.AVATAR) {
				if (this.getPositionComponent().hasMoved()) {
					this.getPositionComponent().resolveCollision(collision.entity());
				}
				return;
			}

			// Zombie vs Zombie: keep separating (no STUCK loop).
			if (entityType == EntityType.ZOMBIE) {
				separateFromDynamicBody(collision.entity());
				return;
			}

			// Tree collision: do NOT repeatedly set STUCK on STAY (that prevents clearing).
			if (entityType == EntityType.TREE) {
				this.getPositionComponent().resolveCollision(collision.entity());
				return;
			}
		}

	}

	@Override
	protected void onMovementCollisionEnd(Collision collision) {
	}

	protected void onPrimaryAttackCollisionStart(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Overlap) {
			EntityType entityType = collision.entity().getType();

			// if entity is avatar, start attack
			if (entityType == EntityType.AVATAR) {
				this.getPrimaryAttackComponent().setTarget(collision.entity());
			}
		}
	}

	protected void onPrimaryAttackCollisionStay(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Overlap) {
			EntityType entityType = collision.entity().getType();

			// if entity is avatar, start attack
			if (entityType == EntityType.AVATAR) {
				this.getPrimaryAttackComponent().setTarget(collision.entity());
			}
		}
	}

	protected void onPrimaryAttackCollisionEnd(Collision collision) {
	}

	protected void onDie() {
		// Increase zombie counter
		Optional<ZombieKillCounter> optZ = Game.world.getUIElement(ZombieKillCounter.class);
		if (optZ.isEmpty()) {
			System.err.println("Could not find ZombieKillCounter");
		} else {
			ZombieKillCounter counter = optZ.get();
			counter.increment();
		}
	}

	/**
	 * Separates this zombie from another dynamic body (typically another zombie) so that both can keep moving.
	 * The separation is bounded to avoid tunneling through trees/walls.
	 */
	private void separateFromDynamicBody(Entity other) {
		if (!(other instanceof Character ch)) {
			// Fallback: just roll back our own movement.
			if (this.getPositionComponent().hasMoved()) {
				this.getPositionComponent().resolveCollision(other);
			}
			return;
		}

		// Process each pair only once to avoid double-separation jitter.
		if (this.hashCode() > other.hashCode()) {
			return;
		}

		WorldPos pA = this.getPositionComponent().getWorldPos();
		WorldPos pB = ch.getPositionComponent().getWorldPos();
		WorldPos d = pB.sub(pA);
		double len = d.length();
		if (len < 1e-6) {
			d = new WorldPos(1, 0);
			len = 1;
		}
		WorldPos dir = d.div(len);

		// Try to separate both bodies a little (bounded). Each push computes the minimal displacement needed.
		WorldPos maxStep = dir.mul(18);
		ch.getPositionComponent().pushOutOf(this, maxStep);
		this.getPositionComponent().pushOutOf(other, maxStep.mul(-1));
	}

	protected void onAttackEnd() {
		this.getPrimaryAttackComponent().stopAttack();
		this.getVisualComponent().changeState(CharacterAction.IDLE);
		this.getPositionComponent().setState(AIState.HUNTING);
	}
}
