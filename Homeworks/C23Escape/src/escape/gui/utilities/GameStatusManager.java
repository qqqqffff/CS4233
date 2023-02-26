package escape.gui.utilities;

import escape.builder.EscapeJsonConverter;
import escape.required.Coordinate;
import escape.required.GameObserver;
import escape.required.GameStatus;
import escape.required.LocationType;

import java.util.List;
import java.util.Map;

import static escape.gui.utilities.GameStatusManager.GameStatusKeys.*;

public class GameStatusManager {
    public enum GameStatusKeys {is_valid_move, is_more_information, get_move_result}
    private GameStatusManager(){ }
    public static GameStatus createNewGameStatus(Map<GameStatusKeys, String> data, Coordinate finalLocation){
        return new GameStatus() {
            @Override
            public boolean isValidMove() {
                return Boolean.parseBoolean(data.get(is_valid_move));
            }

            @Override
            public boolean isMoreInformation() {
                return Boolean.parseBoolean(data.get(is_more_information));
            }

            @Override
            public MoveResult getMoveResult() {
                return EscapeJsonConverter.parseMoveResult(data.get(get_move_result));
            }

            @Override
            public Coordinate finalLocation() {
                return finalLocation;
            }
        };
    }
    public static GameStatus.MoveResult analyzeGameStatus(List<GameObserver> gameStatus, LocationType locationType){
        return GameStatus.MoveResult.NONE;
    }
}
