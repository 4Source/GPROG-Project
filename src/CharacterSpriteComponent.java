
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

enum CharacterAction {
    IDLE, MOVE, ATTACK, PICKUP, DEATH
}

enum CharacterDirection {
    UP, LEFT, DOWN, RIGHT
}

enum CharacterEquipment {
    HANDS, BAT, GUN, PISTOL, SHOTGUN
}

record CharacterAnimationKey(
        CharacterAction action,
        CharacterDirection direction,
        CharacterEquipment equipment) {
}

enum CharacterPart {
    BODY, HANDS
}

public class CharacterSpriteComponent extends SpriteComponent {
    private EnumMap<CharacterPart, Map<CharacterAnimationKey, Sprite>> sprites;
    private CharacterAnimationKey state;

    protected CharacterSpriteComponent(Entity entity, CharacterAnimationKey initialState) {
        super(entity);

        this.sprites = new EnumMap<>(CharacterPart.class);
        this.state = initialState;
    }

    @Override
    public void draw() {
        double posX = this.getEntity().posX - Entity.world.worldPartX;
        double posY = this.getEntity().posY - Entity.world.worldPartY;

        this.getState(CharacterPart.BODY).ifPresent(s -> {
            Sprite body = this.sprites.getOrDefault(CharacterPart.BODY, new HashMap<>()).get(s);

            if (body == null) {
                int size = MissingTexture.getSize();
                GraphicSystem.getInstance().drawSprite(MissingTexture.getTexture(), (int) posX, (int) posX, size, size, 0, 0, size, size);
            }

            body.draw(posX, posY);
        });

        this.getState(CharacterPart.HANDS).ifPresent(s -> {
            Sprite hand = this.sprites.getOrDefault(CharacterPart.HANDS, new HashMap<>()).get(s);

            if (hand == null) {
                int size = MissingTexture.getSize();
                GraphicSystem.getInstance().drawSprite(MissingTexture.getTexture(), (int) posX, (int) posX, size, size, 0, 0, size, size);
            }

            hand.draw(posX, posY);
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

    // @Override
    public void addSprite(CharacterPart part, CharacterAnimationKey state, Sprite sprite) {
        this.sprites.putIfAbsent(part, new HashMap<>());
        this.sprites.get(part).put(state, sprite);
    }
}
