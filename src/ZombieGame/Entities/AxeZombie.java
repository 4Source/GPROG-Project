package ZombieGame.Entities;

import ZombieGame.CharacterAction;
import ZombieGame.CharacterAnimationKey;
import ZombieGame.CharacterDirection;
import ZombieGame.CharacterEquipment;
import ZombieGame.CharacterPart;
import ZombieGame.EntityType;
import ZombieGame.Components.AxeAttackComponent;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.PunchAttackComponent;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Sprites.LoopingSprite;
import ZombieGame.Sprites.OneShotSprite;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.CollisionResponse;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Systems.Physic.RectangleHitBox;

public class AxeZombie extends Zombie {
	private boolean hasAxe;
	private final DynamicPhysicsComponent secondaryAttackPhysicsComponent;

	public AxeZombie(WorldPos start) {
		super(start,
				new CircleHitBox(HitBoxType.Block, 14, new Offset(0, 20)),
				new RectangleHitBox(HitBoxType.Block, 22, 35),
				new CircleHitBox(HitBoxType.Overlap, 18, new Offset(0, 20)),
				62,
				12,
				CharacterEquipment.AXE,
				e -> new PunchAttackComponent((Zombie) e, 1, 35, new Offset(0, 20), 0.9, 0.7, 0.35, t -> ((AxeZombie) e).onPunchAttackStart(t), t -> ((AxeZombie) e).onPunchHit(t), t -> ((AxeZombie) e).onPunchAttackEnd(t)),
				e -> new AxeAttackComponent((AxeZombie) e, 90, 300, new Offset(0, 20), 2.0, 0.9, 0.4, t -> ((AxeZombie) e).onAxeAttackStart(t), t -> ((AxeZombie) e).onAxeHit(t), t -> ((AxeZombie) e).onAxeAttackEnd(t)));

		final double animationFrameTime = 0.1;
		final String base = "assets/PostApocalypse_AssetPack/Enemies/Zombie_Axe/Zombie_Axe";
		final String baseNoAxe = "assets/PostApocalypse_AssetPack/Enemies/Zombie_Axe/No-Axe/Zombie_Axe_No-axe";
		final double scale = 3;
		this.hasAxe = true;

		this.secondaryAttackPhysicsComponent = this.add(new DynamicPhysicsComponent(this, new CircleHitBox(HitBoxType.Overlap, 300, new Offset(0, 20)), PhysicsCollisionLayer.SENSOR, new PhysicsCollisionMask(PhysicsCollisionLayer.BODY)));

		// IDLE WITH AXE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.AXE, null),
				new LoopingSprite(base + "_Down_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.AXE, null),
				new LoopingSprite(base + "_Side_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.AXE, null),
				new LoopingSprite(base + "_Side-left_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.AXE, null),
				new LoopingSprite(base + "_Up_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));

		// IDLE WITHOUT AXE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null, null),
				new LoopingSprite(baseNoAxe + "_Down_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, null, null),
				new LoopingSprite(baseNoAxe + "_Side_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, null, null),
				new LoopingSprite(baseNoAxe + "_Side-left_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, null, null),
				new LoopingSprite(baseNoAxe + "_Up_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));

		// MOVE WITH AXE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.AXE, null),
				new LoopingSprite(base + "_Down_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.AXE, null),
				new LoopingSprite(base + "_Side_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.AXE, null),
				new LoopingSprite(base + "_Side-left_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.AXE, null),
				new LoopingSprite(base + "_Up_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));

		// MOVE WITHOUT AXE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, null, null),
				new LoopingSprite(baseNoAxe + "_Down_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, null, null),
				new LoopingSprite(baseNoAxe + "_Side_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, null, null),
				new LoopingSprite(baseNoAxe + "_Side-left_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, null, null),
				new LoopingSprite(baseNoAxe + "_Up_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));

		// PRIMARY ATTACK WITH AXE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, CharacterEquipment.AXE, null),
				new OneShotSprite(base + "_Down_First-Attack-Sheet7.png", 7, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, CharacterEquipment.AXE, null),
				new OneShotSprite(base + "_Side_First-Attack-Sheet7.png", 7, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, CharacterEquipment.AXE, null),
				new OneShotSprite(base + "_Side-left_First-Attack-Sheet7.png", 7, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, CharacterEquipment.AXE, null),
				new OneShotSprite(base + "_Up_First-Attack-Sheet7.png", 7, 1, scale, animationFrameTime, () -> onAttackEnd()));

		// PRIMARY ATTACK WITHOUT AXE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, null, null),
				new OneShotSprite(baseNoAxe + "_Down_First-Attack-Sheet7.png", 7, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, null, null),
				new OneShotSprite(baseNoAxe + "_Side_First-Attack-Sheet7.png", 7, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, null, null),
				new OneShotSprite(baseNoAxe + "_Side-left_First-Attack-Sheet7.png", 7, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, null, null),
				new OneShotSprite(baseNoAxe + "_Up_First-Attack-Sheet7.png", 7, 1, scale, animationFrameTime, () -> onAttackEnd()));

		// SPECIAL ATTACK WITH AXE
		this.getVisualComponent().addSprite(CharacterPart.BODY, 
				new CharacterAnimationKey(CharacterAction.SPECIAL_ATTACK, CharacterDirection.DOWN, null, null),
				new OneShotSprite(base + "_Down_Second-Attack-Sheet9.png", 9, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.SPECIAL_ATTACK, CharacterDirection.RIGHT, null, null),
				new OneShotSprite(base + "_Side_Second-Attack-Sheet9.png", 9, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.SPECIAL_ATTACK, CharacterDirection.LEFT, null, null),
				new OneShotSprite(base + "_Side-left_Second-Attack-Sheet9.png", 9, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.SPECIAL_ATTACK, CharacterDirection.UP, null, null),
				new OneShotSprite(base + "_Up_Second-Attack-Sheet9.png", 9, 1, scale, animationFrameTime, () -> onAttackEnd()));
	}

	@Override
	public AxeAttackComponent getSecondaryAttackComponent() {
		return (AxeAttackComponent) super.getSecondaryAttackComponent();
	}

	public DynamicPhysicsComponent getSecondaryAttackPhysicsComponent() {
		return this.secondaryAttackPhysicsComponent;
	}

	public boolean hasAxe() {
		return this.hasAxe;
	}

	public void useAxe() {
		this.hasAxe = false;
		this.getVisualComponent().changeState((CharacterEquipment) null);
	}

	public boolean pickupAxe() {
		if (!this.hasAxe) {
			this.hasAxe = true;
			this.getVisualComponent().changeState(CharacterEquipment.AXE);
			return true;
		}
		return false;
	}

	protected void onSecondaryAttackCollisionStart(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Overlap) {
			EntityType entityType = collision.entity().getType();

			// if entity is avatar, start attack
			if (entityType == EntityType.AVATAR) {
				this.getPrimaryAttackComponent().setTarget(collision.entity());
			}
		}
	}

	protected void onSecondaryAttackCollisionStay(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Overlap) {
			EntityType entityType = collision.entity().getType();

			// if entity is avatar, start attack
			if (entityType == EntityType.AVATAR) {
				this.getPrimaryAttackComponent().setTarget(collision.entity());
			}
		}
	}

	protected void onSecondaryAttackCollisionEnd(Collision collision) {
	}

	public void onPunchAttackStart(Entity target) {
	}

	public void onPunchHit(Entity target) {

	}

	public void onPunchAttackEnd(Entity target) {
	}

	public void onAxeAttackStart(Entity target) {
	}

	public void onAxeHit(Entity target) {
	}

	public void onAxeAttackEnd(Entity target) {
	}
}
