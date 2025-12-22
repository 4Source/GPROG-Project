import java.awt.Color;

class Avatar extends Creature {
	private GunshotComponent gunshotComponent;
	private GrenadeComponent grenadeComponent;

	/**
	 * Spawns an avatar
	 * 
	 * @param startX The position in x of the avatar where is should be at game start
	 * @param startY The position in y of the avatar where is should be at game start
	 */
	public Avatar(double startX, double startY) {
		super(startX, startY, 15, new Color(96, 96, 255), e -> new PlayerMovementComponent(e, 200), e -> new LifeComponent(e, 100));
		this.gunshotComponent = this.add(new GunshotComponent(this, 0.2));
		this.grenadeComponent = this.add(new GrenadeComponent(this));
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
