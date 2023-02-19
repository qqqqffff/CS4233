package escape.gui.utilities;

import escape.gui.states.Home;
import javafx.scene.Group;

import java.util.HashMap;
import java.util.Map;

public class AppStateManager {
    private static final Home homeState = new Home();
    private static Map<Group, Boolean> states;
    private AppStateManager(){}

    public static void init(){
        states = new HashMap<>();
        states.put(homeState, true);
    }

    public static Group getActiveAppState(){
        for(Group g: states.keySet()){
            if(states.get(g)) return g;
        }
        throw new NullPointerException("Active State is Null or Not Found");
    }
}
