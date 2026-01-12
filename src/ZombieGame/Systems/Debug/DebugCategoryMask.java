package ZombieGame.Systems.Debug;

public final class DebugCategoryMask {
    public final int bit;

    public DebugCategoryMask(DebugCategory category, DebugCategory... categories) {
        int value = category.bit;
        for (DebugCategory l : categories) {
            value |= l.bit;
        }
        this.bit = value;
    }

    private DebugCategoryMask(int bit) {
        this.bit = bit;
    }

    /**
     * All Layers will match
     */
    public static DebugCategoryMask ALL() {
        return new DebugCategoryMask(-1);
    }
}