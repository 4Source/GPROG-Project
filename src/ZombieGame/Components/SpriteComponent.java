package ZombieGame.Components;

import java.util.Collection;

import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.Sprite;

public abstract class SpriteComponent extends VisualComponent {

    public SpriteComponent(Entity entity) {
        super(entity);
    }

    public abstract Collection<? extends Sprite> getSprite();
}
