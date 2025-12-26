
// (c) Thorsten Hasbargen

public class Zombie extends Creature {
	/**
	 * Spawns a zombie
	 * 
	 * @param startX The position in x of the zombie where is should be at game start
	 * @param startY The position in y of the zombie where is should be at game start
	 */
	public Zombie(double startX, double startY) {
		super(startX, startY, e -> new CharacterSpriteComponent(e, new CharacterAnimationKey(CharacterAction.IDLE, CharacterDirection.DOWN, null)), new CircleHitBox(HitBoxType.Block, 18, 0, 25), e -> new AIMovementComponent(e, 0, 60), e -> new LifeComponent(e, 100));

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
	public AIMovementComponent getMovementComponent() {
		return (AIMovementComponent) super.getMovementComponent();
	}

	@Override
	public EntityType getType() {
		return EntityType.ZOMBIE;
	}

	@Override
	protected void onCollisionStart(Collision collision) {
		if (collision.collisionResponse() == CollisionResponse.Block) {
			EntityType type = collision.entity().getType();

			// if object is avatar, game over
			if (type == EntityType.AVATAR) {
				this.getMovementComponent().moveBack();
				((Avatar) collision.entity()).getLifeComponent().takeDamage(10);
			}

			// if object is zombie, step back
			if (type == EntityType.ZOMBIE) {
				this.getMovementComponent().moveBack();
				this.getMovementComponent().state = AIState.STUCK;
				return;
			}

			// if Object is a tree, move back one step
			if (type == EntityType.TREE) {
				this.getMovementComponent().moveBack();
				this.getMovementComponent().state = AIState.STUCK;
				return;
			}
		}

	}

	@Override
	protected void onCollisionEnd(Collision collision) {
	}
}
