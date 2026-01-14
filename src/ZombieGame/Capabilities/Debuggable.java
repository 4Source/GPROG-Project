package ZombieGame.Capabilities;

import java.util.ArrayList;

import ZombieGame.Systems.Debug.DebugCategory;
import ZombieGame.Systems.Debug.DebugCategoryMask;

public abstract interface Debuggable extends Capability {

    default ArrayList<DebugCategory> getCategories() {
        ArrayList<DebugCategory> categories = new ArrayList<>();
        DebugCategoryMask mask = getCategoryMask();

        for (DebugCategory c : DebugCategory.values()) {
            if ((c.bit & mask.bit) != 0) {
                categories.add(c);
            }
        }

        return categories;
    }

    DebugCategoryMask getCategoryMask();
}
