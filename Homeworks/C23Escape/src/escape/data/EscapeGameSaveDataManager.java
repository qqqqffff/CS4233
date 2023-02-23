package escape.data;

import com.google.gson.stream.JsonWriter;
import escape.builder.EscapeGameInitializer;
import escape.required.GameObserver;
import escape.required.GameStatus;
import escape.required.Location;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EscapeGameSaveDataManager {
    public static final String DEFAULT_JSON_INDENT = "  ";
    private EscapeGameSaveDataManager(){}
    public static void writeInitializerToJson(EscapeGameInitializer initializer, String fileName) throws IOException {
//        JsonWriter jsonWriter = new JsonWriter(new FileWriter(fileName));
//        jsonWriter.setIndent("  ");
    }
    public static void writeActiveGameState(GameStatus status, GameObserver[] observers, Location[] locations){

    }
}
