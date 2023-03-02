package escape.observers;

import escape.required.Coordinate;
import escape.required.EscapePiece;
import escape.required.GameObserver;
import escape.utilities.GameManager;

public class PieceObserver implements GameObserver {
    private Coordinate coordinate;
    private final EscapePiece piece;
    private int pieceHealth;
    public PieceObserver(Coordinate coordinate, EscapePiece piece, int pieceHealth){
        this.coordinate = coordinate;
        this.piece = piece;
        this.pieceHealth = pieceHealth;
    }

    public Coordinate getCoordinate(){
        return this.coordinate;
    }
    public void setCoordinate(Coordinate newCoordinate){
        this.coordinate = newCoordinate;
    }

    public EscapePiece getPiece(){
        return this.piece;
    }

    public int getPieceHealth(){
        return this.pieceHealth;
    }
    public void setPieceHealth(int newHealth){
        this.pieceHealth = newHealth;
    }
    @Override
    public void notify(String message) {
        String possessive = piece.getPlayer().charAt(piece.getPlayer().length() - 1) == 's' ? "' " : "'s ";
        System.out.println(this.piece.getPlayer() + possessive + this.piece.getName() + " " + message);
    }

    @Override
    public void notify(String message, Throwable cause) {
        String possessive = piece.getPlayer().charAt(piece.getPlayer().length() - 1) == 's' ? "' " : "'s ";
        System.out.println("Error - " + cause.getMessage() + ": "  + this.piece.getPlayer() + possessive + this.piece.getName() + " " + message);

    }
}
