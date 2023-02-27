package escape.utilities;

import escape.EscapeGameManager;
import escape.observers.LocationObserver;
import escape.required.Coordinate;
import escape.required.EscapeException;
import escape.required.EscapePiece;
import escape.required.LocationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static escape.utilities.GameManager.*;

public class MovementManager{
    protected static EscapeGameManager<Coordinate> manager;
    private MovementManager(){}
    public static List<Coordinate> generatePath(EscapePiece.MovementPattern pattern, Coordinate from, Coordinate to,
                                         boolean fly, boolean jump, boolean unblock, int distance){
        List<Coordinate> path = new ArrayList<>();
        path.add(from);

        //simple check if the piece has fly and the final location is a block (only fly pieces can stay on block locations)
        if(gameLocations.get(to) == LocationType.BLOCK && !fly) return null;


        switch(pattern){
            case DIAGONAL -> {
                // destinations that are not on the same diagonals as the original are not possible to reach
                if(((to.getRow() +to.getColumn()) % 2) != ((from.getRow() + from.getColumn()) % 2)) return null;
                return diagonalPath(path, from, to, fly, jump, unblock, distance);
            }
            case OMNI -> {

            }
            //only for square
            case ORTHOGONAL -> {

            }
            case LINEAR -> {

            }
        }
        return null;
    }

    public static List<Coordinate> diagonalPath(List<Coordinate> path, Coordinate from, Coordinate to, boolean fly, boolean jump, boolean unblock, int distance){
        if(path == null || from == null || path.size() > distance) return null;
        if(from.equals(to)) return path;
        Coordinate posX_posY, negX_posY, negX_negY, posX_negY;
        try {
            posX_posY = manager.makeCoordinate(from.getColumn() + 1, from.getRow() + 1);
        }
        catch(EscapeException ignored){
            posX_posY = null;
        }
        try {
            negX_posY = manager.makeCoordinate(from.getColumn() - 1, from.getRow() + 1);
        }
        catch(EscapeException ignored){
            negX_posY = null;
        }
        try {
            negX_negY = manager.makeCoordinate(from.getColumn() - 1, from.getRow() - 1);
        }
        catch(EscapeException ignored){
            negX_negY = null;
        }
        try{
            posX_negY = manager.makeCoordinate(from.getColumn() + 1, from.getRow() - 1);
        }
        catch(EscapeException ignored){
            posX_negY = null;
        }

        //pos x, pos y
        if(to.getColumn() > from.getColumn() && to.getRow() > from.getRow()){
            if(!checkCoordinate(posX_posY, jump, unblock)){
                List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, negX_posY, negX_negY, posX_negY);
                return determineMinimumPath(paths);
            }
            else{
                path.add(posX_posY);
                return diagonalPath(path, posX_posY, to, fly, coordinateRequiresJump(posX_posY, jump), unblock, distance);
            }
        }
        //neg x, pos y
        else if(to.getColumn() < from.getColumn() && to.getRow() > from.getRow()){
            if(!checkCoordinate(negX_posY, jump, unblock)){
                List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, negX_negY, posX_negY);
                return determineMinimumPath(paths);
            }
            else{
                path.add(negX_posY);
                return diagonalPath(path, negX_posY, to, fly, coordinateRequiresJump(negX_posY, jump), unblock, distance);
            }
        }
        //neg x, neg y
        else if(to.getColumn() < from.getColumn() && to.getRow() < from.getRow()){
            if(!checkCoordinate(negX_negY, jump, unblock)){
                List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, negX_posY, posX_negY);
                return determineMinimumPath(paths);
            }
            else{
                path.add(negX_negY);
                return diagonalPath(path, negX_negY, to, fly, coordinateRequiresJump(negX_negY, jump), unblock, distance);
            }
        }
        //pos x, neg y
        else if(to.getColumn() > from.getColumn() && to.getRow() < from.getRow()){
            if(!checkCoordinate(posX_negY, jump, unblock)){
                List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, negX_posY, negX_negY);
                return determineMinimumPath(paths);
            }
            else{
                path.add(posX_negY);
                return diagonalPath(path, posX_negY, to, fly, coordinateRequiresJump(posX_negY, jump), unblock, distance);
            }
        }

        //if destination and origin are in the same row/col
        else if(to.getRow() > from.getRow() && to.getColumn() == from.getColumn()){
            List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, negX_posY);
            return determineMinimumPath(paths);
        }
        else if(to.getRow() < from.getRow() && to.getColumn() == from.getColumn()){
            List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, negX_negY, posX_negY);
            return determineMinimumPath(paths);
        }
        else if(to.getRow() == from.getRow() && to.getColumn() > from.getColumn()){
            List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, posX_negY);
            return determineMinimumPath(paths);
        }
        else if(to.getRow() == from.getRow() && to.getColumn() < from.getColumn()){
            List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, negX_posY, negX_negY);
            return determineMinimumPath(paths);
        }
        return path;
    }


    public static List<List<Coordinate>> splitDiagonalPaths(List<Coordinate> originalPath, Coordinate to,
                                                     boolean fly, boolean jump, boolean unblock, int distance, Coordinate ... coordinates){
        if(originalPath == null) return null;
        List<List<Coordinate>> paths = new ArrayList<>();
        for(Coordinate coordinate : coordinates){
            boolean notContained = true;
            for(Coordinate originPathCoordinates : originalPath){
                if(originPathCoordinates.equals(coordinate)){
                    notContained = false;
                    break;
                }
            }
            if(coordinate != null && notContained && checkCoordinate(coordinate, jump, unblock)) {
                List<Coordinate> newPath = new ArrayList<>(List.copyOf(originalPath));
                newPath.add(coordinate);
                paths.add(diagonalPath(newPath, coordinate, to, fly, jump, unblock, distance));
            }
        }
        return paths;
    }

    public static List<Coordinate> determineMinimumPath(List<List<Coordinate>> paths){
        Map<Integer, List<Coordinate>> lengthMap = new HashMap<>();
        paths.forEach(path -> lengthMap.put(path.size(), path));
        int minimumSize = paths.get(0).size();

        for(int size : lengthMap.keySet()){
            if(size < minimumSize) minimumSize = size;
        }

        return lengthMap.get(minimumSize);
    }

    public static boolean checkCoordinate(Coordinate from, boolean jump, boolean unblock){
        //check location data
        LocationType locationType = DEFAULT_LOCATION_TYPE;
        for(LocationObserver observer : locationObservers){
            if(observer.getCoordinate().equals(from))  {
                locationType = observer.locationType();
                break;
            }
        }
        if(locationType == LocationType.CLEAR) return true;
        if (locationType == LocationType.BLOCK) {
            if (unblock) return true;
            return jump;
        }
        for(Coordinate coordinate : pieceLocations.keySet()){
            if(coordinate.equals(from)){
                return jump;
            }
        }
        return true;
    }

    /**
     * Coordinate Requires Jump Method:
     * returns whether the piece will still have the jump attribute
     * @param from to be checked
     * @return the updated jump boolean
     */
    public static boolean coordinateRequiresJump(Coordinate from, boolean jump){
        if(!jump) return false;
        LocationType locationType = DEFAULT_LOCATION_TYPE;
        for(LocationObserver observer : locationObservers){
            if(observer.getCoordinate().equals(from))  {
                locationType = observer.locationType();
                break;
            }
        }
        if (locationType == LocationType.BLOCK) {
            return false;
        }
        //if player encountered jump will be used
        for(Coordinate coordinate : pieceLocations.keySet()){
            if(coordinate.equals(from)){
                return false;
            }
        }
        return true;
    }
}
