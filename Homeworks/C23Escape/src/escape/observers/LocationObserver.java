package escape.observers;

import escape.required.Coordinate;
import escape.required.GameObserver;
import escape.required.LocationType;

public class LocationObserver implements GameObserver {
    private final Coordinate coordinate;
    private final LocationType locationType;

    /**
     * Location Observer Class
     * sort of a redundant class made out of confusion and is now to0 late to remove
     * @param coordinate of the location to be 'observed'
     * @param locationType the type of location
     */
    public LocationObserver(Coordinate coordinate, LocationType locationType){
        this.coordinate = coordinate;
        this.locationType = locationType;
    }

    /**
     * Getter Method for the Coordinate
     * @return the coordinate that is being observed
     */
    public Coordinate getCoordinate(){
        return this.coordinate;
    }

    /**
     * Getter method for the location type
     * @return the location type
     */
    public LocationType locationType(){
        return this.locationType;
    }

    /**
     * @deprecated Notify methods are not used since this class is more used as a data class rather than a observer
     * @param message
     */
    @Override
    @Deprecated
    public void notify(String message) {
        GameObserver.super.notify(message);
    }

    @Override
    @Deprecated
    public void notify(String message, Throwable cause) {
        GameObserver.super.notify(message, cause);
    }
}
