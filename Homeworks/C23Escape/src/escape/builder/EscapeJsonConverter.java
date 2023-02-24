package escape.builder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.google.gson.stream.JsonReader;

import static escape.required.Coordinate.CoordinateType.HEX;
import static escape.required.Coordinate.CoordinateType.SQUARE;
import static escape.required.EscapePiece.MovementPattern.*;
import static escape.required.EscapePiece.MovementPattern.ORTHOGONAL;
import static escape.required.EscapePiece.PieceAttributeID.*;
import static escape.required.EscapePiece.PieceAttributeID.VALUE;
import static escape.required.EscapePiece.PieceName.*;
import static escape.required.LocationType.*;

import escape.data.EscapeGameSaveDataManager;
import escape.data.EscapeGameSaveDataManager.EscapeGameInitializerKeys.*;
import escape.required.*;

/**
 * Escape Json Converter class - a data parsing class that
 * contains static methods to handle data that is passed through the application (will be edge case tested when I have time)
 * most of the methods are not edge case tested and only work with a specific format to the config files
 * see configs to observe the formatting
 */
public class EscapeJsonConverter {
    public static final int DEFAULT_TAB_SIZE = 4;
    public static final char WHITE_SPACE_CHAR = ' ';
    public static final char OPEN_PARENTHESIS_CHAR = '(';
    public static final char CLOSED_PARENTHESIS_CHAR = ')';
    public static final char OPEN_BRACKET_CHAR = '[';
    public static final char CLOSED_BRACKET_CHAR = ']';
    public static final char COMMA_CHAR = ',';
    public static final char PERIOD_CHAR = '.';
    private EscapeJsonConverter(){}
    /**
     * Read From Json Method:
     * Method to read and parse the data from a Json file
     * parsing is delegated to respective methods
     * @param jsonReader that is the json file (Json reading structure inherited from Google's GSON dependency)
     * @return a new EscapeGameInitializer based on the inputted json reader
     */
    public static EscapeGameInitializer readFromJson(JsonReader jsonReader){
        if(jsonReader == null) return null;
        EscapeGameInitializer esgInitializer = new EscapeGameInitializer();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String key = jsonReader.nextName();
                if(EscapeGameSaveDataManager.parseEscapeGameInitializerKeys(key) == null) continue;
                switch(EscapeGameSaveDataManager.parseEscapeGameInitializerKeys(key)){
                    case coordinate_type -> esgInitializer.setCoordinateType(parseCoordinateType(jsonReader.nextString()));
                    case x_max -> esgInitializer.setxMax(Integer.parseInt(jsonReader.nextString()));
                    case y_max -> esgInitializer.setyMax(Integer.parseInt(jsonReader.nextString()));
                    case locations -> {
                        jsonReader.beginArray();
                        while(jsonReader.hasNext()) {
                            jsonReader.beginObject();
                            esgInitializer.addLocationInitializer(parseLocationInitializer(jsonReader));
                            jsonReader.endObject();
                        }
                        jsonReader.endArray();
                    }
                    case players -> {
                        jsonReader.beginArray();
                        while(jsonReader.hasNext()) {
                            esgInitializer.addPlayers(jsonReader.nextString());
                        }
                        jsonReader.endArray();
                    }
                    case piece_descriptors -> {
                        jsonReader.beginArray();
                        while(jsonReader.hasNext()) {
                            jsonReader.beginObject();
                            esgInitializer.addPieceTypes(parsePieceTypeDescriptor(jsonReader));
                            jsonReader.endObject();
                        }
                        jsonReader.endArray();
                    }
                    case rules -> {
                        jsonReader.beginArray();
                        while(jsonReader.hasNext()){
                            jsonReader.beginObject();
                            esgInitializer.addRules(EscapeJsonConverter.parseRuleDescriptor(jsonReader));
                            jsonReader.endObject();
                        }
                        jsonReader.endArray();
                    }
                }
            }
        }
        catch (Exception ignored){

        }
        return esgInitializer;
    }

    /**
     * Escape Game Config Converter Method:
     * Method that reads and parses an Escape Game Config File based on the file structure that is seen
     * throughout my tests and data files
     * @param fileName file name that ends in .egc
     * @return a new EscapeGameInitializer based on the inputted esg file
     * @throws IOException when file reader is bad
     */
    public static EscapeGameInitializer esgConfigConverter(String fileName) throws IOException {
        if(fileName == null) return null;
        EscapeGameInitializer esgInitializer = new EscapeGameInitializer();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String line = bufferedReader.readLine();
        String tabRegex = "\s{" + DEFAULT_TAB_SIZE + "}.*";
        while(line != null){
            line = line.toUpperCase();
            if(line.contains("COORDINATE")){
                if(line.contains("TYPE")){
                    String type = line.substring(line.lastIndexOf(WHITE_SPACE_CHAR) + 1);
                    esgInitializer.setCoordinateType(parseCoordinateType(type));
                    System.out.println(type);
                }
            }
            else if(line.contains("XMAX")){
                String max = line.substring(line.lastIndexOf(WHITE_SPACE_CHAR) + 1);
                esgInitializer.setxMax(Integer.parseInt(max));
            }
            else if(line.contains("YMAX")){
                String max = line.substring(line.lastIndexOf(WHITE_SPACE_CHAR) + 1);
                esgInitializer.setyMax(Integer.parseInt(max));
            }
            else if(line.contains("PLAYERS")){
                line = bufferedReader.readLine();
                while(line != null && Pattern.matches(tabRegex, line)){
                    esgInitializer.addPlayers(line.substring(DEFAULT_TAB_SIZE));
                    line = bufferedReader.readLine();
                }
                System.out.println(Arrays.toString(esgInitializer.getPlayers()));
                continue;
            }
            else if(line.contains("LOCATIONS")){
                line = bufferedReader.readLine();
                while(line != null && Pattern.matches(tabRegex, line)){
                    esgInitializer.addLocationInitializer(parseLocationInitializer(line.substring(DEFAULT_TAB_SIZE)));
                    line = bufferedReader.readLine();
                }
                System.out.println(Arrays.toString(esgInitializer.getLocationInitializers()));
                continue;
            }
            else if(line.contains("PIECE DESCRIPTORS")){
                line = bufferedReader.readLine();
                while(line != null && Pattern.matches(tabRegex, line)){
                    esgInitializer.addPieceTypes(parsePieceTypeDescriptor(line.substring(DEFAULT_TAB_SIZE)));
                    line = bufferedReader.readLine();
                }
                System.out.println(Arrays.toString(esgInitializer.getPieceTypes()));
                continue;
            }
            else if(line.contains("RULES")){
                line = bufferedReader.readLine();
                while(line != null && Pattern.matches(tabRegex, line)){
                    esgInitializer.addRules(parseRuleDescriptor(line.substring(DEFAULT_TAB_SIZE)));
                    line = bufferedReader.readLine();
                }
                System.out.println(Arrays.toString(esgInitializer.getRules()));
                continue;
            }
            line = bufferedReader.readLine();

        }
        EscapeGameSaveDataManager.writeInitializerToJson(esgInitializer, fileName);
        return esgInitializer;
    }

    /**
     * Location Initializer Parsers:
     * Parses the Location Initializer data based on the line or the JsonReader
     * @param reader or line to be parsed
     * @return a Location Initializer based on the parsed information
     * @throws IOException when there is an issue with the json reader
     */
    public static LocationInitializer parseLocationInitializer(JsonReader reader) throws IOException{
        if(reader == null) return null;
        int x = 0, y = 0;
        LocationType locationType = null;
        String player = null;
        EscapePiece.PieceName pieceName = null;
        while(reader.hasNext()){
            String key = reader.nextName();
            switch(key){
                case "x" -> x = Integer.parseInt(reader.nextString());
                case "y" -> y = Integer.parseInt(reader.nextString());
                case "player" -> player = reader.nextString();
                case "location_type" -> locationType = parseLocationType(reader.nextString());
                case "piece_name" -> pieceName = parsePieceName(reader.nextString());
            }
        }
        return new LocationInitializer(x, y, locationType, player, pieceName);
    }
    public static LocationInitializer parseLocationInitializer(String line){
        if(line == null) return null;
        int x = 0, y = 0;
        LocationType locationType = null;
        String player = null;
        EscapePiece.PieceName pieceName = null;

        if(line.contains("(")) {
            x = Integer.parseInt(line.substring(line.indexOf(OPEN_PARENTHESIS_CHAR) + 1, line.indexOf(COMMA_CHAR)));
            try {
                y = Integer.parseInt(line.substring(line.indexOf(COMMA_CHAR) + 2, line.indexOf(CLOSED_PARENTHESIS_CHAR)));
            } catch (NumberFormatException ignored) {
                y = Integer.parseInt(line.substring(line.indexOf(COMMA_CHAR) + 1, line.indexOf(CLOSED_PARENTHESIS_CHAR)));
            }
        }

        line = line.substring(line.indexOf(CLOSED_PARENTHESIS_CHAR));
        String[] entries = line.split("\s");
        for(int i = 0; i < entries.length; i++){
            if(entries[i] == null) continue;
            if(entries[i].contains(CLOSED_PARENTHESIS_CHAR + "") || entries[i].contains(OPEN_PARENTHESIS_CHAR + "")) entries[i] = null;
            if(parseLocationType(entries[i]) != null) {
                locationType = parseLocationType(entries[i]);
                entries[i] = null;
            }
            else if(parsePieceName(entries[i]) != null){
                pieceName = parsePieceName(entries[i]);
                entries[i] = null;
            }
        }
        for(String entry : entries){
            if(entry != null) {
                player = entry;
                break;
            }
        }

        return new LocationInitializer(x, y, locationType, player, pieceName);
    }

    /**
     * Location Type Parser:
     * Parses the Location Type string and returns the respective enum value
     * @param locationType string value of the enumeration (note all strings will be matched to the upper enumeration)
     * @return the parsed location type if found
     */
    public static LocationType parseLocationType(String locationType){
        if(locationType == null) return null;
        locationType = locationType.toUpperCase(Locale.ROOT);
        if(locationType.equals(CLEAR.name())) return CLEAR;
        else if(locationType.equals(BLOCK.name())) return BLOCK;
        else if(locationType.equals(EXIT.name())) return EXIT;
        return null;
    }

    /**
     * Rule Descriptor Parsers
     * Parses the Rule Descriptor based on the passed in information
     * @param reader or line to be parsed
     * @return a new RuleDescriptor based on the parsed information
     * @throws IOException when there is an issue with the JSON reader
     */
    public static RuleDescriptor parseRuleDescriptor(JsonReader reader) throws IOException{
        if(reader == null) return null;
        return new RuleDescriptor(parseRuleID(reader.nextName()), Integer.parseInt(reader.nextString()));
    }
    public static RuleDescriptor parseRuleDescriptor(String line){
        if(line == null) return null;
        Rule.RuleID id = null;
        int value = -1;
        String[] entries = line.split("\s");
        for(String entry : entries){
            if(parseRuleID(entry) != null) id = parseRuleID(entry);
            else value = Integer.parseInt(entry);
        }
        return new RuleDescriptor(id, value);
    }

    /**
     * Rule ID Parser:
     * Parses the Rule ID based on the string and returns the respective enum value
     * Follows the same structure as all the other enum parsers
     * @param ruleID string value of the enum
     * @return the parsed enum if exists
     */
    private static Rule.RuleID parseRuleID(String ruleID){
        if(ruleID == null) return null;
        ruleID = ruleID.toUpperCase(Locale.ROOT);
        if(ruleID.equals(Rule.RuleID.POINT_CONFLICT.name())) return Rule.RuleID.POINT_CONFLICT;
        else if(ruleID.equals(Rule.RuleID.SCORE.name())) return Rule.RuleID.SCORE;
        else if(ruleID.equals(Rule.RuleID.TURN_LIMIT.name())) return Rule.RuleID.TURN_LIMIT;
        return null;
    }

    /**
     * Coordinate Type Parser:
     * Parses the Coordinate Type based on the string and returns the respective enum value
     * Follows the same structure as all the other enum parsers
     * @param coordinateType string value of the enum
     * @return the parsed enum if exists
     */
    public static Coordinate.CoordinateType parseCoordinateType(String coordinateType){
        if(coordinateType == null) return null;
        coordinateType = coordinateType.toUpperCase(Locale.ROOT);
        if(coordinateType.equals(HEX.name())) return HEX;
        else if(coordinateType.equals(SQUARE.name())) return SQUARE;
        return null;
    }

    /**
     * Piece Type Descriptor Parsers:
     * Parses the Piece Type Descriptors based on the passed in information
     * (Overloaded)
     * @param reader or line to be parsed
     * @return a new RuleDescriptor based on the parsed information
     * @throws IOException when there is an issued with the JsonReader
     */
    public static PieceTypeDescriptor parsePieceTypeDescriptor(JsonReader reader) throws IOException{
        if(reader == null) return null;
        EscapePiece.PieceName pieceName = null;
        EscapePiece.MovementPattern movementPattern = null;
        PieceAttribute[] attributes = new PieceAttribute[]{};
        while(reader.hasNext()){
            String key = reader.nextName();
            if(EscapeGameSaveDataManager.parseEscapeGameInitializerKeys(key) == null) continue;
            switch(EscapeGameSaveDataManager.parseEscapeGameInitializerKeys(key)){
                case piece_name -> pieceName = parsePieceName(reader.nextString());
                case movement_pattern -> movementPattern = parseMovementPattern(reader.nextString());
                case attributes -> {
                    reader.beginArray();
                    while(reader.hasNext()){
                        reader.beginObject();
                        PieceAttribute[] newAttributes = new PieceAttribute[attributes.length + 1];
                        System.arraycopy(attributes,0,newAttributes,0,attributes.length);
                        newAttributes[attributes.length] = parsePieceAttribute(reader);
                        attributes = newAttributes;
                        reader.endObject();
                    }
                    reader.endArray();
                }
            }
        }
        return new PieceTypeDescriptor(pieceName, movementPattern, attributes);
    }
    public static PieceTypeDescriptor parsePieceTypeDescriptor(String line){
        if(line == null) return null;
        EscapePiece.PieceName pieceName = null;
        EscapePiece.MovementPattern movementPattern = null;
        PieceAttribute[] pieceAttributes = new PieceAttribute[]{};

        if(line.contains(CLOSED_BRACKET_CHAR + "")) {
            pieceAttributes = parsePieceAttributes(line.substring(line.indexOf(OPEN_BRACKET_CHAR) + 1, line.indexOf(CLOSED_BRACKET_CHAR)));
            line = line.substring(0, line.indexOf(OPEN_BRACKET_CHAR));
        }

        String[] entries = line.split("\s");
        for(int i = 0; i < entries.length; i++){
            if(entries[i] == null) continue;
            else if(parsePieceName(entries[i]) != null) pieceName = parsePieceName(entries[i]);
            else if(parseMovementPattern(entries[i]) != null) movementPattern = parseMovementPattern(entries[i]);
            entries[i] = null;
        }
        return new PieceTypeDescriptor(pieceName, movementPattern, pieceAttributes);
    }

    /**
     * Piece Attribute(s) Parsers:
     * Parses the attribute information passed from the PieceTypeDescriptor Method
     * @param reader or attributes or attribute to be parsed
     * @return a or an array of PieceAttributes based on the method that calls
     * @throws IOException when there is an error with the JsonReader
     */
    public static PieceAttribute parsePieceAttribute(JsonReader reader) throws IOException {
        if(reader == null) return null;
        return new PieceAttribute(parsePieceAttributeID(reader.nextName()), Integer.parseInt(reader.nextString()));
    }
    public static PieceAttribute[] parsePieceAttributes(String attributes){
        if(attributes == null) return null;
        List<PieceAttribute> attributeList = new ArrayList<>();

        if(attributes.indexOf(',') == -1) {
            attributeList.add(parsePieceAttribute(attributes));
        }

        String regex = ".*" + COMMA_CHAR + ".*";
        while(Pattern.matches(regex, attributes)){
            attributeList.add(parsePieceAttribute(attributes.substring(0, attributes.indexOf(COMMA_CHAR))));
            attributes = attributes.substring(attributes.indexOf(COMMA_CHAR) + 2);
            if(!Pattern.matches(regex, attributes)) attributeList.add(parsePieceAttribute(attributes));
        }
        return attributeList.toArray(PieceAttribute[]::new);
    }
    public static PieceAttribute parsePieceAttribute(String attribute){
        if(attribute == null) return null;
        EscapePiece.PieceAttributeID id = parsePieceAttributeID(attribute);
        if(attribute.contains(" ")){
            id = parsePieceAttributeID(attribute.substring(0, attribute.indexOf(WHITE_SPACE_CHAR)));
            int value = Integer.parseInt(attribute.substring(attribute.indexOf(WHITE_SPACE_CHAR) + 1));
            return new PieceAttribute(id, value);
        }
        return new PieceAttribute(id,1);
    }
    /**
     * Piece Name Parser:
     * Parses the Piece Name based on the string and returns the respective enum value
     * Follows the same structure as all the other enum parsers
     * @param pieceName string value of the enum
     * @return the parsed enum if exists
     */
    public static EscapePiece.PieceName parsePieceName(String pieceName){
        if(pieceName == null) return null;
        pieceName = pieceName.toUpperCase(Locale.ROOT);
        if(pieceName.equals(BIRD.name())) return BIRD;
        else if(pieceName.equals(DOG.name())) return DOG;
        else if(pieceName.equals(FROG.name())) return FROG;
        else if(pieceName.equals(HORSE.name())) return HORSE;
        else if(pieceName.equals(SNAIL.name())) return SNAIL;
        return null;
    }
    /**
     * Movement Pattern Parser:
     * Parses the Movement Pattern based on the string and returns the respective enum value
     * Follows the same structure as all the other enum parsers
     * @param movementPattern string value of the enum
     * @return the parsed enum if exists
     */
    public static EscapePiece.MovementPattern parseMovementPattern(String movementPattern){
        if(movementPattern == null) return null;
        movementPattern = movementPattern.toUpperCase(Locale.ROOT);
        if(movementPattern.equals(DIAGONAL.name())) return DIAGONAL;
        else if(movementPattern.equals(LINEAR.name())) return LINEAR;
        else if(movementPattern.equals(OMNI.name())) return OMNI;
        else if(movementPattern.equals(ORTHOGONAL.name())) return ORTHOGONAL;
        return null;
    }
    /**
     * Piece Attribute ID Parser:
     * Parses the Piece Attribute ID based on the string and returns the respective enum value
     * Follows the same structure as all the other enum parsers
     * @param pieceAttributeID string value of the enum
     * @return the parsed enum if exists
     */
    public static EscapePiece.PieceAttributeID parsePieceAttributeID(String pieceAttributeID){
        if(pieceAttributeID == null) return null;
        pieceAttributeID = pieceAttributeID.toUpperCase(Locale.ROOT);
        if(pieceAttributeID.equals(FLY.name())) return FLY;
        else if(pieceAttributeID.equals(DISTANCE.name())) return DISTANCE;
        else if(pieceAttributeID.equals(JUMP.name())) return JUMP;
        else if(pieceAttributeID.equals(UNBLOCK.name())) return UNBLOCK;
        else if(pieceAttributeID.equals(VALUE.name())) return VALUE;
        return null;
    }
}
