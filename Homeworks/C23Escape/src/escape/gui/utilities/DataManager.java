package escape.gui.utilities;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import escape.gui.State;

public class DataManager {
    private enum Directories {save_state};
    public enum SaveStateKeys {state, econfig_path};
    public final static Path save_state_directory = Path.of("src/escape/data/save_state.json");
    public final static Path data_directory = Path.of("src/escape/data/");
    public final static Path escape_configs_directory = Path.of("src/escape/data/escape_configs/");

    private final static Map<String, String> currentState = new HashMap<>();

    private DataManager(){}
    public static void init() {
        try {
            parseSaveState();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private static void parseSaveState() throws IOException {
        if(!data_directory.toFile().exists()){
            if(!data_directory.toFile().mkdir()) throw new RuntimeException("Failed to create data directory");
        }
        if(!save_state_directory.toFile().exists()){
            if(!save_state_directory.toFile().createNewFile()) throw new RuntimeException("Failed to create save state file");
            writeDefaults(Directories.save_state);
            return;
        }

        JsonReader jr = new JsonReader(new FileReader(save_state_directory.toString()));
        jr.beginObject();
        while(jr.hasNext()) {
            String key = jr.nextName();
            String value = jr.nextString();
            if(Objects.equals(key, SaveStateKeys.state.name())){
                switch(value) {
                    case "HOME" -> AppStateManager.setActiveState(State.HOME);
                    case "GAME_SELECT" -> AppStateManager.setActiveState(State.GAME_SELECT);
                }
            }
        }
    }

    private static void writeDefaults(Directories dir) throws IOException {
        if(dir == null) throw new NullPointerException("passed directory cannot be null");
        JsonWriter writer;
        switch(dir){
            case save_state -> {
                writer = new JsonWriter(new FileWriter(save_state_directory.toString()));
                writer.setIndent("  ");
                writer.beginObject();
                writer.name(SaveStateKeys.state.name()).value(State.HOME.name());
                writer.endObject();
                writer.close();
            }
        }
    }
    public static void updateSaveStateData(Map<String, String> data){
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(save_state_directory.toString()));
            writer.setIndent("  ");
            writer.beginObject();
            switch (data.get(SaveStateKeys.state.name())) {
                case "GAME_SELECT", "HOME" -> writer.name(SaveStateKeys.state.name()).value(data.get(SaveStateKeys.state.name()));
                case "IN_GAME" -> {
                    writer.name(SaveStateKeys.state.name()).value(data.get(SaveStateKeys.state.name()));
                    writer.name(SaveStateKeys.econfig_path.name()).value(data.get(SaveStateKeys.econfig_path.name()));
                }
            }
            writer.endObject().close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static List<Path> retrieveEscapeConfigs(){
        List<Path> escapeConfigs = new ArrayList<>();
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(escape_configs_directory)){
            stream.forEach(escapeConfigs::add);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return escapeConfigs;
    }
}
