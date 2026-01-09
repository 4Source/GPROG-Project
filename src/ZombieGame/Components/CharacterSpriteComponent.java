package ZombieGame.Components;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ZombieGame.CharacterAction;
import ZombieGame.CharacterAnimationKey;
import ZombieGame.CharacterDirection;
import ZombieGame.CharacterEquipment;
import ZombieGame.CharacterPart;
import ZombieGame.GraphicLayer;
import ZombieGame.GraphicSystem;
import ZombieGame.MissingTexture;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.Sprite;

public class CharacterSpriteComponent extends SpriteComponent {
    private EnumMap<CharacterPart, Map<CharacterAnimationKey, Sprite>> sprites;
    private CharacterAnimationKey state;

    public CharacterSpriteComponent(Entity entity, CharacterAnimationKey initialState) {
        super(entity);

        this.sprites = new EnumMap<>(CharacterPart.class);
        this.state = initialState;
    }

    @Override
    public void draw() {
        ViewPos view = this.getEntity().getPositionComponent().getViewPos();

        ArrayList<CharacterPart> partsOrder = new ArrayList<>();
        switch (this.getCharacterDirection()) {
            case DOWN:
            case LEFT:
            case RIGHT:
                partsOrder.add(CharacterPart.BODY);
                partsOrder.add(CharacterPart.HANDS);
                break;
            case UP:
                partsOrder.add(CharacterPart.HANDS);
                partsOrder.add(CharacterPart.BODY);
                break;

            default:
                break;
        }

        partsOrder.forEach(p -> {
            this.getState(p).ifPresent(s -> {
                Sprite sprite = this.sprites.getOrDefault(p, new HashMap<>()).get(s);

                if (sprite == null) {
                    int size = MissingTexture.getSize();
                    GraphicSystem.getInstance().drawSprite(MissingTexture.getTexture(), view, size, size, new ViewPos(), size, size);
                    return;
                }

                sprite.draw(view);
            });
        });
    }

    @Override
    public GraphicLayer getLayer() {
        return GraphicLayer.GAME;
    }

    @Override
    public void update(double deltaTime) {
        for (CharacterPart part : this.sprites.keySet()) {
            this.getState(part).ifPresent(s -> {
                Optional.ofNullable(this.sprites.get(part).get(s)).ifPresent(b -> b.update(deltaTime));
            });
        }
    }

    public void changeState(CharacterAnimationKey state) {
        boolean changed = false;
        if (this.state.action() != state.action()) {
            changed = true;
        }
        if (this.state.direction() != state.direction()) {
            changed = true;
        }
        if (this.state.equipment() != state.equipment()) {
            changed = true;
        }

        this.state = state;

        if (changed) {
            for (CharacterPart part : this.sprites.keySet()) {
                this.getState(part).ifPresent(s -> {
                    Optional.ofNullable(this.sprites.get(part).get(s)).ifPresent(b -> {
                        b.setColumnIndex(0);
                        b.setRowIndex(0);
                    });
                });
            }
        }
    }

    public void changeState(CharacterAction action) {
        this.changeState(new CharacterAnimationKey(action, this.state.direction(), this.state.equipment()));
    }

    public void changeState(CharacterDirection direction) {
        this.changeState(new CharacterAnimationKey(this.state.action(), direction, this.state.equipment()));
    }

    public void changeState(CharacterEquipment equipment) {
        this.changeState(new CharacterAnimationKey(this.state.action(), this.state.direction(), equipment));
    }

    public CharacterAction getCharacterAction() {
        return this.state.action();
    }

    public CharacterDirection getCharacterDirection() {
        return this.state.direction();
    }

    public CharacterEquipment getCharacterEquipment() {
        return this.state.equipment();
    }

    public Optional<CharacterAnimationKey> getState(CharacterPart part) {
        Map<CharacterAnimationKey, Sprite> s = this.sprites.get(part);
        if (s == null) {
            return Optional.empty();
        }

        if (s.containsKey(this.state)) {
            return Optional.of(this.state);
        }

        CharacterAnimationKey newState;
        if (s.containsKey(newState = new CharacterAnimationKey(this.state.action(), this.state.direction(), null))) {
            return Optional.of(newState);
        }

        if (s.containsKey(newState = new CharacterAnimationKey(this.state.action(), null, this.state.equipment()))) {
            return Optional.of(newState);
        }

        if (s.containsKey(newState = new CharacterAnimationKey(this.state.action(), null, null))) {
            return Optional.of(newState);
        }

        if (s.containsKey(newState = new CharacterAnimationKey(null, this.state.direction(), this.state.equipment()))) {
            return Optional.of(newState);
        }

        if (s.containsKey(newState = new CharacterAnimationKey(null, this.state.direction(), null))) {
            return Optional.of(newState);
        }

        if (s.containsKey(newState = new CharacterAnimationKey(null, null, this.state.equipment()))) {
            return Optional.of(newState);
        }

        return Optional.empty();
    }

    public void addSprite(CharacterPart part, CharacterAnimationKey state, Sprite sprite) {
        this.sprites.putIfAbsent(part, new HashMap<>());
        this.sprites.get(part).put(state, sprite);
    }
}
