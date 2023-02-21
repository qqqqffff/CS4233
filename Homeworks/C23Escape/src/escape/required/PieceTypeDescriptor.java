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
package escape.required;

import com.google.gson.stream.JsonReader;
import escape.required.EscapePiece.*;

import java.util.*;

/**
 * A JavaBean that represents a complete piece type description. This file
 * is provided as an example that can be used to initialize instances of a GameManager
 * via the EscapeBuilder. You do not have to use this, but are encouraged to do so.
 *
 * However, you do need to be able to load the appropriate named data from the 
 * configuration file in order to create a game correctly.
 * 
 * MODIFIABLE: YES
 * MOVEABLE: YES
 * REQUIRED: NO
 */
public class PieceTypeDescriptor {
	private PieceName pieceName;
    private MovementPattern movementPattern;
    private PieceAttribute[] attributes;
    
    public PieceTypeDescriptor() {}

    public PieceTypeDescriptor(PieceName pieceName, MovementPattern movementPattern, PieceAttribute[] attributes){
        this.pieceName = pieceName;
        this.movementPattern = movementPattern;
        this.attributes = attributes;
    }
    
    /**
     * @return the pieceName
     */
    public PieceName getPieceName()
    {
        return pieceName;
    }
    /**
     * @param pieceName the pieceName to set
     */
    public void setPieceName(PieceName pieceName)
    {
        this.pieceName = pieceName;
    }
    /**
     * @return the movementPattern
     */
    public MovementPattern getMovementPattern()
    {
        return movementPattern;
    }
    /**
     * @param movementPattern the movementPattern to set
     */
    public void setMovementPattern(MovementPattern movementPattern)
    {
        this.movementPattern = movementPattern;
    }
    /**
     * @return the attributes
     */
    public PieceAttribute[] getAttributes()
    {
        return attributes;
    }
    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(PieceAttribute ... attributes)
    {
        this.attributes = attributes;
    }
    
    /**
	 * See if this descriptor has the specified attribute
	 * @param id the attribute ID
	 * @return the attribute or null if the piece has none
	 */
	public PieceAttribute getAttribute(PieceAttributeID id)
	{
		Optional<PieceAttribute> attr = 
			Arrays.stream(attributes)
				.filter(a -> a.getId().equals(id))
				.findFirst();
		return attr.isPresent() ? attr.get() : null;
	}
    private void addPieceAttribute(PieceAttribute ... pieceAttribute){
        PieceAttribute[] pieceAttributes = new PieceAttribute[this.attributes.length + pieceAttribute.length];
        System.arraycopy(this.attributes,0, pieceAttributes,0, this.attributes.length);
        System.arraycopy(pieceAttribute,0,pieceAttributes,this.attributes.length, pieceAttribute.length);
        this.attributes = pieceAttributes;
    }

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "PieceTypeInitializer [pieceName=" + pieceName + ", movementPattern="
		    + movementPattern + ", attributes=" + Arrays.toString(attributes) + "]";
	}
}
