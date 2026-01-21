package ZombieGame;

public record CharacterAnimationKey(
                CharacterAction action,
                CharacterDirection direction,
                CharacterEquipment equipment,
                CharacterAttackState attackState)

{
    public boolean matches(CharacterAnimationKey other) {
    return (action == null || action == other.action)
            && (direction == null || direction == other.direction)
            && (equipment == null || equipment == other.equipment)
            && (attackState == null || attackState == other.attackState);
    }
}