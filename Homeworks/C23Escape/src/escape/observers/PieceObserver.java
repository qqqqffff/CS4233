package escape.observers;

import escape.required.Coordinate;
import escape.required.EscapePiece;
import escape.required.GameObserver;

public class PieceObserver implements GameObserver {
    private Coordinate coordinate;
    private EscapePiece piece;
    public PieceObserver(Coordinate coordinate, EscapePiece piece){
        this.coordinate = coordinate;
        this.piece = piece;
    }

    public Coordinate getCoordinate(){
        return this.coordinate;
    }

    public EscapePiece getPiece(){
        return this.piece;
    }
    @Override
    public void notify(String message) {
        GameObserver.super.notify(message);
    }

    @Override
    public void notify(String message, Throwable cause) {
        GameObserver.super.notify(message, cause);
    }
}
