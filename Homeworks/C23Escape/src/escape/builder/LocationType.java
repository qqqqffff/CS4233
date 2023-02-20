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

import static escape.builder.LocationType.LocationTypes.*;

/**
 * Every location (e.g., square) on an Escape board has a type. This enumeration
 * identifies the valid types.
 * 
 * MODIFIABLE: NO
 * MOVEABLE: YES
 * REQUIRED: YES
 */
public interface LocationType {
	enum LocationTypes { BLOCK, CLEAR, EXIT }
	static LocationTypes parseLocationTypes(String locationTypes){
		if(locationTypes.equals(CLEAR.name())) return CLEAR;
		else if(locationTypes.equals(BLOCK.name())) return BLOCK;
		else if(locationTypes.equals(EXIT.name())) return EXIT;
		return null;
	}
}
