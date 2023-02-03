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

import java.util.HashMap;
import java.util.Map;

/**
 * This class has a single method that will translate OCR digits to a string of
 * text digits that correspond to the OCR digits.
 * 
 * @version Mar 13, 2019
 */
public class OCRTranslator {
	public final Map<String, String> acceptedTops;
	public final Map<String, String> acceptedMiddles;
	public final Map<String, String> acceptedBottoms;
	/**
	 * Default constructor. You may not add parameters to this. This is
	 * the only constructor for this class and is what the master tests will use.
	 */
	public OCRTranslator() {
		//adding all accepted values for each number to a lists for each respective place
		acceptedTops = new HashMap<>();
		acceptedMiddles = new HashMap<>();
		acceptedBottoms = new HashMap<>();

		//number zero
		acceptedTops.put("0","b_b");
		acceptedMiddles.put("0","|b|");
		acceptedBottoms.put("0","|_|");

		//number one
		acceptedTops.put("1","bb|");
		acceptedMiddles.put("1","bb|");
		acceptedBottoms.put("1","bb|");

		//number two
		acceptedTops.put("2","b_b");
		acceptedMiddles.put("2","b_|");
		acceptedBottoms.put("2","|_b");

		//number three
		acceptedTops.put("3","b_b");
		acceptedMiddles.put("3","b_|");
		acceptedBottoms.put("3","b_|");

		//number four
		acceptedTops.put("4","bbb");
		acceptedMiddles.put("4","|_|");
		acceptedBottoms.put("4","bb|");

		//number five
		acceptedTops.put("5","b_b");
		acceptedMiddles.put("5","|_b");
		acceptedBottoms.put("5","b_|");

		//number six
		acceptedTops.put("6","b_b");
		acceptedMiddles.put("6","|_b");
		acceptedBottoms.put("6","|_|");

		//number seven
		acceptedTops.put("7","b_b");
		acceptedMiddles.put("7","bb|");
		acceptedBottoms.put("7","bb|");

		//number eight
		acceptedTops.put("8","b_b");
		acceptedMiddles.put("8","|_|");
		acceptedBottoms.put("8","|_|");

		//number nine
		acceptedTops.put("9","b_b");
		acceptedMiddles.put("9","|_|");
		acceptedBottoms.put("9","bb|");
	}
	
	/**
	 * Translate a string of OCR digits to a corresponding string of text
	 * digits. OCR digits are represented as three rows of characters (|, _, and space).
	 * @param top the top row of the OCR input
	 * @param middle the middle row of the OCR input
	 * @param bottom the third row of the OCR input
	 * @return a String containing the digits corresponding to the OCR input
	 * @throws RuntimeException as noted in the specification
	 */
	public String translate(String top, String middle, String bottom) {
		if(top == null || middle == null || bottom == null){
			String clause = top == null ? "1" : middle == null ? "2" : "3";
			throw new OCRException("Line " + clause + " is null");
		}
		if(!(top.length() == middle.length() && top.length() == bottom.length())){
			String clause = top.length() != middle.length() ? "1 and 2" : "1 and 3";
 			throw new OCRException("Lines " + clause + " are not the same length");
		}
		if(!containsValidCharacter(top) && !containsValidCharacter(middle) && !containsValidCharacter(bottom)){
			throw new OCRException("Invalid OCR String");
		}


		StringBuilder returnNumber = new StringBuilder();
		int detectedCounter = 0; //counter to detect whether a sequence has been detected or not

		//OCR digits are only 3 in length therefore only need to check a window of three indexes at a time
		for(int i = 0; i < top.length() - 2; i++){
			int index = i;
			int initialLength = returnNumber.length();

			//boolean to determine if there is a valid sequence
			boolean validSequence = containsValidCharacter(top.substring(index, index + 3)) ||
					containsValidCharacter(middle.substring(index, index + 3)) ||
					containsValidCharacter(bottom.substring(index, index + 3));

			//parsing the top map (could be others but chose top)
			acceptedTops.forEach((key, value) -> {
				if(value.equals(top.substring(index, index + 3)) &&
					acceptedMiddles.get(key).equals(middle.substring(index, index + 3)) &&
					acceptedBottoms.get(key).equals(bottom.substring(index, index + 3))){
						returnNumber.append(key);
				}
			});
			//update index if returnNumber has been updated
			if(returnNumber.length() > initialLength){
				i += 3;
				detectedCounter = 0;
			}
			else if(validSequence) detectedCounter++; //update detected counter if no number valid number detected

			//three chances to detect a valid number after detecting a valid sequence otherwise throw an error
			//or is edge cased for strings with one number
			if(detectedCounter == 3 || (validSequence && i + 1 >= top.length() - 2 && returnNumber.length() == initialLength)) throw new OCRException("Invalid OCR String");

		}
		return returnNumber.toString();
	}
	private boolean containsValidCharacter(String s){
		for(char c : s.toCharArray()){
			if(c == '|' || c == '_') return true;
		}
		return false;
	}
}
