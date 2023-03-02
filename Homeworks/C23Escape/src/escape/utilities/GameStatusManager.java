package escape.utilities;

import escape.EscapeGameManager;
import escape.builder.EscapeJsonConverter;
import escape.observers.LocationObserver;
import escape.observers.PieceObserver;
import escape.required.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static escape.utilities.GameStatusManager.GameStatusKeys.*;

public class GameStatusManager {
    public static final boolean DISPLAY_PATH_STEPS = false;
    public static EscapeGameManager<Coordinate> manager;
    public enum GameStatusKeys {is_valid_move, is_more_information, get_move_result}
    private GameStatusManager(){ }

    public GameStatus.MoveResult analyzeGameStatus(List<GameObserver> gameStatus, LocationType locationType){
        return GameStatus.MoveResult.NONE;
    }


    public static GameStatus createNewGameStatus(Map<GameStatusKeys, String> data, Coordinate finalLocation){
        return new GameStatus() {
            @Override
            public boolean isValidMove() {
                return Boolean.parseBoolean(data.get(is_valid_move));
            }

            @Override
            public boolean isMoreInformation() {
                if(data.get(is_more_information).equals("null")) return false;
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
    public static Map<GameStatusManager.GameStatusKeys, String> generateEmptyStatusMap(){
        Map<GameStatusManager.GameStatusKeys, String> map = new HashMap<>();
        map.put(GameStatusManager.GameStatusKeys.is_valid_move, "null");
        map.put(GameStatusManager.GameStatusKeys.is_more_information, "null");
        map.put(GameStatusManager.GameStatusKeys.get_move_result, GameStatus.MoveResult.NONE.name());
        return map;
    }

    public static Map<GameStatusManager.GameStatusKeys, String> verifyMove(PieceAttribute[] attributes, EscapePiece.MovementPattern pattern, Coordinate from, Coordinate to){
        Map<GameStatusManager.GameStatusKeys, String> observerData = GameStatusManager.generateEmptyStatusMap();

        boolean fly = false;
        boolean jump = false;
        boolean unblock = false;
        int distance = 1;
        int value = 1;
        if(attributes != null){
            for (PieceAttribute attribute : attributes) {
                switch (attribute.getId()) {
                    case FLY -> fly = true;
                    case JUMP -> jump = true;
                    case UNBLOCK -> unblock = true;
                    case DISTANCE -> distance = attribute.getValue();
                    case VALUE -> value = attribute.getValue();
                }
            }
        }
        String movingPlayer = null;
        PieceObserver playerObserver = null;
        for(PieceObserver observer : GameManager.pieceObservers){
            if(observer.getCoordinate().equals(from)) {
                movingPlayer = observer.getPiece().getPlayer();
                playerObserver = observer;
                break;
            }
        }
        assert movingPlayer != null;

        List<Coordinate> path = MovementManager.generatePath(pattern, from, to, fly, jump, unblock,distance + 1);
        if(path == null){
            playerObserver.notify(" cannot move to (" + to.getRow() + "," + to.getColumn() + ")", new EscapeException("Bad Path"));
            observerData.put(is_valid_move, "false");
            return observerData;
        }
        else{
            if(DISPLAY_PATH_STEPS) {
                for (Coordinate coordinate : path) {
                    System.out.println(coordinate.getColumn() + ", " + coordinate.getRow());
                }
                System.out.println();
            }

            boolean exit = false;
            //getting special location type
            for(LocationObserver observer : GameManager.locationObservers){
                if(observer.getCoordinate().equals(to)){
                    if(observer.locationType() == LocationType.EXIT){
                        exit = true;
                    }
                }
            }

            //initializing player map
            Map<String, Integer> playerPieces = new HashMap<>();
            Arrays.stream(GameManager.gameObserver.getPlayers()).toList().forEach(player -> playerPieces.put(player, 0));

            //using observers to update information
            PieceObserver observerToRemove = null;
            for(PieceObserver observer : GameManager.pieceObservers) {
                playerPieces.put(observer.getPiece().getPlayer(), playerPieces.get(observer.getPiece().getPlayer()) + 1);
                if (observer.getCoordinate().equals(from)) {
                    observer.setCoordinate(to);
                    if (exit) {
                        observerData.put(is_more_information, "true");
                        observerToRemove = observer;
                        if (jump || fly) {
                            GameManager.gameObserver.updatePlayerScore(movingPlayer, value);
                            String pointGrammar = GameManager.gameObserver.getPlayerScore(movingPlayer) == 1 ? " point" : " points";
                            observer.notify("has ESCAPED! " + movingPlayer + " now has " + GameManager.gameObserver.getPlayerScore(movingPlayer) + pointGrammar);
                        }
                        else observer.notify("has FALLEN!", new EscapeException("Failed Exit"));
                        playerPieces.put(observer.getPiece().getPlayer(), playerPieces.get(observer.getPiece().getPlayer()) - 1);
                    }
                    else {
                        observer.notify("moved from (" + from.getColumn() +"," + from.getRow() + ") to (" + to.getColumn() + "," + to.getRow() + ")");
                    }
                }
            }
            manager.removeObserver(observerToRemove);

            //checking if the game is over
            boolean continueGame = false;
            for(Integer pieces : playerPieces.values()){
                if(pieces != 0){
                    continueGame = true;
                    break;
                }
            }
            if(!continueGame){
                GameManager.gameObserver.notify(movingPlayer);
            }
            observerData.put(is_valid_move, "true");
            observerData.put(get_move_result, GameManager.gameObserver.getGameStatus().toString());
        }

        return observerData;
    }
    public static Map<GameStatusManager.GameStatusKeys, String> verifyMove(PieceAttribute[] attackerAttributes, EscapePiece.MovementPattern attackerPattern, PieceAttribute[] defenderAttributes, Coordinate from, Coordinate to){
        Map<GameStatusManager.GameStatusKeys, String> observerData = GameStatusManager.generateEmptyStatusMap();

        boolean fly = false;
        boolean jump = false;
        boolean unblock = false;
        int attackerValue = 1;
        int defenderValue = 1;
        int distance = 1;
        if(attackerAttributes != null) {
            for (PieceAttribute attribute : attackerAttributes) {
                switch (attribute.getId()) {
                    case FLY -> fly = attribute.getValue() == 1;
                    case JUMP -> jump = attribute.getValue() == 1;
                    case UNBLOCK -> unblock = attribute.getValue() == 1;
                    case VALUE -> attackerValue = attribute.getValue();
                    case DISTANCE -> distance = attribute.getValue();
                }
            }
        }
        else if(defenderAttributes != null){
            for(PieceAttribute attribute : defenderAttributes){
                if (attribute.getId() == EscapePiece.PieceAttributeID.VALUE) {
                    defenderValue = attribute.getValue();
                }
            }
        }

        //build a path of coordinates that the piece will go across depending on the movement pattern
        //null means that the path is not possible to be created
        //minimum distance path will be returned
        //path includes start point
        List<Coordinate> path = MovementManager.generatePath(attackerPattern, from, to, fly, jump, unblock,distance + 1);
        if(path == null || path.size() > (distance + 1)){
            observerData.put(is_valid_move, "false");
            return observerData;
        }
        else{
            if(DISPLAY_PATH_STEPS) {
                for (Coordinate coordinate : path) {
                    System.out.println(coordinate.getColumn() + ", " + coordinate.getRow());
                }
            }
            observerData.put(is_valid_move, "true");
        }

        return observerData;
    }
}
