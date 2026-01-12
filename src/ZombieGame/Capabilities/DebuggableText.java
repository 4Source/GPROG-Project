package ZombieGame.Capabilities;

import java.util.ArrayList;

public interface DebuggableText extends Debuggable {
    /**
     * @return The text elements which should be shown in debug overlay
     */
    ArrayList<String> getTextElements();
}
