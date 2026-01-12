package ZombieGame.Entities;

import ZombieGame.CharacterAction;
import ZombieGame.CharacterAnimationKey;
import ZombieGame.CharacterDirection;
import ZombieGame.CharacterEquipment;
import ZombieGame.CharacterPart;
import ZombieGame.CircleHitBox;
import ZombieGame.Collision;
import ZombieGame.CollisionResponse;
import ZombieGame.Components.*;
import ZombieGame.EntityType;
import ZombieGame.HitBoxType;
import ZombieGame.PhysicsCollisionLayer;
import ZombieGame.PhysicsCollisionMask;
import ZombieGame.RectangleHitBox;
import ZombieGame.Game;
import ZombieGame.Components.CharacterSpriteComponent;
import ZombieGame.Components.DynamicPhysicsComponent;
import ZombieGame.Components.GunshotComponent;
import ZombieGame.Components.LifeComponent;
import ZombieGame.Components.PlayerMovementComponent;
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

public class Avatar extends Character {
	private PlayerWeaponComponent weaponComponent;
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

		// Bat
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));

		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, new Offset(0, (int) (7 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, new Offset((int) (4 * scale), (int) (3 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, new Offset((int) (-4 * scale), (int) (3 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, new Offset(0, (int) (-4 * scale))));
		// Pistol
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Gun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));

		// Shotgun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));


		// MOVE
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_down_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_side-left_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, null), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Character_up_run_no-hands-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_down_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_side-left_run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.HANDS), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Run\\Hands_up_run-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Bat
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));

		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, new Offset(0, (int) (7 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, new Offset((int) (4 * scale), (int) (3 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, new Offset((int) (-4 * scale), (int) (3 * scale))));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, new Offset(0, (int) (-4 * scale))));
		// Pistol
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime));

		// Gun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));

		// Shotgun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_down_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_up_idle-and-run-Sheet6.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));


		// DEATH
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.DEATH, null, null), new OneShotSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Death\\Character_side_death1-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.DEATH, CharacterDirection.RIGHT, null), new OneShotSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Death\\Character_side_death1-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY, new CharacterAnimationKey(CharacterAction.DEATH, CharacterDirection.LEFT, null), new OneShotSprite("assets\\PostApocalypse_AssetPack\\Character\\Main\\Death\\Character_side-left_death1-Sheet6.png", 6, 1, scale, animationFrameTime));


		// Attack
		// Bat
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_down_attack-Sheet4.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side_attack-Sheet4.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_side-left_attack-Sheet4.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, CharacterEquipment.BAT), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Bat\\Bat_up_attack-Sheet4.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));

		// Pistol
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_down_shoot-Sheet3.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side_shoot-Sheet3.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_side-left_shoot-Sheet3.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, CharacterEquipment.PISTOL), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Pistol\\Pistol_up_shoot-Sheet3.png", 6, 1, scale, animationFrameTime));

		// Gun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_shoot-Sheet3.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side_shoot-Sheet3.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_side-left_shoot-Sheet3.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, CharacterEquipment.GUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Gun\\Gun_up_shoot-Sheet3.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));

		// Shotgun
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_down_shoot-Sheet3.png", 6, 1, scale, animationFrameTime, 0, (int) (7 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side_shoot-Sheet3.png", 6, 1, scale, animationFrameTime, (int) (4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_side-left_shoot-Sheet3.png", 6, 1, scale, animationFrameTime, (int) (-4 * scale), (int) (3 * scale)));
		this.getVisualComponent().addSprite(CharacterPart.HANDS, new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, CharacterEquipment.SHOTGUN), new LoopingSprite("assets\\PostApocalypse_AssetPack\\Character\\Guns\\Shotgun\\Shotgun_up_shoot-Sheet3.png", 6, 1, scale, animationFrameTime, 0, (int) (-4 * scale)));


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
				((Item) collision.entity()).pickUp(this);
			}
		}
	}

	@Override
	protected void onMovementCollisionStay(Collision collision) {
		// if Object is a tree, move back one step
		if (collision.collisionResponse() == CollisionResponse.Block) {
			this.getPositionComponent().resolveCollision(collision.entity());
		}
	}

	@Override
	protected void onMovementCollisionEnd(Collision collision) {
	}
}
