package escape.utilities;

import escape.EscapeGameManager;
import escape.builder.EscapeGameInitializer;
import escape.builder.LocationInitializer;
import escape.observers.GameplayObserver;
import escape.observers.LocationObserver;
import escape.observers.PieceObserver;
import escape.required.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static escape.utilities.GameStatusManager.GameStatusKeys.*;

public class GameManager implements EscapeGameManager<Coordinate> {
    protected static List<LocationObserver> locationObservers;
    public static List<PieceObserver> pieceObservers;
    protected static List<GameStatus> gameStatuses;
    protected static Map<Coordinate, LocationType> gameLocations;
    protected static Map<Coordinate, EscapePiece> pieceLocations;
    protected final EscapeGameInitializer gameInitializer;
    protected static GameplayObserver gameObserver;
    private Integer turnLimit;
    private int currentTurnCount = 0;
    protected static boolean pointConflict = false;
    private final List<String> currentTurnPlayers;
    private final int totalPlayers;
    protected static Coordinate.CoordinateType coordinateSystem;


    //DEFAULTS
    public static final LocationType DEFAULT_LOCATION_TYPE = LocationType.CLEAR;


    /**
     * My Manager implementation:
     * a lot of the data processing is handled statically through the other manager classes to help with visibility
     * comments will describe chunks of code through this class and managers
     * @param initializer that is passed
     */
    public GameManager(EscapeGameInitializer initializer){
        this.gameInitializer = initializer;
        coordinateSystem = gameInitializer.getCoordinateType();
        locationObservers = new ArrayList<>();
        pieceObservers = new ArrayList<>();
        gameStatuses = new ArrayList<>();
        currentTurnPlayers = new ArrayList<>();
        //filling in the grid with clear spaces
        gameLocations = initializeGrid(gameInitializer.getxMax(), gameInitializer.getyMax());
        pieceLocations = new HashMap<>();

        //initializing the GameObserverManager
        Integer maxScore = null;
        totalPlayers = gameInitializer.getPlayers().length;
        if(initializer.getRules() != null) {
            for (RuleDescriptor rule : initializer.getRules()) {
                switch (rule.ruleId) {
                    case SCORE -> maxScore = rule.ruleValue;
                    case TURN_LIMIT -> turnLimit = rule.ruleValue;
                    case POINT_CONFLICT -> pointConflict = true;
                }
            }
        }
        gameObserver = new GameplayObserver(gameInitializer.getPlayers(), maxScore);


        //initializing grid with location initializers and data handlers
        //if no player is specified pieces will be assigned to players incrementing at 0
        if(this.gameInitializer.getLocationInitializers() != null) {
            AtomicInteger piecePlayerAssignIndex = new AtomicInteger(0);
            for (LocationInitializer location : this.gameInitializer.getLocationInitializers()) {
                Coordinate locationCoordinate = makeCoordinate(location.getX(), location.getY());
                for(Coordinate coordinate : gameLocations.keySet()){
                    if(coordinate.equals(locationCoordinate)){
                        locationCoordinate = coordinate;
                        break;
                    }
                }
                if (location.getPieceName() != null) {
                    EscapePiece piece = new EscapePiece() {
                        @Override
                        public PieceName getName() {
                            return location.getPieceName();
                        }

                        @Override
                        public String getPlayer() {
                            //if player is null
                            if (location.getPlayer() == null) {
                                if (piecePlayerAssignIndex.get() == initializer.getPlayers().length)
                                    piecePlayerAssignIndex.set(0);
                                return initializer.getPlayers()[piecePlayerAssignIndex.get() != initializer.getPlayers().length ? piecePlayerAssignIndex.getAndIncrement() : piecePlayerAssignIndex.get()];
                            }
                            return location.getPlayer();
                        }
                    };
                    int health = 1;
                    for(PieceTypeDescriptor descriptor : initializer.getPieceTypes()){
                        if(descriptor.getPieceName().equals(piece.getName())) {
                            for (PieceAttribute attribute : descriptor.getAttributes()) {
                                if (attribute.getId().equals(EscapePiece.PieceAttributeID.VALUE)) {
                                    health = attribute.getValue();
                                    break;
                                }
                            }
                        }
                    }
                    pieceObservers.add(new PieceObserver(locationCoordinate, piece, health));
                    pieceLocations.put(locationCoordinate, piece);
                }
                switch (location.getLocationType()) {
                    case BLOCK, EXIT -> {
                        locationObservers.add(new LocationObserver(locationCoordinate, location.getLocationType()));
                        gameLocations.put(locationCoordinate, location.getLocationType());
                    }
                }
            }
        }
        MovementManager.manager = this;
        GameStatusManager.manager = this;
    }

    /**
     * Grid initialization method:
     * Kind of unnecessary but helps with clarity and debugging to see the grid
     * @param maxX the max x for coordinates
     * @param maxY the max y for coordinates
     * @return the map that has all the coordinates of the grid
     */
    public Map<Coordinate, LocationType> initializeGrid(int maxX, int maxY){
        Map<Coordinate, LocationType> grid = new HashMap<>();
        if(gameInitializer.getCoordinateType() == Coordinate.CoordinateType.HEX){
            for(int x = -1 * maxX; x < maxX; x++){
                for(int y = -1 * maxY; y < maxY; y++){
                    grid.put(makeCoordinate(x, y), DEFAULT_LOCATION_TYPE);
                }
            }
        }
        else if(gameInitializer.getCoordinateType() == Coordinate.CoordinateType.SQUARE){
            for(int x = 0; x < maxX; x++){
                for(int y = 0; y < maxY; y++){
                    grid.put(makeCoordinate(x, y), DEFAULT_LOCATION_TYPE);
                }
            }
        }
        return grid;
    }

    /**
     * Move method:
     * does what it says it does
     * @param from starting location
     * @param to ending location
     * @return GameStatus that is generated by the GameStatusManager
     */
    @Override
    public GameStatus move(Coordinate from, Coordinate to) {
        //defaulting the status data list
        Map<GameStatusManager.GameStatusKeys, String> gameStatusData = GameStatusManager.generateEmptyStatusMap();
        //if the game is over can't move
        if(!gameObserver.getGameStatus().equals(GameStatus.MoveResult.NONE)){
            gameStatuses.add(GameStatusManager.createNewGameStatus(gameStatusData, from));
            gameObserver.notify("Game is over!", new EscapeException("Game Over"));
            return gameStatuses.get(gameStatuses.size() - 1);
        }

        EscapePiece attacker = getPieceAt(from);
        Coordinate attackerCoordinate = null;

        //no piece to move, no change in game status
        if(attacker == null) {
            gameObserver.notify("No piece is at the origin!", new EscapeException("Bad Coordinate"));
            gameStatuses.add(GameStatusManager.createNewGameStatus(gameStatusData, from));
            return gameStatuses.get(gameStatuses.size() - 1);
        }
        else{
            //turn logic
            for(String player : currentTurnPlayers){
                if(attacker.getPlayer().equals(player)){
                    String possessive = player.charAt(player.length() - 1) == 's' ? "' " : "'s ";
                    gameObserver.notify("It is not " + player + possessive + "turn!", new EscapeException("Wrong Turn"));
                    gameStatuses.add(GameStatusManager.createNewGameStatus(gameStatusData, from));
                    return gameStatuses.get(gameStatuses.size() - 1);
                }
            }
            currentTurnPlayers.add(attacker.getPlayer());
            if(currentTurnPlayers.size() == totalPlayers){
                currentTurnCount++;
                currentTurnPlayers.clear();
            }

            //finding coordinate in the map, maps are wierd
            for(Coordinate coordinate : pieceLocations.keySet()){
                if(coordinate.equals(from)) {
                    attackerCoordinate = coordinate;
                    break;
                }
            }
        }

        //looking for defender
        EscapePiece defender = getPieceAt(to);
        Coordinate defenderCoordinate = null;

        PieceAttribute[] attackerAttributes = null;
        PieceAttribute[] defenderAttributes = null;

        EscapePiece.MovementPattern attackerPattern = null;

        for(PieceTypeDescriptor descriptor : gameInitializer.getPieceTypes()){
            if(descriptor.getPieceName() == attacker.getName()) {
                attackerAttributes = descriptor.getAttributes();
                attackerPattern = descriptor.getMovementPattern();
            }
            else if(defender != null && descriptor.getPieceName().equals(defender.getName())){
                defenderAttributes = descriptor.getAttributes();
            }
        }

        //no defender
        if(defender == null){
            gameStatusData = GameStatusManager.verifyMove(attackerAttributes, attackerPattern, from, to);
            if(Boolean.parseBoolean(gameStatusData.get(is_valid_move))){
                pieceLocations.remove(attackerCoordinate);
                boolean exit = false;
                for(LocationObserver observer : locationObservers){
                    if(observer.getCoordinate().equals(to)){
                        exit = observer.locationType().equals(LocationType.EXIT);
                        break;
                    }
                }
                if(!exit) pieceLocations.put(to, attacker);
            }
            else{
                //fixing current player turn when invalid move
                if(currentTurnPlayers.size() == 0){
                    for(String player : gameInitializer.getPlayers()){
                        if(player.equals(attacker.getPlayer())) continue;
                        currentTurnPlayers.add(player);
                    }
                    currentTurnCount--;
                }
                else{
                    currentTurnPlayers.remove(attacker.getPlayer());
                }
            }
        }
        //yes defender
        else{
            //no captures allowed
            if(!pointConflict){
                gameStatuses.add(GameStatusManager.createNewGameStatus(gameStatusData, from));
                System.out.println("Error - No Captures: No Captures are allowed!");
                return gameStatuses.get(gameStatuses.size() - 1);
            }
            //if there is no value attributes for the attacker or defender: both pieces die
            //if there is no value attribute for the attacker: defender wins if value is not negative
            //if there is no value for defender: attacker wins
            gameStatusData = GameStatusManager.verifyMove(attackerAttributes, attackerPattern, defenderAttributes, from, to);
            //all processing is handled by game status manager
        }
        //last player has gone -> end the game
        if(Boolean.parseBoolean(gameStatusData.get(is_valid_move)) && turnLimit != null && currentTurnCount > turnLimit){
            gameObserver.notify(attacker.getPlayer());
        }
        gameStatuses.add(GameStatusManager.createNewGameStatus(gameStatusData, to));
        return gameStatuses.get(gameStatuses.size() - 1);
    }

    /**
     * Piece Getter method
     * @param from the location to probe
     * @return the piece if it is found null otherwise
     */
    @Override
    public EscapePiece getPieceAt(Coordinate from) {
        for(Coordinate coordinate : pieceLocations.keySet()){
            if(coordinate.equals(from)) return pieceLocations.get(coordinate);
        }
        return null;
    }

    /**
     * Coordinate Generator Method:
     * Generates the coordinates to be used
     * Note: hex sadly does not work
     * @param x the x component
     * @param y the y component
     * @return the Coordinate
     */
    @Override
    public Coordinate makeCoordinate(int x, int y) {
        if(gameInitializer.getCoordinateType() == null) throw new EscapeException("Coordinate Type is not defined or null");
        if (gameInitializer.getCoordinateType() == Coordinate.CoordinateType.HEX) {
            if (x > gameInitializer.getxMax() || x < -gameInitializer.getxMax() || y > gameInitializer.getyMax() || y < -gameInitializer.getyMax())
                throw new EscapeException("Coordinate Parameters are out of bounds");
            return new Coordinate() {
                @Override
                public int getRow() {
                    return y + (x / 2);
                }

                @Override
                public int getColumn() {
                    return x;
                }

                @Override
                public boolean equals(Coordinate obj) {
                    return this.getColumn() == obj.getColumn() && this.getRow() == obj.getRow();
                }
            };
        }
        else if (gameInitializer.getCoordinateType() == Coordinate.CoordinateType.SQUARE) {
            if(!(gameInitializer.getxMax() == 0 && gameInitializer.getyMax() == 0)) {
                if (x > gameInitializer.getxMax() || x < 0 || y > gameInitializer.getyMax() || y < 0)
                    throw new EscapeException("Coordinate Parameters are out of bounds");
            }
            return new Coordinate() {
                @Override
                public int getRow() {
                    return y;
                }

                @Override
                public int getColumn() {
                    return x;
                }
                @Override
                public boolean equals(Coordinate obj) {
                    return this.getColumn() == obj.getColumn() && this.getRow() == obj.getRow();
                }
            };
        }
        throw new EscapeException("Coordinate Type is invalid");
    }

    /**
     * Observer adder (unused)
     */
    @Override
    @Deprecated
    public GameObserver addObserver(GameObserver observer) {
        return EscapeGameManager.super.addObserver(observer);
    }

    /**
     * Observer Remover
     * @param observer to be removed
     * @return removed observer
     */
    @Override
    public GameObserver removeObserver(GameObserver observer) {
        if(observer == null) return null;
        if(observer.getClass().equals(PieceObserver.class)){
            pieceObservers.remove(observer);
        }
        return observer;
    }
}
