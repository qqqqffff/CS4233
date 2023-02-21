package escape.gui.states;

import escape.builder.EscapeGameInitializer;
import escape.builder.LocationInitializer;
import escape.gui.AppState;
import escape.gui.utilities.BoardManager;
import escape.required.Coordinate;
import escape.EscapeGameManager;
import escape.required.EscapePiece;
import escape.required.GameObserver;
import escape.required.GameStatus;
import javafx.scene.Group;

import java.util.Map;

public class Game extends Group implements AppState, EscapeGameManager<Coordinate> {
    private final EscapeGameInitializer gameInitializer;
    private GameStatus currentGameStatus;
    public Game(EscapeGameInitializer gameInitializer){
        this.gameInitializer = gameInitializer;
        if(gameInitializer != null) {
            //if x = 0 -> y axis is infinite in 1D
            //if y = 0 -> x axis is infinite in 1D
            //if x & y = 0 -> infinite board in 2D
            this.getChildren().add(BoardManager.drawGrid(gameInitializer.getxMax(), gameInitializer.getyMax()));
            for (LocationInitializer location : this.gameInitializer.getLocationInitializers()) {
                this.getChildren().add(BoardManager.drawLocation(location));
            }

        }
    }

    @Override
    public GameStatus move(Coordinate from, Coordinate to) {
        return EscapeGameManager.super.move(from, to);
    }

    @Override
    public EscapePiece getPieceAt(Coordinate coordinate) {
        return EscapeGameManager.super.getPieceAt(coordinate);
    }

    @Override
    public Coordinate makeCoordinate(int x, int y) {
        if (gameInitializer.getCoordinateType() == Coordinate.CoordinateType.HEX) {
            if (x > gameInitializer.getxMax() || x < -gameInitializer.getxMax()
                    || y > gameInitializer.getyMax() || y < -gameInitializer.getyMax()) return null;
            return new Coordinate() {
                @Override
                public int getRow() {
                    return y + (x / 2);
                }

                @Override
                public int getColumn() {
                    return x;
                }
            };
        } else if (gameInitializer.getCoordinateType() == Coordinate.CoordinateType.SQUARE) {
            if (x > gameInitializer.getxMax() || x < 0 || y > gameInitializer.getyMax() || y < 0) return null;
            return new Coordinate() {
                @Override
                public int getRow() {
                    return y;
                }

                @Override
                public int getColumn() {
                    return x;
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public GameObserver addObserver(GameObserver observer) {
        return EscapeGameManager.super.addObserver(observer);
    }

    @Override
    public GameObserver removeObserver(GameObserver observer) {
        return EscapeGameManager.super.removeObserver(observer);
    }

    @Override
    public void updateLayout(Number width, Number height) {

    }

    @Override
    public void restoreData(Map<String, String> data) {

    }

    @Override
    public Map<String, String> generateData() {
        return null;
    }
}
