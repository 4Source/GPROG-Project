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

	/**
	 * @param visualFactory A Factory method to create the component
	 */
	public Zombie(WorldPos start, HitBox movementHitBox, HitBox damageHitBox, double movementSpeed, int maxHealth, CharacterEquipment equipment, Function<Entity, AttackComponent> primaryAttackFactory, Function<Entity, AttackComponent> secondaryAttackFactory) {
		super(
				e -> new CharacterSpriteComponent(e, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, equipment, null)),
				movementHitBox,
				e -> new DynamicPhysicsComponent(e, damageHitBox, PhysicsCollisionLayer.ZOMBIE_CHARACTER, new PhysicsCollisionMask(PhysicsCollisionLayer.PROJECTILE)),
				e -> new AIMovementComponent((Zombie) e, start, 0, movementSpeed),
				e -> new LifeComponent(e, maxHealth, () -> ((Zombie) e).onDie()));

		this.primaryAttackComponent = this.add(primaryAttackFactory.apply(this));
		this.secondaryAttackComponent = this.add(secondaryAttackFactory.apply(this));
	}

	public Zombie(WorldPos start, HitBox movementHitBox, HitBox damageHitBox, double movementSpeed, int maxHealth, CharacterEquipment equipment, Function<Entity, AttackComponent> primaryAttackFactory) {
		this(start, movementHitBox, damageHitBox, movementSpeed, maxHealth, equipment, primaryAttackFactory, e -> null);
	}

	@Override
	public CharacterSpriteComponent getVisualComponent() {
		return (CharacterSpriteComponent) super.getVisualComponent();
	}

	@Override
	public AIMovementComponent getPositionComponent() {
		return (AIMovementComponent) super.getPositionComponent();
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

			// if entity is avatar, start attack
			if (entityType == EntityType.AVATAR) {
				this.getPositionComponent().resolveCollision(collision.entity());
				this.getPositionComponent().setState(AIState.ATTACKING);
			}

			// if entity is zombie, step back
			if (entityType == EntityType.ZOMBIE) {
				this.getPositionComponent().resolveCollision(collision.entity());
				this.getPositionComponent().setState(AIState.STUCK);
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

			// if entity is avatar, start attack
			if (entityType == EntityType.AVATAR) {
				this.getPositionComponent().resolveCollision(collision.entity());
			}

			// if entity is zombie, step back
			if (entityType == EntityType.ZOMBIE) {
				this.getPositionComponent().resolveCollision(collision.entity());
				this.getPositionComponent().setState(AIState.STUCK);
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
	protected void onMovementCollisionEnd(Collision collision) {
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

	protected void onAttackEnd() {
		this.getPrimaryAttackComponent().stopAttack();
		this.getVisualComponent().changeState(CharacterAction.IDLE);
		this.getPositionComponent().setState(AIState.HUNTING);
	}
}
