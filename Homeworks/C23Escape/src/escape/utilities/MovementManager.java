package escape.utilities;

import escape.EscapeGameManager;
import escape.observers.LocationObserver;
import escape.required.Coordinate;
import escape.required.EscapeException;
import escape.required.EscapePiece;
import escape.required.LocationType;

import java.util.*;

import static escape.utilities.GameManager.*;

public class MovementManager{
    protected static EscapeGameManager<Coordinate> manager;
    private MovementManager(){}

    /**
     * Path Generation Function
     * Generates a path depending on the parameters passed (duh)
     * notes:
     *     path will use jump on the first instance of an obstacle
     *     path avoids confrontation (other players) unless confrontation occurs on to coordinate
     * @param pattern Movement pattern to follow
     * @param from origin coordinate
     * @param to destination coordinate
     * @param fly boolean attribute
     * @param jump boolean attribute
     * @param unblock boolean attribute
     * @param distance int attribute
     * @return a path (list of coordinates (steps) inclusive of origin and destination) or null if path is not possible
     */
    //todo: change the algorithm(s) to split another path for avoiding the obstacle vs using jump
    //todo: update algorithms for non-square coordinates
    public static List<Coordinate> generatePath(EscapePiece.MovementPattern pattern, Coordinate from, Coordinate to,
                                         boolean fly, boolean jump, boolean unblock, int distance){
        if(from == null || to == null) return null;
        List<Coordinate> path = new ArrayList<>();
        path.add(from);

        //simple check if the piece has fly and the final location is a block (only fly pieces can stay on block locations)
        if(gameLocations.get(to) == LocationType.BLOCK && !fly) return null;


        switch(pattern){
            //only for square coordinates
            case DIAGONAL -> {
                // destinations that are not on the same diagonals as the original are not possible to reach
                if(((to.getRow() + to.getColumn()) % 2) != ((from.getRow() + from.getColumn()) % 2)) return null;
                return diagonalPath(path, from, to, fly, jump, unblock, distance);
            }
            case OMNI -> {
                return omniPath(path, from, to, fly, jump, unblock, distance);
            }
            //only for square
            case ORTHOGONAL -> {
                return orthogonalPath(path, from, to, fly, jump, unblock, distance);
            }
            case LINEAR -> {
                int direction = -1;
                //pos x
                if(to.getRow() == from.getRow() && to.getColumn() > from.getColumn()) direction = 1;
                //pos x pos y diag
                else if(((to.getColumn() - from.getColumn()) == (to.getRow() - from.getRow())) && to.getColumn() > from.getColumn() && to.getRow() > from.getRow()) direction = 2;
                //pos y
                else if(to.getColumn() == from.getColumn() && to.getRow() > from.getRow()) direction = 3;
                //neg x pos y diag
                else if(((from.getColumn() - to.getColumn()) == (to.getRow() - from.getRow())) && to.getColumn() < from.getColumn() && to.getRow() > from.getRow()) direction = 4;
                //neg x
                else if(to.getRow() == from.getRow() && to.getColumn() < from.getColumn()) direction = 5;
                //neg x neg y diag
                else if(((to.getColumn() - from.getColumn()) == (to.getRow() - from.getRow())) && to.getColumn() < from.getColumn() && to.getRow() < from.getRow()) direction = 6;
                //neg y
                else if(to.getColumn() == from.getColumn() && to.getRow() < from.getRow()) direction = 7;
                //pos x neg y diag
                else if(((from.getColumn() - to.getColumn()) == (to.getRow() - from.getRow())) && to.getColumn() > from.getColumn() && to.getRow() < from.getRow()) direction = 8;
                //destinations that are not on the +/- diagonal or horizontal / vertical lines are not possible to reach
                if(direction == -1) return null;
                return linearPath(path, from, to, fly, jump, unblock, distance, direction);
            }
        }
        return null;
    }

    /**
     * Diagonal Path Generator Algorithm
     * Recursive breadth first search method to determine the shortest path using only diagonal movement options
     * for specific notes view path generation method
     * @param path a current list of coordinates representing the path
     * @param from the current coordinate
     * @param to destination coordinate
     * @param fly boolean determining relevant attribute
     * @param jump boolean determining relevant attribute
     * @param unblock boolean determining relevant attribute
     * @param distance int determining relevant attribute
     * @return a list containing all the coordinates of the path, null if no path is possible
     */
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
            if(!checkCoordinate(posX_posY, to, jump, unblock) && posX_posY != to){
                List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, negX_posY, negX_negY, posX_negY);
                return computeMinimumPath(paths);
            }
            else{
                path.add(posX_posY);
                return diagonalPath(path, posX_posY, to, fly, coordinateRequiresJump(posX_posY, jump), unblock, distance);
            }
        }
        //neg x, pos y
        else if(to.getColumn() < from.getColumn() && to.getRow() > from.getRow()){
            if(!checkCoordinate(negX_posY, to, jump, unblock) && negX_posY != to){
                List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, negX_negY, posX_negY);
                return computeMinimumPath(paths);
            }
            else{
                path.add(negX_posY);
                return diagonalPath(path, negX_posY, to, fly, coordinateRequiresJump(negX_posY, jump), unblock, distance);
            }
        }
        //neg x, neg y
        else if(to.getColumn() < from.getColumn() && to.getRow() < from.getRow()){
            if(!checkCoordinate(negX_negY, to, jump, unblock) && negX_negY != to){
                List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, negX_posY, posX_negY);
                return computeMinimumPath(paths);
            }
            else{
                path.add(negX_negY);
                return diagonalPath(path, negX_negY, to, fly, coordinateRequiresJump(negX_negY, jump), unblock, distance);
            }
        }
        //pos x, neg y
        else if(to.getColumn() > from.getColumn() && to.getRow() < from.getRow()){
            if(!checkCoordinate(posX_negY, to, jump, unblock) && posX_negY != to){
                List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, negX_posY, negX_negY);
                return computeMinimumPath(paths);
            }
            else{
                path.add(posX_negY);
                return diagonalPath(path, posX_negY, to, fly, coordinateRequiresJump(posX_negY, jump), unblock, distance);
            }
        }

        //if destination and origin are in the same row/col
        else if(to.getRow() > from.getRow() && to.getColumn() == from.getColumn()){
            List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, negX_posY);
            return computeMinimumPath(paths);
        }
        else if(to.getRow() < from.getRow() && to.getColumn() == from.getColumn()){
            List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, negX_negY, posX_negY);
            return computeMinimumPath(paths);
        }
        else if(to.getRow() == from.getRow() && to.getColumn() > from.getColumn()){
            List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, posX_posY, posX_negY);
            return computeMinimumPath(paths);
        }
        else if(to.getRow() == from.getRow() && to.getColumn() < from.getColumn()){
            List<List<Coordinate>> paths = splitDiagonalPaths(path, to, fly, jump, unblock, distance, negX_posY, negX_negY);
            return computeMinimumPath(paths);
        }
        return path;
    }


    /**
     * Diagonal Path Splitter Function
     * Helper function for diagonalPath function
     * When more than one move is possible compute a diagonalPath(args) from the main path
     * @param originalPath original path that will be deviated from
     * @param coordinates a or list of coordinates to make new paths down
     * other params are inherited for the diagonalPath recursive calls
     * @return a list of a list of possible paths
     */
    public static List<List<Coordinate>> splitDiagonalPaths(List<Coordinate> originalPath, Coordinate to,
                                                     boolean fly, boolean jump, boolean unblock, int distance, Coordinate ... coordinates){
        if(originalPath == null || coordinates == null || coordinates.length == 0) return null;
        List<List<Coordinate>> paths = new ArrayList<>();
        for(Coordinate coordinate : coordinates){
            if(coordinate == null) continue;
            boolean notContained = true;
            for(Coordinate originPathCoordinates : originalPath){
                if(originPathCoordinates.equals(coordinate)){
                    notContained = false;
                    break;
                }
            }
            if(notContained && checkCoordinate(coordinate, to, jump, unblock)) {
                List<Coordinate> newPath = new ArrayList<>(List.copyOf(originalPath));
                newPath.add(coordinate);
                paths.add(diagonalPath(newPath, coordinate, to, fly, jump, unblock, distance));
            }
        }
        return paths;
    }

    /**
     * Orthogonal Path Generation Method
     * Recursive breadth first search method to generate the shortest path using only orthogonal movement options
     * for specific notes view path generation method
     * @param path a current list of coordinates representing the path
     * @param from the current coordinate
     * @param to destination coordinate
     * @param fly boolean determining relevant attribute
     * @param jump boolean determining relevant attribute
     * @param unblock boolean determining relevant attribute
     * @param distance int determining relevant attribute
     * @return a list containing all the coordinates of the path, null if no path is possible
     */
    public static List<Coordinate> orthogonalPath(List<Coordinate> path, Coordinate from, Coordinate to, boolean fly, boolean jump, boolean unblock, int distance){
        if(path == null || path.size() > distance || from == null) return null;
        if(from.equals(to)) return path;
        Coordinate posX, negX, posY, negY;
        try{
            posX = manager.makeCoordinate(from.getColumn() + 1, from.getRow());
        }
        catch(EscapeException ignored){
            posX = null;
        }
        try{
            negX = manager.makeCoordinate(from.getColumn() - 1, from.getRow());
        }
        catch(EscapeException ignored){
            negX = null;
        }
        try{
            posY = manager.makeCoordinate(from.getColumn(),from.getRow() + 1);
        }
        catch(EscapeException ignored){
            posY = null;
        }
        try{
            negY = manager.makeCoordinate(from.getColumn(),from.getRow() - 1);
        }
        catch(EscapeException ignored){
            negY = null;
        }
        //move towards whichever coordinate difference is greater (will prefer x movement if equal)
        //positive val -> move in positive direction
        //negative val -> move in negative direction
        int xDifference = to.getColumn() - from.getColumn();
        int yDifference = to.getRow() - from.getRow();
        //true means move in x direction false means y direction
        boolean direction = Math.abs(xDifference) >= Math.abs(yDifference);
        if(direction){
            //posX
            if(xDifference > 0){
                if(!checkCoordinate(posX, to, jump, unblock) && posX != to){
                    List<List<Coordinate>> paths = splitOrthogonalPaths(path, to, fly, jump, unblock, distance, negX, posY, negY);
                    return computeMinimumPath(paths);
                }
                else{
                    path.add(posX);
                    return orthogonalPath(path, posX, to, fly, coordinateRequiresJump(posX, jump), unblock, distance);
                }
            }
            //negX
            else if(xDifference < 0){
                if(!checkCoordinate(negX, to, jump, unblock) && negX != to){
                    List<List<Coordinate>> paths = splitOrthogonalPaths(path, to, fly, jump, unblock, distance, posX, posY, negY);
                    return computeMinimumPath(paths);
                }
                else{
                    path.add(negX);
                    return orthogonalPath(path, negX, to, fly, coordinateRequiresJump(negX, jump), unblock, distance);
                }
            }
        }
        else {
            //posY
            if(yDifference > 0){
                if(!checkCoordinate(posY, to, jump, unblock) && posY != to){
                    List<List<Coordinate>> paths = splitOrthogonalPaths(path, to, fly, jump, unblock, distance, posX, negX, negY);
                    return computeMinimumPath(paths);
                }
                else{
                    path.add(posY);
                    return orthogonalPath(path, posY, to, fly, coordinateRequiresJump(posY, jump), unblock, distance);
                }
            }
            //negY
            else if(yDifference < 0){
                if(!checkCoordinate(negY, to, jump, unblock)){
                    List<List<Coordinate>> paths = splitOrthogonalPaths(path, to, fly, jump, unblock, distance, posX, negX, posY);
                    return computeMinimumPath(paths);
                }
                else{
                    path.add(negY);
                    return orthogonalPath(path, negY, to, fly, coordinateRequiresJump(negY, jump), unblock, distance);
                }

            }
        }
        return path;
    }

    /**
     * Orthogonal Path Splitter Function
     * Helper function for orthogonalPath function
     * When more than one move is possible compute a diagonalPath(args) from main path
     * @param originalPath a list containing all the original coordinates main path
     * @param coordinates a or list of coordinates to be split off from
     * other params are inherited for the orthogonalPath recursive calls
     * @return a list of lists containing all the coordinates of the path, null if no paths are possible
     */
    public static List<List<Coordinate>> splitOrthogonalPaths(List<Coordinate> originalPath, Coordinate to,
                                              boolean fly, boolean jump, boolean unblock, int distance, Coordinate ... coordinates){
        if(originalPath == null || coordinates == null || coordinates.length == 0) return null;
        List<List<Coordinate>> paths = new ArrayList<>();
        for(Coordinate coordinate : coordinates){
            if(coordinate == null) continue;
            boolean notContained = true;
            for(Coordinate originPathCoordinates : originalPath){
                if(originPathCoordinates.equals(coordinate)){
                    notContained = false;
                    break;
                }
            }
            if(notContained && checkCoordinate(coordinate, to, jump, unblock)){
                List<Coordinate> newPath = new ArrayList<>(List.copyOf(originalPath));
                newPath.add(coordinate);
                paths.add(orthogonalPath(newPath, coordinate, to, fly, jump, unblock, distance));
            }
        }
        return paths;
    }

    /**
     * Linear Path Generator Function
     * Recursive path generator function to generate paths from origin to destination that are collinear
     * for specific notes view path generation method
     * @param path a current list of coordinates representing the path
     * @param from the current coordinate
     * @param to destination coordinate
     * @param fly boolean determining relevant attribute
     * @param jump boolean determining relevant attribute
     * @param unblock boolean determining relevant attribute
     * @param distance int determining relevant attribute
     * @param direction int determining the direction view path generation method for more info
     * @return a list containing all the coordinates of the path, null if no path is possible
     */
    public static List<Coordinate> linearPath(List<Coordinate> path, Coordinate from, Coordinate to, boolean fly, boolean jump, boolean unblock, int distance, int direction){
        if(path == null || path.size() > distance || from == null) return null;
        if(from.equals(to)) return path;
        Coordinate nextMove = null;
        //pos x
        if(direction == 1){
            try{
                nextMove = manager.makeCoordinate(from.getColumn() + 1, from.getRow());
            }catch(EscapeException ignored){ }
        }
        //pos x pos y
        else if(direction == 2){
            try{
                nextMove = manager.makeCoordinate(from.getColumn() + 1, from.getRow() + 1);
            }catch(EscapeException ignored){ }
        }
        //pos y
        else if(direction == 3){
            try{
                nextMove = manager.makeCoordinate(from.getColumn(), from.getRow() + 1);
            }catch(EscapeException ignored){ }
        }
        //neg x pos y
        else if(direction == 4){
            try{
                nextMove = manager.makeCoordinate(from.getColumn() - 1, from.getRow() + 1);
            }catch(EscapeException ignored){ }
        }
        //neg x
        else if(direction == 5){
            try{
                nextMove = manager.makeCoordinate(from.getColumn() - 1, from.getRow());
            }catch(EscapeException ignored){ }
        }
        //neg x neg y
        else if(direction == 6){
            try{
                nextMove = manager.makeCoordinate(from.getColumn() - 1, from.getRow() - 1);
            }catch(EscapeException ignored){ }
        }
        //neg y
        else if(direction == 7){
            try{
                nextMove = manager.makeCoordinate(from.getColumn(), from.getRow() - 1);
            }catch(EscapeException ignored){ }
        }
        //pos x neg y
        else if(direction == 8){
            try{
                nextMove = manager.makeCoordinate(from.getColumn() + 1, from.getRow() - 1);
            }catch(EscapeException ignored){ }
        }
        if(nextMove == null || (!checkCoordinate(nextMove, to, jump, unblock) && !fly)) return null;
        path.add(nextMove);
        return linearPath(path, nextMove, to, fly, coordinateRequiresJump(nextMove, jump), unblock, distance, direction);
    }

    /**
     * Omni Path Generator Function
     * Recursive breadth first search function to generate paths from origin to destination using best moves
     * for specific notes view path generation method
     * @param path a current list of coordinates representing the path
     * @param from the current coordinate
     * @param to destination coordinate
     * @param fly boolean determining relevant attribute
     * @param jump boolean determining relevant attribute
     * @param unblock boolean determining relevant attribute
     * @param distance int determining relevant attribute
     * @return a list containing all the coordinates of the path, null if no path is possible
     */
    public static List<Coordinate> omniPath(List<Coordinate> path, Coordinate from, Coordinate to, boolean fly, boolean jump, boolean unblock, int distance){
        if(path == null || path.size() > distance || from == null) return null;
        if(from.equals(to)) return path;
        Coordinate nextCoordinate = null;
        Coordinate posX = null, posX_posY = null, posY = null, negX_posY = null, negX = null, negX_negY = null, negY = null, posX_negY = null;
        try{
            posX = manager.makeCoordinate(from.getColumn() + 1, from.getRow());
        }catch(EscapeException ignored){ }
        try{
            posX_posY = manager.makeCoordinate(from.getColumn() + 1, from.getRow() + 1);
        }catch(EscapeException ignored){ }
        try{
            posY = manager.makeCoordinate(from.getColumn(), from.getRow() + 1);
        }catch(EscapeException ignored){ }
        try{
            negX_posY = manager.makeCoordinate(from.getColumn() - 1, from.getRow() + 1);
        }catch(EscapeException ignored){ }
        try{
            negX = manager.makeCoordinate(from.getColumn() - 1, from.getRow());
        }catch(EscapeException ignored){ }
        try{
            negX_negY = manager.makeCoordinate(from.getColumn() - 1, from.getRow() - 1);
        }catch(EscapeException ignored){ }
        try{
            negY = manager.makeCoordinate(from.getColumn(), from.getRow() - 1);
        }catch(EscapeException ignored){ }
        try{
            posX_negY = manager.makeCoordinate(from.getColumn() + 1, from.getRow() - 1);
        }catch(EscapeException ignored){ }

        //pos x pos y
        if(to.getColumn() > from.getColumn() && to.getRow() > from.getRow()) nextCoordinate = posX_posY;
        //neg x pos y
        else if(to.getColumn() < from.getColumn() && to.getRow() > from.getRow()) nextCoordinate = negX_posY;
        //neg x neg y
        else if(to.getColumn() < from.getColumn() && to.getRow() < from.getRow()) nextCoordinate = negX_negY;
        //pos x neg y
        else if(to.getColumn() > from.getColumn() && to.getRow() < from.getRow()) nextCoordinate = posX_negY;
        else{
            //move towards whichever coordinate difference is greater
            //positive val -> move in positive dir
            //negative val -> move in negative dir
            int xDifference = to.getColumn() - from.getColumn();
            int yDifference = to.getRow() - from.getRow();
            //true means move in x direction false means move in y direction
            boolean direction = Math.abs(xDifference) >= Math.abs(yDifference);
            if(direction){
                //pos x
                if(xDifference > 0) nextCoordinate = posX;
                //neg x
                else if(xDifference < 0) nextCoordinate = negX;
            }
            else{
                //pos y
                if(yDifference > 0) nextCoordinate = posY;
                //neg y
                else if(xDifference < 0) nextCoordinate = negY;
            }
        }
        if(nextCoordinate == null) return null; //this should ideally not happen lol, just making sure
        if(!checkCoordinate(nextCoordinate, to, jump, unblock)){
            if(nextCoordinate.equals(posX)){
                List<List<Coordinate>> paths = splitOmniPaths(path, to, fly, jump, unblock, distance,
                        posX_posY, posY, negX_posY, negX, negX_negY, negY, posX_negY);
                return computeMinimumPath(paths);
            }
            else if(nextCoordinate.equals(posX_posY)){
                List<List<Coordinate>> paths = splitOmniPaths(path, to, fly, jump, unblock, distance,
                        posX, posY, negX_posY, negX, negX_negY, negY, posX_negY);
                return computeMinimumPath(paths);
            }
            else if(nextCoordinate.equals(posY)){
                List<List<Coordinate>> paths = splitOmniPaths(path, to, fly, jump, unblock, distance,
                        posX_posY, posX, negX_posY, negX, negX_negY, negY, posX_negY);
                return computeMinimumPath(paths);
            }
            else if(nextCoordinate.equals(negX_posY)){
                List<List<Coordinate>> paths = splitOmniPaths(path, to, fly, jump, unblock, distance,
                        posX_posY, posY, posX, negX, negX_negY, negY, posX_negY);
                return computeMinimumPath(paths);
            }
            else if(nextCoordinate.equals(negX)){
                List<List<Coordinate>> paths = splitOmniPaths(path, to, fly, jump, unblock, distance,
                        posX_posY, posY, negX_posY, posX, negX_negY, negY, posX_negY);
                return computeMinimumPath(paths);
            }
            else if(nextCoordinate.equals(negX_negY)){
                List<List<Coordinate>> paths = splitOmniPaths(path, to, fly, jump, unblock, distance,
                        posX_posY, posY, negX_posY, negX, posX, negY, posX_negY);
                return computeMinimumPath(paths);
            }
            else if(nextCoordinate.equals(negY)){
                List<List<Coordinate>> paths = splitOmniPaths(path, to, fly, jump, unblock, distance,
                        posX_posY, posY, negX_posY, negX, negX_negY, posX, posX_negY);
                return computeMinimumPath(paths);
            }
            else if(nextCoordinate.equals(posX_negY)){
                List<List<Coordinate>> paths = splitOmniPaths(path, to, fly, jump, unblock, distance,
                        posX_posY, posY, negX_posY, negX, negX_negY, negY, posX);
                return computeMinimumPath(paths);
            }
            else return null; //also shouldnt happen
        }
        path.add(nextCoordinate);
        return omniPath(path, nextCoordinate, to, fly, coordinateRequiresJump(nextCoordinate, jump), unblock, distance);
    }

    /**
     * Omni Path Splitter Function
     * Helper function for omniPath function
     * When more than one move is possible compute a omniPath(args) from the main path
     * @param originalPath a list containing all the original coordinates of the main path
     * @param coordinates a or list of coordinates to be split off from the main path
     * other params are inherited for the orthogonal recursive calls
     * @return a list of lists containing all the coordinates of the path, null if no paths are possible
     */
    public static List<List<Coordinate>> splitOmniPaths(List<Coordinate> originalPath, Coordinate to,
                                            boolean fly, boolean jump, boolean unblock, int distance, Coordinate ... coordinates){
        if(originalPath == null || coordinates == null || coordinates.length == 0) return null;
        List<List<Coordinate>> paths = new ArrayList<>();
        for(Coordinate coordinate : coordinates){
            if(coordinate == null) continue;
            boolean notContained = true;
            for(Coordinate originPathCoordinates : originalPath){
                if(originPathCoordinates.equals(coordinate)){
                    notContained = false;
                    break;
                }
            }
            if(notContained && checkCoordinate(coordinate, to, jump, unblock)){
                List<Coordinate> newPath = new ArrayList<>(List.copyOf(originalPath));
                newPath.add(coordinate);
                paths.add(omniPath(newPath, coordinate, to, fly, jump, unblock, distance));
            }
        }
        return paths;
    }
    /**
     * Compute Minimum Path Method
     * Minimizing function to determine which path in the list is the shortest
     * @param paths list of paths (list of a list of coordinates)
     * @return the shortest path (list of coordinates)
     */
    public static List<Coordinate> computeMinimumPath(List<List<Coordinate>> paths){
        if(paths == null || paths.size() == 0) return null;
        Map<Integer, List<Coordinate>> lengthMap = new HashMap<>();
        paths.forEach(path -> {
            if(path != null) lengthMap.put(path.size(), path);
        });

        if(lengthMap.size() == 0) return null;
        int minimumSize = Integer.MAX_VALUE;

        for(int size : lengthMap.keySet()){
            if(size < minimumSize) minimumSize = size;
        }

        return lengthMap.get(minimumSize);
    }

    /**
     * Check Coordinate Method
     * Parsing method to determine if the coordinate is passable with the given attributes
     * @param from coordinate to check
     * @param to coordinate to also check
     * @param jump boolean determining relevant attribute
     * @param unblock boolean determining relevant attribute
     * @return boolean representing if the coordinate is passable
     */
    public static boolean checkCoordinate(Coordinate from, Coordinate to, boolean jump, boolean unblock){
        //check location data
        LocationType locationType = DEFAULT_LOCATION_TYPE;
        for(LocationObserver observer : locationObservers){
            if(observer.getCoordinate().equals(from))  {
                locationType = observer.locationType();
                break;
            }
        }
        if (locationType == LocationType.BLOCK) {
            if (unblock) return true;
            return jump;
        }
        else if(locationType == LocationType.EXIT) return false;
        for(Coordinate coordinate : pieceLocations.keySet()){
            if(coordinate.equals(from)){
                return coordinate.equals(to) || jump;
            }
        }
        return true;
    }

    /**
     * Coordinate Requires Jump Method
     * returns whether the piece will still have the jump attribute
     * @param from to be checked
     * @param jump boolean determining relevant attribute
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
