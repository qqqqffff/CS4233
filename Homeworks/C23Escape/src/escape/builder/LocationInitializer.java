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
import escape.EscapePiece;
import escape.EscapePiece.PieceName;
import escape.builder.LocationType.LocationTypes;

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
public class LocationInitializer
{
	public int x, y;
	public LocationTypes locationType;
	public String player;
	public PieceName pieceName;
	
	public LocationInitializer() 
	{
	    // needed for JAXB unmarshalling
	}
	
    public LocationInitializer(int x, int y, LocationTypes locationType,
        String player, PieceName pieceName)
    {
    	this.x = x;
        this.y = y;
        this.locationType = locationType;
        this.player = player;
        this.pieceName = pieceName;
    }

    public static LocationInitializer parseLocationInitializer(JsonReader reader)throws Exception{
        if(reader == null) throw new NullPointerException("Reader is null");
        LocationInitializer locationInitializer = new LocationInitializer();
        while(reader.hasNext()){
            String key = reader.nextName();
            switch(key){
                case "x" -> locationInitializer.x = Integer.parseInt(reader.nextString());
                case "y" -> locationInitializer.y = Integer.parseInt(reader.nextString());
                case "player" -> locationInitializer.player = reader.nextString();
                case "location_type" -> locationInitializer.locationType = LocationType.parseLocationTypes(reader.nextString());
                case "piece_name" -> {
                    String value = reader.nextString();
                    locationInitializer.pieceName = EscapePiece.parsePieceName(value);
                }
            }
        }        return locationInitializer;
    }

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
