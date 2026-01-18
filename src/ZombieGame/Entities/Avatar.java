package ZombieGame.Entities;

import ZombieGame.CharacterAction;
import ZombieGame.CharacterAnimationKey;
import ZombieGame.CharacterDirection;
import ZombieGame.CharacterEquipment;
import ZombieGame.CharacterPart;
import ZombieGame.Components.*;
import ZombieGame.Coordinates.Rotation;
import ZombieGame.EntityType;
import ZombieGame.Game;
import ZombieGame.Components.CharacterSpriteComponent;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Components.PlayerMovementComponent;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Sprites.LoopingSprite;
import ZombieGame.Sprites.OneShotSprite;
import ZombieGame.Systems.Input.Action;
import ZombieGame.Systems.Input.InputSystem;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.Collision;
import ZombieGame.Systems.Physic.CollisionResponse;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.PhysicsCollisionLayer;
import ZombieGame.Systems.Physic.PhysicsCollisionMask;
import ZombieGame.Systems.Physic.RectangleHitBox;

public class Avatar extends Character {
	private PlayerWeaponComponent weaponComponent;
	private PlayerDirection playerDirection;
	/**
	 * Spawns an avatar
	 * 
	 * @param start The position in the world of the avatar where is should be at game start
	 */
	public Avatar(WorldPos start) {
		super(e -> new CharacterSpriteComponent(e, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.HANDS)), new CircleHitBox(HitBoxType.Block, 12, new Offset(0, 20)),
				e -> new DynamicPhysicsComponent(e, new RectangleHitBox(HitBoxType.Block, 20, 30),
						PhysicsCollisionLayer.PLAYER_CHARACTER, new PhysicsCollisionMask(PhysicsCollisionLayer.ZOMBIE, PhysicsCollisionLayer.OBSTACLES)),
				e -> new PlayerMovementComponent((Avatar) e, start, 200),
				e -> new LifeComponent(e, 10) {
					@Override
					public void kill() {
						((Avatar) this.getEntity()).getVisualComponent().changeState(CharacterAction.DEATH);

						Game.world.gameOver = true;
					}
				});

        this.weaponComponent = this.add(new PlayerWeaponComponent(this));
		this.playerDirection = this.add(new PlayerDirection(this));

		addSprites(this);

	}


	private void addSprites(Avatar avatar){

		double animationFrameTime = 0.1;
		double scale = 3;

		Offset offsetDown = new Offset(0, (int) (7 * scale));
		Offset offsetRight = new Offset((int) (4 * scale), (int) (3 * scale));
		Offset offsetLeft = new Offset((int) (-4 * scale), (int) (3 * scale));
		Offset offsetUp = new Offset( 0, (int) (-4 * scale));
		Offset offsetDownRight = new Offset((int) (6 * scale), (int) (3 * scale));
		Offset offsetDownLeft = new Offset((int) (-6 * scale), (int) (3 * scale));
		Offset offsetUpRight = new Offset((int) (5 * scale), (int) (4 * scale));
		Offset offsetUpLeft = new Offset((int) (-3 * scale), (int) (-3 * scale));



		Offset rotationCenter = new Offset(0,0);
		Rotation upRight = new Rotation(Math.toRadians(-45));
		Rotation downRight = new Rotation(Math.toRadians(45));
		Rotation upLeft = new Rotation(Math.toRadians(45));
		Rotation downLeft = new Rotation(Math.toRadians(-45));

		// IDLE
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_down_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_side_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_side-left_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_up_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_side_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_side-left_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_side_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Character_side-left_idle_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));

		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_down_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_side_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_side-left_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_up_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_side_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_LEFT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_side-left_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_RIGHT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_side_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_LEFT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Idle\\Hands_side-left_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		// Bat
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Pistol
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetDown));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetUp));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, downRight, offsetDownRight.invert().add(4,0), animationFrameTime, offsetDownRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, downLeft, offsetDownLeft.invert(), animationFrameTime, offsetDownLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, upRight, rotationCenter, animationFrameTime, offsetUpRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, upLeft, rotationCenter, animationFrameTime, offsetUpLeft));


		// Gun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetDown));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetUp));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, downRight, offsetDownRight.invert(), animationFrameTime, offsetDownRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, downLeft, offsetDownLeft.invert(), animationFrameTime, offsetDownLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, upRight, rotationCenter, animationFrameTime, offsetUpRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, upLeft, rotationCenter, animationFrameTime, offsetUpLeft));

		// Shotgun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetDown));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetUp));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_idle-and-run-Sheet6.png", 6, 1, scale, downRight, rotationCenter, animationFrameTime, offsetDownRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN_LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, downLeft, rotationCenter, animationFrameTime, offsetDownLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_idle-and-run-Sheet6.png", 6, 1, scale, upRight, rotationCenter, animationFrameTime, offsetUpRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP_LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, upLeft, rotationCenter, animationFrameTime, offsetUpLeft));


		// MOVE
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_down_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side-left_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_up_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side-left_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side-left_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));

		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_down_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side-left_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_up_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_LEFT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side-left_idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_RIGHT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_LEFT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side-left_idle-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Bat
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Pistol
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetDown));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetUp));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, downRight, rotationCenter, animationFrameTime, offsetDownRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, downLeft, rotationCenter, animationFrameTime, offsetDownLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, upRight, rotationCenter, animationFrameTime, offsetUpRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, upLeft, rotationCenter, animationFrameTime, offsetUpLeft));

		// Gun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetDown));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetUp));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, downRight, rotationCenter, animationFrameTime, offsetDownRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, downLeft, rotationCenter, animationFrameTime, offsetDownLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, upRight, rotationCenter, animationFrameTime, offsetUpRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, upLeft, rotationCenter, animationFrameTime, offsetUpLeft));

		// Shotgun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetDown));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, offsetUp));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_idle-and-run-Sheet6.png", 6, 1, scale, downRight, rotationCenter, animationFrameTime, offsetDownRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN_LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, downLeft, rotationCenter, animationFrameTime, offsetDownLeft));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_idle-and-run-Sheet6.png", 6, 1, scale, upRight, rotationCenter, animationFrameTime, offsetUpRight));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP_LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, upLeft, rotationCenter, animationFrameTime, offsetUpLeft));


		// DEATH
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.DEATH, null, null), new OneShotSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Death\\Character_side_death1-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.DEATH, CharacterDirection.RIGHT, null), new OneShotSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Death\\Character_side_death1-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.DEATH, CharacterDirection.LEFT, null), new OneShotSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Death\\Character_side-left_death1-Sheet6.png", 6, 1, scale, animationFrameTime));











		// Attack
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_down_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side-left_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_up_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side-left_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side-left_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Bat
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_down_attack-Sheet4.png", 6, 1, scale, animationFrameTime,new Offset ( 0, (int) (7 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_attack-Sheet4.png", 6, 1, scale, animationFrameTime,new Offset ( (int) (4 * scale), (int) (3 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_attack-Sheet4.png", 6, 1, scale, animationFrameTime,new Offset ( (int) (-4 * scale), (int) (3 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_up_attack-Sheet4.png", 6, 1, scale, animationFrameTime,new Offset ( 0, (int) (-4 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_down_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side-left_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_up_run-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Pistol
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_down_shoot-Sheet3.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_shoot-Sheet3.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_shoot-Sheet3.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_up_shoot-Sheet3.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_down_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side-left_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_up_run-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Gun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_shoot-Sheet3.png", 6, 1, scale, animationFrameTime,new Offset ( 0, (int) (7 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_shoot-Sheet3.png", 6, 1, scale, animationFrameTime,new Offset ( 0, (int) (7 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_shoot-Sheet3.png", 6, 1, scale, animationFrameTime,new Offset ( 0, (int) (7 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_shoot-Sheet3.png", 6, 1, scale, animationFrameTime,new Offset ( 0, (int) (7 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_down_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side-left_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_up_run-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Shotgun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_down_shoot-Sheet3.png", 6, 1, scale, animationFrameTime,new Offset ( 0, (int) (7 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_shoot-Sheet3.png", 6, 1, scale, animationFrameTime,new Offset ( (int) (4 * scale), (int) (3 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_shoot-Sheet3.png", 6, 1, scale, animationFrameTime,new Offset ( (int) (-4 * scale), (int) (3 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_up_shoot-Sheet3.png", 6, 1, scale, animationFrameTime,new Offset ( 0, (int) (-4 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_down_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN_LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side-left_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP_LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_up_run-Sheet6.png", 6, 1, scale, animationFrameTime));



	}

	@Override
	public CharacterSpriteComponent getVisualComponent() {
		return (CharacterSpriteComponent) super.getVisualComponent();
	}

	@Override
	public PlayerMovementComponent getPositionComponent() {
		return (PlayerMovementComponent) super.getPositionComponent();
	}

	public PlayerWeaponComponent getWeaponComponent() {
		return this.weaponComponent;
	}

	@Override
	public EntityType getType() {
		return EntityType.AVATAR;
	}

	@Override
	protected void onMovementCollisionStart(Collision collision) {
		// if Object is a tree, move back one step
		if (collision.collisionResponse() == CollisionResponse.Block) {
			this.getPositionComponent().resolveCollision(collision.entity());
		}

		// pick up Items
		else if (collision.collisionResponse() == CollisionResponse.Overlap) {
			if (collision.entity().getType() == EntityType.ITEM) {
				if(InputSystem.getInstance().isPressed(Action.INTERACT)){
					((Item) collision.entity()).pickUp(this);
				}
			}
		}
	}

	@Override
	protected void onMovementCollisionStay(Collision collision) {
		// if Object is a tree, move back one step
		if (collision.collisionResponse() == CollisionResponse.Block) {
			this.getPositionComponent().resolveCollision(collision.entity());
		}
		// pick up Items
		else if (collision.collisionResponse() == CollisionResponse.Overlap) {
			if (collision.entity().getType() == EntityType.ITEM) {
				if(InputSystem.getInstance().isPressed(Action.INTERACT)){
					((Item) collision.entity()).pickUp(this);
				}
			}
		}
	}

	@Override
	protected void onMovementCollisionEnd(Collision collision) {
	}
}
