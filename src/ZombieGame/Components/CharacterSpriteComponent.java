package ZombieGame.Components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ZombieGame.*;
import ZombieGame.Coordinates.ViewPos;
import ZombieGame.Entities.Entity;
import ZombieGame.Sprites.AnimatedSprite;
import ZombieGame.Sprites.Sprite;
import ZombieGame.Systems.Graphic.GraphicLayer;
import ZombieGame.Systems.Graphic.GraphicSystem;
import ZombieGame.Systems.Graphic.MissingTexture;

public class CharacterSpriteComponent extends SpriteComponent {
    private EnumMap<CharacterPart, HashMap<CharacterAnimationKey, AnimatedSprite>> sprites;
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
            case DOWN_LEFT:
            case DOWN_RIGHT:
            case LEFT:
            case RIGHT:
            case UP_LEFT:
            case UP_RIGHT:
                partsOrder.add(CharacterPart.BODY);
                partsOrder.add(CharacterPart.MUZZLE);
                partsOrder.add(CharacterPart.HANDS);
                break;

            case UP:
                partsOrder.add(CharacterPart.MUZZLE);
                partsOrder.add(CharacterPart.HANDS);
                partsOrder.add(CharacterPart.BODY);
                break;

            default:
                break;
        }

        for (CharacterPart p : partsOrder) {
            Optional<CharacterAnimationKey> s = this.getState(p);
            if (s.isPresent()) {
                CharacterAnimationKey matchedKey = s.get();
                Sprite sprite = this.sprites.get(p).get(matchedKey);

                if (sprite == null) {
                    int size = MissingTexture.getSize();
                    GraphicSystem.getInstance().drawSprite(MissingTexture.getTexture(), view.add(size / 2, size / 2), 0, 0, 1, size * 2, size * 2);
                    continue;
                }

                sprite.draw(view);
            }
        }
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
        if (this.state.attackState() != state.attackState()) {
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
        this.changeState(new CharacterAnimationKey(action, this.state.direction(), this.state.equipment(), this.state.attackState()));
    }

    public void changeState(CharacterDirection direction) {
        this.changeState(new CharacterAnimationKey(this.state.action(), direction, this.state.equipment(), this.state.attackState()));
    }

    public void changeState(CharacterEquipment equipment) {
        this.changeState(new CharacterAnimationKey(this.state.action(), this.state.direction(), equipment, this.state.attackState()));
    }

    public void changeState(CharacterAttackState attackState) {
        this.changeState(new CharacterAnimationKey(this.state.action(), this.state.direction(), this.state.equipment(), attackState));
    }

    public void changeState(CharacterPart part, CharacterAnimationKey state) {

        Optional<CharacterAnimationKey> existingState = getState(part);
        if (existingState.isPresent()) {
            CharacterAnimationKey newState = state;
            this.sprites.get(part).put(newState, this.sprites.get(part).get(newState));
            this.state = newState;
        }
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

    public CharacterAttackState getCharacterAttackState() {
        return this.state.attackState();
    }


    public Optional<CharacterAnimationKey> getState(CharacterPart part) {
        HashMap<CharacterAnimationKey, AnimatedSprite> s = this.sprites.get(part);
        if (s == null) {
            return Optional.empty();
        }

        // Match
        if (s.containsKey(state)) {
            return Optional.of(state);
        }


        // wildcard match
        for (CharacterAnimationKey key : s.keySet()) {
            if (key.matches(state)) {
                return Optional.of(key);
            }
        }

        return Optional.empty();
    }

    public void addSprite(CharacterPart part, CharacterAnimationKey state, AnimatedSprite sprite) {
        this.sprites.putIfAbsent(part, new HashMap<>());
        this.sprites.get(part).put(state, sprite);
    }

    /**
     * @return A flattened variant of the sprites
     */
    @Override
    public List<AnimatedSprite> getSprites() {
        return this.sprites.values().stream().flatMap(m -> m.values().stream()).toList();
    }

    /**
     * @return A set containing the sprite or an empty sprite if no matching sprite found
     */
    public Set<AnimatedSprite> getSprite(CharacterPart part, CharacterAnimationKey animationKey) {
        HashMap<CharacterAnimationKey, AnimatedSprite> map = this.sprites.get(part);
        if (map == null) {
            return new HashSet<>();
        }

        AnimatedSprite sprite = map.get(animationKey);

        if (sprite == null) {
            return new HashSet<>();
        }

        return Collections.singleton(sprite);
    }

    @Override
    public Sprite getSprite(int index) {
        return getSprites().get(index);
    }
}
