package escape.utilities;

import escape.EscapeGameManager;
import escape.builder.EscapeGameInitializer;
import escape.builder.LocationInitializer;
import escape.observers.LocationObserver;
import escape.observers.PieceObserver;
import escape.required.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static escape.utilities.GameStatusManager.GameStatusKeys.*;

public class GameManager implements EscapeGameManager<Coordinate> {
    protected static List<LocationObserver> locationObservers;
    protected static List<PieceObserver> pieceObservers;
    protected static List<GameStatus> gameStatuses;
    protected static Map<Coordinate, LocationType> gameLocations;
    protected static Map<Coordinate, EscapePiece> pieceLocations;
    protected final EscapeGameInitializer gameInitializer;


    //DEFAULTS
    public static final LocationType DEFAULT_LOCATION_TYPE = LocationType.CLEAR;


    public GameManager(EscapeGameInitializer initializer){
        this.gameInitializer = initializer;
        locationObservers = new ArrayList<>();
        pieceObservers = new ArrayList<>();
        gameStatuses = new ArrayList<>();
        //filling in the grid with clear spaces [starting from (-x, -y) -> (+x, +y)]
        gameLocations = initializeGrid(gameInitializer.getxMax(), gameInitializer.getyMax());
        pieceLocations = new HashMap<>();


        //initializing grid with location initializers
        //if no player is specified pieces will be assigned to players incrementing at 0
        if(this.gameInitializer.getLocationInitializers() != null) {
            AtomicInteger piecePlayerAssignIndex = new AtomicInteger(0);
            System.out.println(Arrays.toString(this.gameInitializer.getLocationInitializers()));
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
    }

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

    @Override
    public GameStatus move(Coordinate from, Coordinate to) {
        //defaulting the status data list
        Map<GameStatusManager.GameStatusKeys, String> gameStatusData = GameStatusManager.generateEmptyStatusMap();

        EscapePiece attacker = getPieceAt(from);
        Coordinate attackerCoordinate = null;

        //no piece to move, no change in game status
        if(attacker == null) {
            gameStatuses.add(GameStatusManager.createNewGameStatus(gameStatusData, from));
            return gameStatuses.get(gameStatuses.size() - 1);
        }
        //find coordinate in the map
        else{
            for(Coordinate coordinate : pieceLocations.keySet()){
                if(coordinate.equals(from)) {
                    attackerCoordinate = coordinate;
                    break;
                }
            }
        }

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

        if(defender == null){
            gameStatusData = GameStatusManager.verifyMove(attackerAttributes, attackerPattern, from, to);
            if(Boolean.parseBoolean(gameStatusData.get(is_valid_move))){
                pieceLocations.remove(attackerCoordinate);
                pieceLocations.put(to, attacker);
            }
            gameStatuses.add(GameStatusManager.createNewGameStatus(gameStatusData, to));
            return gameStatuses.get(gameStatuses.size() - 1);
        }
        else{
            //if there is no value attributes for the attacker or defender: both pieces die
            //if there is no value attribute for the attacker: defender wins if value is not negative
            //if there is no value for defender: attacker wins
            for(Coordinate coordinate : pieceLocations.keySet()){
                if(coordinate.equals(to)) {
                    defenderCoordinate = coordinate;
                    break;
                }
            }
            gameStatusData = GameStatusManager.verifyMove(attackerAttributes, attackerPattern, defenderAttributes, from, to);
        }

        throw new EscapeException("Game Status Could not be created");
    }

    @Override
    public EscapePiece getPieceAt(Coordinate from) {
        for(Coordinate coordinate : pieceLocations.keySet()){
            if(coordinate.equals(from)) return pieceLocations.get(coordinate);
        }
        return null;
    }

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
            if (x > gameInitializer.getxMax() || x < 0 || y > gameInitializer.getyMax() || y < 0)
                throw new EscapeException("Coordinate Parameters are out of bounds");
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

    @Override
    public GameObserver addObserver(GameObserver observer) {
        return EscapeGameManager.super.addObserver(observer);
    }

    @Override
    public GameObserver removeObserver(GameObserver observer) {
        return EscapeGameManager.super.removeObserver(observer);
    }
}
