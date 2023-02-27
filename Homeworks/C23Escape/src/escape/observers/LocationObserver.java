package escape.observers;

import escape.required.Coordinate;
import escape.required.GameObserver;
import escape.required.LocationType;

public class LocationObserver implements GameObserver {
    private Coordinate coordinate;
    private LocationType locationType;
    public LocationObserver(Coordinate coordinate, LocationType locationType){
        this.coordinate = coordinate;
        this.locationType = locationType;
    }
    public Coordinate getCoordinate(){
        return this.coordinate;
    }
    public LocationType locationType(){
        return this.locationType;
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
