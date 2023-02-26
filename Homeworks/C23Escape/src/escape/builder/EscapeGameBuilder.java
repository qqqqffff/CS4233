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

import com.google.gson.stream.JsonReader;
import econfig.EscapeConfigurator;
import escape.EscapeGameManager;
import escape.required.*;
//import org.antlr.v4.runtime.CharStreams;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class builds an instance of an EscapeGameManager from a configuration
 * file (.egc). This uses the EscapeConfigurator for XML to turn the .egc file
 * into a valid XML string that is then unmarshalled into an EscapeGameInitializer
 * file.
 *
 * The Escape Configuration Tool JAR file is required for this class to compile and
 * run correctly. The file also requires the ANTLR 4 JAR file. Both of these JAR files are
 * in the lib directory of this IntelliJ project.
 *
 * NOTE: The Escape Configuration Tool was built with Java 17.
 * 
 * MODIFIABLE: YES
 * MOVEABLE: NO
 * REQUIRED: YES
 * 
 * You must change this class to be able to get the data from the configuration
 * file and implement the makeGameManager() method. You may not change the signature
 * of that method or the constructor for this class. You can change the file any 
 * other way that you need to.
 * 
 * You don't have to use the EscapeGameInitializer object if
 * you have a way that better suits your design and capabilities. Don't go down
 * a rathole, however, in order to use something different. This implementation
 * works and will not take much time to modify the EscapeGameInitializer to create
 * your game instance. Just creating the game instance should take as little time
 * as possible to implement.
 */
public class EscapeGameBuilder {
    private final EscapeGameInitializer gameInitializer;
    
    /**
     * The constructor takes a file that points to the Escape game
     * configuration file. It should get the necessary information 
     * to be ready to create the game manager specified by the configuration
     * file and other configuration files that it links to.
     * @param fileName the file for the Escape game configuration file (.egc).
     * @throws Exception on any errors
     */
    public EscapeGameBuilder(String fileName) throws Exception {
		//TODO: when i get the time i will implement a converter if i ever get the time
		if(fileName.contains(".egc")) throw new RuntimeException("egc files are not supported, use json");
		JsonReader reader = new JsonReader(new FileReader(fileName));
    	gameInitializer = EscapeJsonConverter.readFromJson(reader);
    }

	/**
	 * Take the .egc file contents and turn it into XML.
	 * @param fileName the input configuration (.egc) file
	 * @return the XML data needed to 
	 * @throws IOException when bad file name is pased
	 */
	@Deprecated
	private String getXmlConfiguration(String fileName) throws IOException
	{
		EscapeConfigurator configurator = new EscapeConfigurator();
//    	return configurator.makeConfiguration(CharStreams.fromFileName(fileName));
		return null;
	}

	/**
	 * Unmarshal the XML into an EscapeGameInitializer object.
	 * @param xmlConfiguration string xml file
	 * @throws JAXBException bad conversion
	 */
	private EscapeGameInitializer unmarshalXml(String xmlConfiguration) throws JAXBException
	{
		JAXBContext contextObj = JAXBContext.newInstance(EscapeGameInitializer.class);
        Unmarshaller mub = contextObj.createUnmarshaller();
        return (EscapeGameInitializer)mub.unmarshal(
            	new StreamSource(new StringReader(xmlConfiguration)));
	}
	
	/**
	 * Getter for the gameInitializer. Can be used to examine it after the builder
	 * creates it.
	 * @return the gameInitializer
	 */
	public EscapeGameInitializer getGameInitializer()
	{
		return gameInitializer;
	}
    
    /***********************************************************************
     * Once the EscapeGameIniializer is constructed, this method creates the
     * EscapeGameManager instance. You use the gameInitializer object to get
	 * all of the information you need to create your game.
     * @return the game instance
     ***********************************************************************/
    public EscapeGameManager<Coordinate> makeGameManager() {
		return new EscapeGameManager<Coordinate>() {
			GameStatus currentGameStatus;
			final List<Location> locations = new ArrayList<>();
			GameObserver observer;
			boolean initialized;
			public void init(){
				if(initialized) return;
				//if(player_choice) return; //TODO: implement me

				initialized = true;
			}

			@Override
			public Coordinate makeCoordinate(int x, int y) {
				return null;
			}

			@Override
			public GameStatus move(Coordinate from, Coordinate to) {
				EscapePiece pieceFrom = getPieceAt(from);
				if(getPieceAt(from) == null){
					System.out.println("piece is null");
					return new GameStatus() {
						@Override
						public boolean isValidMove() {
							return false;
						}

						@Override
						public boolean isMoreInformation() {
							return false;
						}

						@Override
						public MoveResult getMoveResult() {
							return MoveResult.NONE;
						}

						@Override
						public Coordinate finalLocation() {
							return from;
						}
					};
				}
				assert pieceFrom != null;
				EscapePiece pieceTo = getPieceAt(to);
				for(Location location : locations){
					if(location.getCoordinate().equals(from) && pieceTo == null){
						location.updateEscapePiece(null);
						for(Location newLocation : locations){
							if(newLocation.getCoordinate().equals(to)){
								newLocation.updateEscapePiece(pieceFrom);
								break;
							}
						}
						break;
					}
					else if(location.getCoordinate().equals(to) && pieceTo != null){
						//TODO: do something if there is a guy already there
						for(PieceTypeDescriptor descriptor : gameInitializer.getPieceTypes()){
							if(descriptor.getPieceName() == pieceFrom.getName()) {
								for (PieceAttribute attribute : descriptor.getAttributes()) {
									if(attribute.id == EscapePiece.PieceAttributeID.VALUE){

									}
								}
							}
							else if(descriptor.getPieceName() == pieceTo.getName()){

							}
						}
						int value
						EscapeJsonConverter.parse
					}
				}
				//TODO: replace with a default game status maker
				return currentGameStatus;
			}

			@Override
			public EscapePiece getPieceAt(Coordinate coordinate) {
				return null;
			}



			@Override
			public GameObserver addObserver(GameObserver observer) {
				return EscapeGameManager.super.addObserver(observer);
			}

			@Override
			public GameObserver removeObserver(GameObserver observer) {
				return EscapeGameManager.super.removeObserver(observer);
			}
		};
	}
}
