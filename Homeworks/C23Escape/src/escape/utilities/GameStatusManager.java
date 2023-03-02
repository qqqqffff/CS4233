package escape.utilities;

import escape.EscapeGameManager;
import escape.observers.LocationObserver;
import escape.observers.PieceObserver;
import escape.required.*;

import java.util.*;

import static escape.utilities.GameStatusManager.GameStatusKeys.*;

/**
 * Game Status Manager Class:
 * Handles Game Status Related Activities Statically for the Game Manager
 */
public class GameStatusManager {
    public static final boolean DISPLAY_PATH_STEPS = false;
    public static EscapeGameManager<Coordinate> manager;
    public enum GameStatusKeys {is_valid_move, is_more_information, get_move_result}

    /**
     * Game Status Generator:
     * generates a game status based on the data and the final location
     * @param data that will be used
     * @param finalLocation that will also be used
     * @return the game status
     */
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
                return parseMoveResult(data.get(get_move_result));
            }

            @Override
            public Coordinate finalLocation() {
                return finalLocation;
            }
        };
    }

    /**
     * Empty Status Data Map Generator:
     * Generates a map with defaulted values that will be able to create a game status
     * @return a map with the relevant data (or irrelevant)
     */
    public static Map<GameStatusManager.GameStatusKeys, String> generateEmptyStatusMap(){
        Map<GameStatusManager.GameStatusKeys, String> map = new HashMap<>();
        map.put(GameStatusManager.GameStatusKeys.is_valid_move, "null");
        map.put(GameStatusManager.GameStatusKeys.is_more_information, "null");
        map.put(GameStatusManager.GameStatusKeys.get_move_result, GameStatus.MoveResult.NONE.name());
        return map;
    }

    /**
     * Verify Move Method (simple move):
     * Verifies the move given the following parameters
     * @param attributes attributes of the piece that is moving
     * @param pattern movement pattern to be obeyed
     * @param from origin coordinate
     * @param to destination coordinate
     * @return a map of the relevant game status data when attempting this move
     */
    public static Map<GameStatusManager.GameStatusKeys, String> verifyMove(PieceAttribute[] attributes, EscapePiece.MovementPattern pattern, Coordinate from, Coordinate to){
        Map<GameStatusManager.GameStatusKeys, String> observerData = GameStatusManager.generateEmptyStatusMap();

        //retrieving the relevant attributes
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

        //getting the moving player's data
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

        //getting the location type
        LocationType toLocationType = LocationType.CLEAR;
        for(LocationObserver locationObserver : GameManager.locationObservers) {
            if (locationObserver.getCoordinate().equals(to)) toLocationType = locationObserver.locationType();
        }

        //attempting to generate a path
        List<Coordinate> path = MovementManager.generatePath(pattern, from, to, fly, jump, unblock,distance + 1);
        //failed
        if(path == null|| (!fly && toLocationType.equals(LocationType.BLOCK))){
            playerObserver.notify("cannot move to (" + to.getRow() + "," + to.getColumn() + ")", new EscapeException("Bad Path"));
            observerData.put(is_valid_move, "false");
            return observerData;
        }
        //success
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
            //a lot of information is being handled at once
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

    /**
     * Verify Move Method (piece capture):
     * Verifying the move given the following parameters
     * @param attackerAttributes attributes of the attacking piece
     * @param attackerPattern attacker movement pattern
     * @param defenderAttributes attributes of the defending piece
     * @param from origin coordinates
     * @param to destination coordinates
     * @return a map with the game status as a result of attempting this move
     */
    public static Map<GameStatusManager.GameStatusKeys, String> verifyMove(PieceAttribute[] attackerAttributes, EscapePiece.MovementPattern attackerPattern, PieceAttribute[] defenderAttributes, Coordinate from, Coordinate to){
        Map<GameStatusManager.GameStatusKeys, String> observerData = GameStatusManager.generateEmptyStatusMap();

        //getting attacker and defender attributes
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
                    case DISTANCE -> distance = attribute.getValue();
                    case VALUE -> attackerValue = attribute.getValue();
                }
            }
        }
        if(defenderAttributes != null){
            for(PieceAttribute attribute : defenderAttributes){
                if(attribute.getId().equals(EscapePiece.PieceAttributeID.VALUE)) {
                    defenderValue = attribute.getValue();
                    break;
                }
            }
        }

        //getting attacker and defender data
        String attacker = null;
        String defender = null;
        PieceObserver attackerObserver = null;
        PieceObserver defenderObserver = null;
        for(PieceObserver observer : GameManager.pieceObservers){
            if(observer.getCoordinate().equals(from)) {
                attacker = observer.getPiece().getPlayer();
                attackerObserver = observer;
            }
            else if(observer.getCoordinate().equals(to)) {
                defender = observer.getPiece().getPlayer();
                defenderObserver = observer;
            }
        }
        assert attacker != null && defender != null;

        //getting location type
        LocationType toLocationType = LocationType.CLEAR;
        for(LocationObserver locationObserver : GameManager.locationObservers){
            if(locationObserver.getCoordinate().equals(to)) toLocationType = locationObserver.locationType();
        }

        //build a path of coordinates that the piece will go across depending on the movement pattern
        //null means that the path is not possible to be created
        //minimum distance path will be returned (hopefully)
        //path includes start point
        List<Coordinate> path = MovementManager.generatePath(attackerPattern, from, to, fly, jump, unblock,distance + 1);
        //failure
        if(path == null || (!fly && toLocationType.equals(LocationType.BLOCK))){
            attackerObserver.notify("cannot move to (" + to.getRow() + "," + to.getColumn() + ")", new EscapeException("Bad Path"));
            observerData.put(is_valid_move, "false");
            return observerData;
        }
        //success
        else{
            if(DISPLAY_PATH_STEPS) {
                for (Coordinate coordinate : path) {
                    System.out.println(coordinate.getColumn() + ", " + coordinate.getRow());
                }
            }

            //piece health calculations and observer logic
            String possessive = attacker.charAt(attacker.length() - 1) == 's' ? "' ": "'s ";
            int attackerHealth = attackerObserver.getPieceHealth();
            int defenderHealth = defenderObserver.getPieceHealth();
            attackerObserver.setPieceHealth(attackerObserver.getPieceHealth() - defenderHealth);
            defenderObserver.setPieceHealth(defenderObserver.getPieceHealth() - attackerHealth);
            attackerObserver.setCoordinate(to);
            defenderObserver.setCoordinate(to);
            if(attackerObserver.getPieceHealth() <= 0) {
                if (defenderObserver.getPieceHealth() <= 0) {
                    attackerObserver.notify("has been DESTROYED!");
                    defenderObserver.notify("has been DESTROYED!");
                    manager.removeObserver(defenderObserver);
                    GameManager.gameObserver.updatePlayerScore(attacker, defenderValue);
                }
                else {
                    attackerObserver.notify("has been CAPTURED by " + defender + possessive + defenderObserver.getPiece().getName() + "!");
                }
                manager.removeObserver(attackerObserver);
                GameManager.gameObserver.updatePlayerScore(defender, attackerValue);
            }
            else if(defenderObserver.getPieceHealth() <= 0 && attackerObserver.getPieceHealth() > 0){
                defenderObserver.notify("has been CAPTURED by " + attacker + possessive + attackerObserver.getPiece().getName() + "!");
                manager.removeObserver(defenderObserver);
                GameManager.gameObserver.updatePlayerScore(attacker, defenderValue);
            }

            Map<String, Integer> playerPieces = new HashMap<>();
            Arrays.stream(GameManager.gameObserver.getPlayers()).toList().forEach(player -> playerPieces.put(player, 0));

            //checking if the game is over
            for(PieceObserver observer : GameManager.pieceObservers){
                playerPieces.put(observer.getPiece().getPlayer(), playerPieces.get(observer.getPiece().getPlayer()) + 1);
            }

            boolean continueGame = true;
            for(Integer pieces : playerPieces.values()){
                if(pieces == 0){
                    continueGame = false;
                    break;
                }
            }
            if(!continueGame){
                GameManager.gameObserver.notify(attacker);
            }

            observerData.put(is_more_information, "true");
            observerData.put(is_valid_move, "true");
            observerData.put(get_move_result, GameManager.gameObserver.getGameStatus().toString());
        }

        return observerData;
    }

    /**
     * Move Result Parser:
     * Parses the Move Result based on the string and returns the respective enum value
     * Follows the same structure as all the other enum parsers
     * @param moveResult string value of the enum
     * @return the parsed enum if exists
     */
    public static GameStatus.MoveResult parseMoveResult(String moveResult){
        if(moveResult == null) return GameStatus.MoveResult.NONE;
        moveResult = moveResult.toUpperCase(Locale.ROOT);
        if(moveResult.equals(GameStatus.MoveResult.DRAW.name())) return GameStatus.MoveResult.DRAW;
        else if(moveResult.equals(GameStatus.MoveResult.LOSE.name())) return GameStatus.MoveResult.LOSE;
        else if(moveResult.equals(GameStatus.MoveResult.WIN.name())) return GameStatus.MoveResult.WIN;
        return GameStatus.MoveResult.NONE;
    }
}
