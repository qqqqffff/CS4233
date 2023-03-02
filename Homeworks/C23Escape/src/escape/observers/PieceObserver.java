package escape.observers;

import escape.required.Coordinate;
import escape.required.EscapePiece;
import escape.required.GameObserver;
import escape.utilities.GameManager;

public class PieceObserver implements GameObserver {
    private Coordinate coordinate;
    private final EscapePiece piece;
    private int pieceHealth;

    /**
     * Piece Observer Class
     * Much more useful observer that watches over the pieces and what they are up to
     * @param coordinate of the piece (will be updated over time)
     * @param piece the piece that is being observed
     * @param pieceHealth the health of the piece used when combat is enabled
     */
    public PieceObserver(Coordinate coordinate, EscapePiece piece, int pieceHealth){
        this.coordinate = coordinate;
        this.piece = piece;
        this.pieceHealth = pieceHealth;
    }

    /**
     * Getter Method for the coordinate
     * @return coordinate of the observer
     */
    public Coordinate getCoordinate(){
        return this.coordinate;
    }

    /**
     * Setter Method for the coordinate
     * @param newCoordinate to be updated for the observer
     */
    public void setCoordinate(Coordinate newCoordinate){
        this.coordinate = newCoordinate;
    }

    /**
     * Getter Method for the Piece that this observer is Observing
     * @return the observed escape piece
     */
    public EscapePiece getPiece(){
        return this.piece;
    }

    /**
     * Getter Method to get the pieces observed health
     * @return health of the piece
     */
    public int getPieceHealth(){
        return this.pieceHealth;
    }

    /**
     * Setter Method to update the health of the observed piece
     * @param newHealth health that will be changed to
     */
    public void setPieceHealth(int newHealth){
        this.pieceHealth = newHealth;
    }

    /**
     * Notify Method (without error)
     * Notifying the player when a game event happens
     * @param message that will be displayed to the player
     */
    @Override
    public void notify(String message) {
        String possessive = piece.getPlayer().charAt(piece.getPlayer().length() - 1) == 's' ? "' " : "'s ";
        System.out.println(this.piece.getPlayer() + possessive + this.piece.getName() + " " + message);
    }

    /**
     * Notify Method (with error)
     * @param message that will be displayed when a game event happens (most likely an error hehe)
     * @param cause error that happened
     */
    @Override
    public void notify(String message, Throwable cause) {
        String possessive = piece.getPlayer().charAt(piece.getPlayer().length() - 1) == 's' ? "' " : "'s ";
        System.out.println("Error - " + cause.getMessage() + ": "  + this.piece.getPlayer() + possessive + this.piece.getName() + " " + message);

    }
}
