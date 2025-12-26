package ZombieGame.Entities;

import ZombieGame.CharacterAction;
import ZombieGame.CharacterAnimationKey;
import ZombieGame.CharacterDirection;
import ZombieGame.CharacterEquipment;
import ZombieGame.CharacterPart;
import ZombieGame.CircleHitBox;
import ZombieGame.Collision;
import ZombieGame.CollisionResponse;
import ZombieGame.EntityType;
import ZombieGame.HitBoxType;
import ZombieGame.ZombieWorld;
import ZombieGame.Components.CharacterSpriteComponent;
import ZombieGame.Components.GrenadeComponent;
import ZombieGame.Components.GunshotComponent;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Components.PlayerMovementComponent;
import ZombieGame.Sprites.LoopingSprite;
import ZombieGame.Sprites.OneShotSprite;

public class Avatar extends Character {
	private GunshotComponent gunshotComponent;
	private GrenadeComponent grenadeComponent;

	/**
	 * Spawns an avatar
	 * 
	 * @param startX The position in x of the avatar where is should be at game start
	 * @param startY The position in y of the avatar where is should be at game start
	 */
	public Avatar(double startX, double startY) {
		super(startX, startY, e -> new CharacterSpriteComponent(e, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.GUN)), new CircleHitBox(HitBoxType.Block, 12, 0, 20), e -> new PlayerMovementComponent(e, 200), e -> new LifeComponent(e, 100) {
			@Override
			public void kill() {
				((Avatar) this.getEntity()).getVisualComponent().changeState(CharacterAction.DEATH);

				Entity.world.gameOver = true;
			}
		});
		this.gunshotComponent = this.add(new GunshotComponent(this, 0.2));
		this.grenadeComponent = this.add(new GrenadeComponent(this));

		double animationFrameTime = 0.1;
		double scale = 3;

		// IDLE
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_down_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_side_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_side-left_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_up_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_down_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_side_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_side-left_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_up_idle-Sheet6.png", 6, 1, scale, animationFrameTime));

		// this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		// this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		// this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		// this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));

		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));

		// MOVE
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_down_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side-left_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_up_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_down_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side-left_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_up_run-Sheet6.png", 6, 1, scale, animationFrameTime));

		// this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		// this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		// this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		// this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));

		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));

		// DEATH
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.DEATH, null, null), new OneShotSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Death\\Character_side_death1-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.DEATH, CharacterDirection.RIGHT, null), new OneShotSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Death\\Character_side_death1-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.DEATH, CharacterDirection.LEFT, null), new OneShotSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Death\\Character_side-left_death1-Sheet6.png", 6, 1, scale, animationFrameTime));
	}

	@Override
	public CharacterSpriteComponent getVisualComponent() {
		return (CharacterSpriteComponent) super.getVisualComponent();
	}

	@Override
	public PlayerMovementComponent getMovementComponent() {
		return (PlayerMovementComponent) super.getMovementComponent();
	}

	public GunshotComponent getGunshotComponent() {
		return this.gunshotComponent;
	}

	public GrenadeComponent getGrenadeComponent() {
		return this.grenadeComponent;
	}

	@Override
	public EntityType getType() {
		return EntityType.AVATAR;
	}

	@Override
	protected void onCollisionStart(Collision collision) {
		// if Object is a tree, move back one step
		if (collision.collisionResponse() == CollisionResponse.Block) {
			this.getMovementComponent().moveBack();
		}

		// pick up Grenades
		else if (collision.collisionResponse() == CollisionResponse.Overlap) {
			if (collision.entity().getType() == EntityType.GRENADE_ITEM) {
				((ZombieWorld) Entity.world).addGrenade();
				((Grenade) collision.entity()).getLifetimeComponent().kill();
			}
		}
	}

	@Override
	protected void onCollisionEnd(Collision collision) {
	}
}
