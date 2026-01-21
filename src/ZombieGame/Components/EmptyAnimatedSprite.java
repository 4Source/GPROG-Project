package ZombieGame.Components;

import ZombieGame.Sprites.AnimatedSprite;

public class EmptyAnimatedSprite extends AnimatedSprite {

    public EmptyAnimatedSprite() {
        super("", 1, 1, 1, 0);
    }

    @Override
    public boolean isAnimationFinished() {
        return true;
    }
}
