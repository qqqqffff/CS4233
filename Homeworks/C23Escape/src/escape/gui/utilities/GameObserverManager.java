package escape.gui.utilities;

import escape.required.EscapePiece;
import escape.required.GameObserver;
import escape.required.LocationType;
import escape.required.PieceAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameObserverManager {
    private GameObserverManager(){}
    public static GameObserver generateGameObserver(LocationType locationType, EscapePiece piece){
        return new GameObserver() {

            @Override
            public void notify(String message) {
                GameObserver.super.notify(message);
            }

            @Override
            public void notify(String message, Throwable cause) {
                GameObserver.super.notify(message, cause);
            }
        };
    }
    public static Map<GameStatusManager.GameStatusKeys, String> verifyMove(List<GameObserver> observers, PieceAttribute[] attributes, EscapePiece.MovementPattern pattern){
        Map<GameStatusManager.GameStatusKeys, String> observerData = new HashMap<>();

        return observerData;
    }
    public static Map<GameStatusManager.GameStatusKeys, String> verifyMove(List<GameObserver> observers, PieceAttribute[] attackerAttributes, EscapePiece.MovementPattern attackerPattern, PieceAttribute[] defenderAttributes){
        Map<GameStatusManager.GameStatusKeys, String> observerData = new HashMap<>();

        return observerData;
    }
}
