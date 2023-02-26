package escape.gui.utilities;

import escape.EscapeGameManager;
import escape.builder.EscapeGameInitializer;
import escape.builder.LocationInitializer;
import escape.gui.Offset;
import escape.required.*;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static escape.gui.utilities.GameStatusManager.GameStatusKeys.*;
import static escape.gui.Screen.DEFAULT_LOAD_TIME;

public class BoardManager implements EscapeGameManager<Coordinate> {
    public static final double MAX_TILE_WIDTH = 25;
    public static final double MAX_TILE_HEIGHT = 25;
    private final List<GameObserver> gameObservers;
    private final List<GameStatus> gameStatuses;
    private final List<Location> gameLocations;
    private final EscapeGameInitializer gameInitializer;
    public BoardManager(EscapeGameInitializer initializer){
        this.gameInitializer = initializer;
        this.gameObservers = new ArrayList<>();
        this.gameStatuses = new ArrayList<>();
        this.gameLocations = new ArrayList<>();
        for(LocationInitializer location : this.gameInitializer.getLocationInitializers()){
            Coordinate locationCoordinate = makeCoordinate(location.getX(), location.getY());
            this.gameLocations.add(new Location(locationCoordinate, new EscapePiece() {
                @Override
                public PieceName getName() {
                    return location.getPieceName();
                }
                @Override
                public String getPlayer() {
                    return location.getPlayer();
                }
            }, location.getLocationType()));
        }
    }
    public static Group drawTable(int rows, int cols, double width, double height, Map<String, String> data){
        Group group = new Group();
        Offset offsets = new Offset(0,0, width, height);

        Rectangle background = new Rectangle(offsets.getWidth(), offsets.getHeight());
        background.setId("background");
        background.setFill(Color.rgb(0,0,0,0));
        background.setStroke(Color.rgb(0,0,0,1));
        background.setStrokeWidth(3);
        background.setArcWidth(25);
        background.setArcHeight(25);
        group.getChildren().add(background);

        for(int i = 1; i < rows; i++){
            offsets.setY((offsets.getHeight() / rows) * i);
            Line l = new Line(offsets.getX(), offsets.getY(), offsets.getWidth(), offsets.getY());
            l.setStroke(Color.rgb(0,0,0,1));
            l.setStrokeWidth(1);
            l.setId("horizontalLine" + i);
            group.getChildren().add(l);
        }
        offsets.setY(0);
        for(int j = 1; j < cols; j++){
            offsets.setX((offsets.getWidth() / cols) * j);
            Line l = new Line(offsets.getX(), offsets.getY(), offsets.getX(), offsets.getHeight());
            l.setStroke(Color.rgb(0,0,0,1));
            l.setStrokeWidth(1);
            l.setId("verticalLine" + j);
            group.getChildren().add(l);
        }
        offsets.setX(0);

        Button[][] gameIcon = new Button[rows][cols];
        for(int col = 0, count = 0; col < cols && count < data.size(); col++, count++){
            for(int row = 0; row < rows && count < data.size(); row++, count++){
                //TODO: read the coord type and put it in the number system
                Button b = new Button(String.valueOf(count));
                b.setFont(new Font(12));
                b.setId(data.keySet().stream().toList().get(count));
                int configNumber = count;
                b.setOnAction(event -> AppStateManager.launchGame(data.get("EscapeConfig"+ configNumber)));
                gameIcon[row][col] = b;
                group.getChildren().add(b);
            }
        }

        new Thread(() -> {
            try{
                Thread.sleep(DEFAULT_LOAD_TIME);
                for(int row = 0; row < rows; row++){
                    for(int col = 0; col < cols; col++){
                        if(gameIcon[col][row] == null) break;
                        gameIcon[col][row].setLayoutX(((offsets.getWidth() / cols) - gameIcon[col][row].getWidth()) / 2 + ((offsets.getWidth() / cols) * col));
                        gameIcon[col][row].setLayoutY(((offsets.getHeight() / rows) - gameIcon[col][row].getHeight()) / 2 + ((offsets.getHeight() / rows) * row));
                    }
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }).start();

        return group;
    }
    public static Group drawGrid(int maxX, int maxY){
        Group group = new Group();

        return group;
    }
    public static Group drawLocation(LocationInitializer location){
        Group group = new Group();

        return group;
    }

    @Override
    public GameStatus move(Coordinate from, Coordinate to) {
        //defaulting the data list
        Map<GameStatusManager.GameStatusKeys, String> gameStatusData = new HashMap<>();
        gameStatusData.put(is_valid_move, "false");
        gameStatusData.put(is_more_information, "false");
        gameStatusData.put(get_move_result,"NONE");

        EscapePiece attacker = getPieceAt(from);

        //no piece to move, no change in game status
        if(attacker == null) {
            gameStatuses.add(GameStatusManager.createNewGameStatus(gameStatusData, from));
            return gameStatuses.get(gameStatuses.size() - 1);
        }

        EscapePiece defender = getPieceAt(to);

        PieceAttribute[] attackerAttributes = null;
        PieceAttribute[] defenderAttributes = null;

        EscapePiece.MovementPattern attackerPattern = null;
        EscapePiece.MovementPattern defenderPattern = null;

        for(PieceTypeDescriptor descriptor : gameInitializer.getPieceTypes()){
            if(descriptor.getPieceName() == attacker.getName()) {
                attackerAttributes = descriptor.getAttributes();
                attackerPattern = descriptor.getMovementPattern();
            }
            else if(defender != null && descriptor.getPieceName().equals(defender.getName())){
                defenderAttributes = descriptor.getAttributes();
                defenderPattern = descriptor.getMovementPattern();
            }
        }

        for(Location location : gameLocations){
            if(location.getCoordinate().equals(from) && defender == null){
                location.updateEscapePiece(null);
                for(Location newLocation : gameLocations){
                    if(newLocation.getCoordinate().equals(to)){
                        newLocation.updateEscapePiece(attacker);
                        Map<GameStatusManager.GameStatusKeys, String> observerData = GameObserverManager.verifyMove(gameObservers, attackerAttributes, attackerPattern);
                        gameStatusData.put(is_valid_move, observerData.get(is_valid_move));
                        gameStatusData.put(is_more_information, observerData.get(is_more_information));
                        gameStatusData.put(get_move_result, observerData.get(get_move_result));
                        gameStatuses.add(GameStatusManager.createNewGameStatus(gameStatusData, to));
                    }
                }
            }
        }

        return EscapeGameManager.super.move(from, to);
    }

    @Override
    public EscapePiece getPieceAt(Coordinate coordinate) {
        for(Location location : gameLocations){
            if(location.getCoordinate().equals(coordinate)) return location.getEscapePiece();
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
