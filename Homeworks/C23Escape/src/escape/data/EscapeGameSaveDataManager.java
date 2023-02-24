package escape.data;

import com.google.gson.stream.JsonWriter;
import escape.builder.EscapeGameInitializer;
import escape.builder.LocationInitializer;
import escape.required.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import static escape.builder.EscapeJsonConverter.PERIOD_CHAR;
import static escape.data.EscapeGameSaveDataManager.EscapeGameInitializerKeys.*;

public class EscapeGameSaveDataManager {
    public static final String DEFAULT_JSON_INDENT = "  ";
    public static final String JSON_FILE_TERMINATOR = ".json";

    public enum EscapeGameInitializerKeys { coordinate_type, x_max, y_max, locations, players, piece_descriptors, rules,
                                                x, y, player, location_type, piece_name, movement_pattern, attributes}
    private EscapeGameSaveDataManager(){}

    /**
     * Write Escape Game Initializer To Json Method:
     *
     * Will Overwrite any existing jsons that have the same file name
     * If either parameter are null will do nothing
     * @param initializer
     * @param fileName
     * @throws IOException
     */
    public static void writeInitializerToJson(EscapeGameInitializer initializer, String fileName) throws IOException {
        if(initializer == null || fileName == null) return;
        fileName = fileName.substring(0, fileName.indexOf(PERIOD_CHAR)) + JSON_FILE_TERMINATOR;

        JsonWriter jsonWriter = new JsonWriter(new FileWriter(fileName));
        jsonWriter.setIndent(DEFAULT_JSON_INDENT);
        jsonWriter.beginObject();
        jsonWriter.name(coordinate_type.name()).value(initializer.getCoordinateType().name());
        jsonWriter.name(x_max.name()).value(String.valueOf(initializer.getxMax()));
        jsonWriter.name(y_max.name()).value(String.valueOf(initializer.getyMax()));
        jsonWriter.name(players.name());
        jsonWriter.beginArray();
        for(String player : initializer.getPlayers()){
            jsonWriter.value(player);
        }
        jsonWriter.endArray();
        jsonWriter.name(locations.name()).beginArray();
        for(LocationInitializer location : initializer.getLocationInitializers()){
            jsonWriter.beginObject();
            jsonWriter.name(x.name()).value(String.valueOf(location.getX()));
            jsonWriter.name(y.name()).value(String.valueOf(location.getY()));
            jsonWriter.name(player.name()).value(location.getPlayer() != null ? location.getPlayer() : "null");
            jsonWriter.name(location_type.name()).value(location.getLocationType() != null ? location.getLocationType().name() : "null");
            jsonWriter.name(piece_name.name()).value(location.getPieceName() != null ? location.getPieceName().name() : "null");
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.name(piece_descriptors.name()).beginArray();
        for(PieceTypeDescriptor descriptor : initializer.getPieceTypes()){
            jsonWriter.beginObject();
            jsonWriter.name(piece_name.name()).value(descriptor.getPieceName() != null ? descriptor.getPieceName().name() : null);
            jsonWriter.name(movement_pattern.name()).value(descriptor.getMovementPattern() != null ? descriptor.getMovementPattern().name() : "null");
            jsonWriter.name(attributes.name()).beginArray();
            for(PieceAttribute attribute : descriptor.getAttributes()){
                jsonWriter.beginObject();
                jsonWriter.name(attribute.id.name().toLowerCase(Locale.ROOT)).value(String.valueOf(attribute.getValue()));
                jsonWriter.endObject();
            }
            jsonWriter.endArray().endObject();
        }
        jsonWriter.endArray();
        jsonWriter.name(rules.name()).beginArray();
        for(RuleDescriptor rule : initializer.getRules()) jsonWriter.beginObject().name(rule.ruleId.name().toLowerCase(Locale.ROOT)).value(String.valueOf(rule.ruleValue)).endObject();
        jsonWriter.endArray();
        jsonWriter.endObject();
        jsonWriter.close();
    }
    public static void writeActiveGameState(GameStatus status, GameObserver[] observers, Location[] locations){

    }

    public static EscapeGameInitializerKeys parseEscapeGameInitializerKeys(String key){
        if(key == null) return null;
        key = key.toLowerCase(Locale.ROOT);
        if(key.equals(coordinate_type.name())) return coordinate_type;
        else if(key.equals(x_max.name())) return x_max;
        else if(key.equals(y_max.name())) return y_max;
        else if(key.equals(players.name())) return players;
        else if(key.equals(locations.name())) return locations;
        else if(key.equals(piece_descriptors.name())) return piece_descriptors;
        else if(key.equals(rules.name())) return rules;
        else if(key.equals(piece_name.name())) return piece_name;
        else if(key.equals(movement_pattern.name())) return movement_pattern;
        else if(key.equals(attributes.name())) return attributes;
        return null;
    }
}
