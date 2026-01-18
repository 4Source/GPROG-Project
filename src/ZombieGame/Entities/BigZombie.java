package ZombieGame.Entities;

import ZombieGame.CharacterAction;
import ZombieGame.CharacterAnimationKey;
import ZombieGame.CharacterDirection;
import ZombieGame.CharacterPart;
import ZombieGame.Components.PunchAttackComponent;
import ZombieGame.Coordinates.Offset;
import ZombieGame.Coordinates.WorldPos;
import ZombieGame.Sprites.LoopingSprite;
import ZombieGame.Sprites.OneShotSprite;
import ZombieGame.Systems.Physic.CircleHitBox;
import ZombieGame.Systems.Physic.HitBoxType;
import ZombieGame.Systems.Physic.RectangleHitBox;

public class BigZombie extends Zombie {

	public BigZombie(WorldPos start) {
		super(start,
				new CircleHitBox(HitBoxType.Block, 18, new Offset(0, 25)),
				new RectangleHitBox(HitBoxType.Block, 30, 40),
				50,
				15,
				null,
				e -> new PunchAttackComponent((Zombie) e, 2, 40, new Offset(0, 15), 0.65, 0.8, 0.4, t -> ((BigZombie) e).onAttackStart(t), t -> ((BigZombie) e).onHit(t), t -> ((BigZombie) e).onAttackEnd(t)));

		final double animationFrameTime = 0.1;
		final String base = "assets\\PostApocalypse_AssetPack\\Enemies\\Zombie_Big\\Zombie_Big";
		final double scale = 3;

		// IDLE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null, null),
				new LoopingSprite(base + "_Down_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.RIGHT, null, null),
				new LoopingSprite(base + "_Side_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.LEFT, null, null),
				new LoopingSprite(base + "_Side-left_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.UP, null, null),
				new LoopingSprite(base + "_Up_Idle-Sheet6.png", 6, 1, scale, animationFrameTime));

		// MOVE
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.DOWN, null, null),
				new LoopingSprite(base + "_Down_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.RIGHT, null, null),
				new LoopingSprite(base + "_Side_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.LEFT, null, null),
				new LoopingSprite(base + "_Side-left_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.MOVE, CharacterDirection.UP, null, null),
				new LoopingSprite(base + "_Up_Walk-Sheet8.png", 8, 1, scale, animationFrameTime));

		// PRIMARY ATTACK
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.DOWN, null, null),
				new OneShotSprite(base + "_Down_First-Attack-Sheet8.png", 8, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.RIGHT, null, null),
				new OneShotSprite(base + "_Side_First-Attack-Sheet8.png", 8, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.LEFT, null, null),
				new OneShotSprite(base + "_Side-left_First-Attack-Sheet8.png", 8, 1, scale, animationFrameTime, () -> onAttackEnd()));
		this.getVisualComponent().addSprite(CharacterPart.BODY,
				new CharacterAnimationKey(CharacterAction.ATTACK, CharacterDirection.UP, null, null),
				new OneShotSprite(base + "_Up_First-Attack-Sheet8.png", 8, 1, scale, animationFrameTime, () -> onAttackEnd()));
	}

	public void onAttackStart(Entity target) {

	}

	public void onHit(Entity target) {

	}

	public void onAttackEnd(Entity target) {

	}
}
