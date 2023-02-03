/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2016-2017 Gary F. Pollice
 *******************************************************************************/
package ocr;

/**
 * The exception class that is used in the OCR TDD project.
 * 
 * @version Mar 13, 2019
 */
public class OCRException extends RuntimeException
{
	/**
	 * Default constructor.
	 * @param message a meaningful message that describes the error
	 */
	public OCRException(String message)
	{
		super(message);
	}
}
