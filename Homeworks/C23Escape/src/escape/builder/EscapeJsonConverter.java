package escape.builder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.stream.JsonReader;

import static escape.required.Coordinate.CoordinateType.HEX;
import static escape.required.Coordinate.CoordinateType.SQUARE;
import static escape.required.EscapePiece.MovementPattern.*;
import static escape.required.EscapePiece.MovementPattern.ORTHOGONAL;
import static escape.required.EscapePiece.PieceAttributeID.*;
import static escape.required.EscapePiece.PieceAttributeID.VALUE;
import static escape.required.EscapePiece.PieceName.*;
import static escape.required.LocationType.*;

import escape.required.*;


public class EscapeJsonConverter {

    //TODO: generate java doc comments and fix inferences
    public static EscapeGameInitializer readFromJson(JsonReader jsonReader){
        EscapeGameInitializer esgInitializer = new EscapeGameInitializer();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String key = jsonReader.nextName();
                switch(key){
                    case "coordinate_type" -> esgInitializer.setCoordinateType(parseCoordinateType(jsonReader.nextString()));
                    case "x_max" -> esgInitializer.setxMax(Integer.parseInt(jsonReader.nextString()));
                    case "y_max" -> esgInitializer.setyMax(Integer.parseInt(jsonReader.nextString()));
                    case "locations" -> {
                        jsonReader.beginArray();
                        while(jsonReader.hasNext()) {
                            jsonReader.beginObject();
                            esgInitializer.addLocationInitializer(parseLocationInitializer(jsonReader));
                            jsonReader.endObject();
                        }
                        jsonReader.endArray();
                    }
                    case "players" -> {
                        jsonReader.beginArray();
                        while(jsonReader.hasNext()) {
                            esgInitializer.addPlayers(jsonReader.nextString());
                        }
                        jsonReader.endArray();
                    }
                    case "piece_descriptors" -> {
                        jsonReader.beginArray();
                        while(jsonReader.hasNext()) {
                            jsonReader.beginObject();
                            esgInitializer.addPieceTypes(parsePieceTypeDescriptor(jsonReader));
                            jsonReader.endObject();
                        }
                        jsonReader.endArray();
                    }
                    case "rules" -> {
                        jsonReader.beginObject();
                        while(jsonReader.hasNext()){
                            esgInitializer.addRules(EscapeJsonConverter.parseRuleDescriptor(jsonReader));
                        }
                        jsonReader.endObject();
                    }
                }
            }
        }
        catch (Exception ignored){

        }
        return esgInitializer;
    }

    //TODO: implement me
    public static EscapeGameInitializer esgConfigConverter(String fileName) throws IOException {
        EscapeGameInitializer esgInitializer = new EscapeGameInitializer();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        for(String line : bufferedReader.lines().toList()){
            System.out.println();
        }
        return esgInitializer;
    }
    public static LocationInitializer parseLocationInitializer(JsonReader reader) throws Exception{
        if(reader == null) throw new NullPointerException("Reader is null");
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
                case "location_type" -> locationType = parseLocationTypes(reader.nextString());
                case "piece_name" -> pieceName = parsePieceName(reader.nextString());
            }
        }
        return new LocationInitializer(x, y, locationType, player, pieceName);
    }
    public static LocationType parseLocationTypes(String locationTypes){
        if(locationTypes.equals(CLEAR.name())) return CLEAR;
        else if(locationTypes.equals(BLOCK.name())) return BLOCK;
        else if(locationTypes.equals(EXIT.name())) return EXIT;
        return null;
    }
    public static RuleDescriptor parseRuleDescriptor(JsonReader reader) throws Exception{
        if(reader == null) throw new NullPointerException("Reader is null");
        RuleDescriptor ruleDescriptor = new RuleDescriptor();
        switch(reader.nextName()){
            case "POINT_CONFLICT" -> ruleDescriptor.ruleId = Rule.RuleID.POINT_CONFLICT;
            case "SCORE" -> ruleDescriptor.ruleId = Rule.RuleID.SCORE;
            case "TURN_LIMIT" -> ruleDescriptor.ruleId = Rule.RuleID.TURN_LIMIT;
        }
        ruleDescriptor.ruleValue = Integer.parseInt(reader.nextString());
        return ruleDescriptor;
    }
    public static Coordinate.CoordinateType parseCoordinateType(String coordinateType){
        if(coordinateType.equals(HEX.name())) return HEX;
        else if(coordinateType.equals(SQUARE.name())) return SQUARE;
        return null;
    }

    public static PieceTypeDescriptor parsePieceTypeDescriptor(JsonReader reader) throws Exception{
        if(reader == null) throw new NullPointerException("Reader is null");
        EscapePiece.PieceName pieceName = null;
        EscapePiece.MovementPattern movementPattern = null;
        PieceAttribute[] attributes = new PieceAttribute[]{};
        while(reader.hasNext()){
            String key = reader.nextName();
            switch(key){
                case "piece_name" -> pieceName = parsePieceName(reader.nextString());
                case "movement_pattern" -> movementPattern = parseMovementPattern(reader.nextString());
                case "attributes" -> {
                    reader.beginObject();
                    while(reader.hasNext()){
                        PieceAttribute[] newAttributes = new PieceAttribute[attributes.length + 1];
                        System.arraycopy(attributes,0,newAttributes,0,attributes.length);
                        newAttributes[attributes.length] = parsePieceAttribute(reader);
                        attributes = newAttributes;
                    }
                    reader.endObject();
                }
            }
        }
        return new PieceTypeDescriptor(pieceName, movementPattern, attributes);
    }
    private static PieceAttribute parsePieceAttribute(JsonReader reader) throws Exception {
        return new PieceAttribute(parsePieceAttributeID(reader.nextName()), Integer.parseInt(reader.nextString()));
    }
    private static EscapePiece.PieceName parsePieceName(String pieceName){
        if(pieceName.equals(BIRD.name())) return BIRD;
        else if(pieceName.equals(DOG.name())) return DOG;
        else if(pieceName.equals(FROG.name())) return FROG;
        else if(pieceName.equals(HORSE.name())) return SNAIL;
        else if(pieceName.equals(SNAIL.name())) return DOG;
        return null;
    }
    private static EscapePiece.MovementPattern parseMovementPattern(String movementPattern){
        if(movementPattern.equals(DIAGONAL.name())) return DIAGONAL;
        if(movementPattern.equals(LINEAR.name())) return LINEAR;
        if(movementPattern.equals(OMNI.name())) return OMNI;
        if(movementPattern.equals(ORTHOGONAL.name())) return ORTHOGONAL;
        return null;
    }
    private static EscapePiece.PieceAttributeID parsePieceAttributeID(String pieceAttributeID){
        if(pieceAttributeID.equals(FLY.name())) return FLY;
        if(pieceAttributeID.equals(DISTANCE.name())) return DISTANCE;
        if(pieceAttributeID.equals(JUMP.name())) return JUMP;
        if(pieceAttributeID.equals(UNBLOCK.name())) return UNBLOCK;
        if(pieceAttributeID.equals(VALUE.name())) return VALUE;
        return null;
    }
}
