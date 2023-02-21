package escape.required;

//TODO: java docify
public class Location {
    private final Coordinate coordinate;
    private EscapePiece escapePiece;
    private final LocationType locationType;
    public Location(Coordinate coordinate, EscapePiece escapePiece, LocationType locationType){
        this.coordinate = coordinate;
        this.escapePiece = escapePiece;
        this.locationType = locationType;
    }
    public Coordinate getCoordinate(){
        return this.coordinate;
    }
    public void updateEscapePiece(EscapePiece newPiece){
        this.escapePiece = newPiece;
    }
}
