package escape.gui.utilities;

import escape.builder.EscapeGameBuilder;
import escape.builder.EscapeGameInitializer;
import escape.gui.Screen;
import escape.gui.State;
import escape.gui.states.Game;
import escape.gui.states.GameSelect;
import escape.gui.states.Home;
import javafx.scene.Group;

import java.util.HashMap;
import java.util.Map;

public class AppStateManager {
    private static final Home homeState = new Home();
    private static final GameSelect gameSelectState = new GameSelect();
    private static Game gameState = new Game(null);
    private static Map<Group, Boolean> states;
    private AppStateManager(){}

    public static void init(){
        states = new HashMap<>();
        states.put(homeState, false);
        states.put(gameSelectState, false);
        states.put(gameState, false);
    }

    public static Group getActiveAppState(){
        for(Group g: states.keySet()){
            if(states.get(g)) return g;
        }
        throw new NullPointerException("Active State is Null or Not Found");
    }
    public static void setActiveState(State activeState){
        if(activeState == null) throw new NullPointerException("State parameter is null");
        removeAllActiveStates();
        switch(activeState){
            case HOME -> states.put(homeState, true);
            case GAME_SELECT -> states.put(gameSelectState, true);
            case IN_GAME -> states.put(gameState, true);
        }
        Screen.addChild(getActiveAppState());
    }
    public static void transitionStates(State origin, State destination, Map<String, String> data){
        if(origin == null || destination == null) throw new NullPointerException("Origin or Destination Parameter is null");
        if(data == null) data = new HashMap<>();

        switch(destination){
            case HOME -> {
                //TODO: implement me
            }
            case GAME_SELECT -> {
                data.put(DataManager.SaveStateKeys.state.name(), State.GAME_SELECT.name());
                if(origin == State.HOME) setActiveState(State.GAME_SELECT);
                else{
                    //do something
                }
            }
            case IN_GAME -> {
                data.put(DataManager.SaveStateKeys.state.name(), State.IN_GAME.name());
                setActiveState(State.IN_GAME);
            }
        }

        DataManager.updateSaveStateData(data);
    }
    private static void removeAllActiveStates(){
        states.keySet().forEach(group -> {
            if(states.get(group)) Screen.removeChild(group.getId());
            states.put(group, false);
        });
    }
    public static void launchGame(String econfigPath){
        //TODO implement me
        try {
            gameState = new Game(new EscapeGameBuilder(econfigPath).getGameInitializer());
            Map<String, String> data = new HashMap<>();
            data.put(DataManager.SaveStateKeys.econfig_path.name(), econfigPath);
            transitionStates(State.GAME_SELECT, State.IN_GAME, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
