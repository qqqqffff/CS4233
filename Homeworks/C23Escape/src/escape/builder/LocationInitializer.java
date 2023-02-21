/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Copyright ©2016-2023 Gary F. Pollice
 *******************************************************************************/
package escape.builder;

import com.google.gson.stream.JsonReader;
import escape.required.Coordinate;
import escape.required.EscapePiece;
import escape.required.EscapePiece.PieceName;
import escape.required.LocationType;

/**
 * A general initializer for a board location. Since this is used
 * internally and not part of the game, we take a shortcut and make
 * the instance variables public rather than private since this class
 * is not part of the actual game implementation.
 * 
 * This file mirrors the <locationInitializers> structure in the
 * XML configuration files.
 * 
 * You do not have to use this, but are encouraged to do so. 
 * 
 * However, you do need to be able to load the appropriate named
 * data from the configuration file in order to create a game
 * correctly.
 *  
 * MODIFIABLE: YES
 * MOVEABLE: NO
 * REQUIRED: NO 
 */
public class LocationInitializer {
	final private int x, y;
    private Coordinate locationCoordinate;
	final private LocationType locationType;
	final private String player;
	final private PieceName pieceName;
	
	public LocationInitializer() {
        this.x = 0;
        this.y = 0;
        this.locationType = null;
        this.player = null;
        this.pieceName = null;
	}
	
    public LocationInitializer(int x, int y, LocationType locationType, String player, PieceName pieceName) {
    	this.x = x;
        this.y = y;
        this.locationType = locationType;
        this.player = player;
        this.pieceName = pieceName;
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public LocationType getLocationType() { return this.locationType; }
    public String getPlayer() { return this.player; }
    public PieceName getPieceName() { return this.pieceName; }
    public Coordinate getLocationCoordinate(){ return this.locationCoordinate; }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "LocationInitializer [x=" + x + ", y=" + y + ", locationType="
            + locationType + ", player=" + player + ", pieceName=" + pieceName + "]";
    }
}
