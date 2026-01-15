package ZombieGame.Entities;

import ZombieGame.Components.ButtonSpriteComponent;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Sprites.StaticSprite;

public class Button extends UIElement {
    public Button(ViewPos pos, StaticSprite sprite, StaticSprite activeSprite, Runnable onClick) {
        super(pos, e -> new ButtonSpriteComponent(e, sprite, activeSprite, onClick));
    }
    
    @Override
    public ButtonSpriteComponent getUIComponent() {
        return (ButtonSpriteComponent) super.getUIComponent();
    }   
}
