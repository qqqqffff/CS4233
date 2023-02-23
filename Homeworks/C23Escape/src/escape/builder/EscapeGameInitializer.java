/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright ©2016-2023 Gary F. Pollice
 *******************************************************************************/

package escape.builder;

import escape.required.Coordinate.*;
import escape.required.EscapeException;
import escape.required.PieceTypeDescriptor;
import escape.required.RuleDescriptor;

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * An example of reading a game configuration file and storing the information in this
 * data object. Using this object, you can get the information needed to create your
 * game.
 * 
 * MODIFIABLE: YES
 * MOVEABLE: NO
 * REQUIRED: NO
 * 
 * @see EscapeGameBuilder#makeGameManager()
 */
@XmlRootElement
public class EscapeGameInitializer {
	private CoordinateType coordinateType;
	
	// Board items
	private int xMax, yMax;
	private LocationInitializer[] locationInitializers;
	
	// Piece items
	private PieceTypeDescriptor[] pieceTypes;
	
	// Rule items
	private RuleDescriptor[] rules;

	// Players
	private String[] players;
    
    public EscapeGameInitializer() {
		locationInitializers = new LocationInitializer[]{};
		pieceTypes = new PieceTypeDescriptor[]{};
		rules = new RuleDescriptor[]{};
		players = new String[]{};
        // Needed for JAXB
    }

    /**
     * @return the coordinateType
     */
    public CoordinateType getCoordinateType()
    {
        return coordinateType;
    }

    /**
     * @param coordinateType the coordinateType to set
     */
    public void setCoordinateType(CoordinateType coordinateType) {
		if(coordinateType == null) throw new EscapeException("Coordinate Type is null");
        this.coordinateType = coordinateType;
    }

	/**
	 * @return the xMax
	 */
	public int getxMax()
	{
		return xMax;
	}

	/**
	 * @param xMax the xMax to set
	 */
	public void setxMax(int xMax)
	{
		this.xMax = xMax;
	}

	/**
	 * @return the yMax
	 */
	public int getyMax()
	{
		return yMax;
	}

	/**
	 * @param yMax the yMax to set
	 */
	public void setyMax(int yMax)
	{
		this.yMax = yMax;
	}

	/**
	 * @return the locationInitializers
	 */
	public LocationInitializer[] getLocationInitializers()
	{
		return locationInitializers;
	}

	/**
	 * @param locationInitializers the locationInitializers to set
	 */
	public void setLocationInitializers(LocationInitializer ... locationInitializers)
	{
		this.locationInitializers = locationInitializers;
	}

	public void addLocationInitializer(LocationInitializer ... locationInitializer){
		LocationInitializer[] locationInitializers = new LocationInitializer[this.locationInitializers.length + locationInitializer.length];
		System.arraycopy(this.locationInitializers, 0, locationInitializers, 0, this.locationInitializers.length);
		System.arraycopy(locationInitializer, 0, locationInitializers, this.locationInitializers.length, locationInitializer.length);
		this.locationInitializers = locationInitializers;
	}
	public void addPlayers(String ... player){
		String[] players = new String[this.players.length + player.length];
		System.arraycopy(this.players,0, players,0,this.players.length);
		System.arraycopy(player,0,players,this.players.length,player.length);
		this.players = players;
	}
	public void addRules(RuleDescriptor ... rule){
		RuleDescriptor[] rules = new RuleDescriptor[this.rules.length + rule.length];
		System.arraycopy(this.rules,0, rules,0, this.rules.length);
		System.arraycopy(rule,0, rules, this.rules.length, rule.length);
		this.rules = rules;
	}
	public void addPieceTypes(PieceTypeDescriptor ... pieceType){
		PieceTypeDescriptor[] pieceTypes = new PieceTypeDescriptor[this.pieceTypes.length + pieceType.length];
		System.arraycopy(this.pieceTypes,0, pieceTypes,0, this.pieceTypes.length);
		System.arraycopy(pieceType,0,pieceTypes,this.pieceTypes.length, pieceType.length);
		this.pieceTypes = pieceTypes;
	}

	/**
	 * @return the types
	 */
	public PieceTypeDescriptor[] getPieceTypes()
	{
		return pieceTypes;
	}

	/**
	 * @param types the types to set
	 */
	public void setPieceTypes(PieceTypeDescriptor ... types)
	{
		this.pieceTypes = types;
	}

	/**
	 * @return the rules
	 */
	public RuleDescriptor[] getRules()
	{
		return rules;
	}

	/**
	 * @param rules the rules to set
	 */
	public void setRules(RuleDescriptor[] rules)
	{
		this.rules = rules;
	}

	/**
	 * @return players
	 */
	public String[] getPlayers(){ return this.players; }

	/**
	 * @param players to set
	 */
	public void setPlayers(String[] players){
		this.players = players;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "EscapeGameInitializer [xMax=" + xMax + ", yMax=" + yMax
		    + ", coordinateType=" + coordinateType + ", locationInitializers="
		    + Arrays.toString(locationInitializers) + ", types="
		    + Arrays.toString(pieceTypes) + "]";
	}
	
}
