package escape.gui;

import escape.gui.Offset.OffsetType;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static escape.gui.Screen.DEFAULT_WIDTH;
import static escape.gui.Screen.DEFAULT_HEIGHT;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AppState {
    double DEFAULT_SCREEN_WIDTH = DEFAULT_WIDTH;
    double DEFAULT_SCREEN_HEIGHT = DEFAULT_HEIGHT;
    double DEFAULT_OFFSET_X = DEFAULT_WIDTH / 16;
    double DEFAULT_OFFSET_Y = DEFAULT_HEIGHT / 16;
    double DEFAULT_OFFSET_WIDTH = DEFAULT_WIDTH - (DEFAULT_OFFSET_X * 2);
    double DEFAULT_OFFSET_HEIGHT = DEFAULT_HEIGHT - (DEFAULT_OFFSET_Y * 2);
    Offset offsets = new Offset(DEFAULT_OFFSET_X, DEFAULT_OFFSET_Y, DEFAULT_OFFSET_WIDTH, DEFAULT_OFFSET_HEIGHT);

    /**
     * Update Layout Method:
     * Method to be implemented by children to update the State's layout based on
     * width and height changes to the Screen
     * @param width number to update the width of the state
     * @param height number to update the height of the state
     */
    void updateLayout(Number width, Number height);

    /**
     * Restore Data Method:
     * Method to restore the App State data from the data directory
     * @param data that is to be used to restore the place of the user
     */
    void restoreData(Map<String, String> data);

    /**
     * Compute Offsets Function:
     * Computes the sizes to be used by the App State based on defaults
     * Note - overloaded
     * @param offsets_list set of offset values to be updated
     */
    default void updateDefaultOffsets(Map<OffsetType, Double> offsets_list){
        if(offsets_list == null || offsets_list.isEmpty()) throw new NullPointerException("offsets list is null");
        offsets_list.forEach((offset, value) -> {
            switch(offset){
                case x -> offsets.setX(value);
                case y -> offsets.setY(value);
                case width -> offsets.setWidth(value);
                case height -> offsets.setHeight(value);
            }
        });
    }
    default void updateDefaultOffsets(OffsetType offset, double value){
        if(offset == null) throw new NullPointerException("offset is null");
        if(value < 0) throw new NumberFormatException("value is less than 0");
        switch(offset){
            case x -> offsets.setX(value);
            case y -> offsets.setY(value);
            case width -> offsets.setWidth(value);
            case height -> offsets.setHeight(value);
        }
    }

    /**
     * Generate Default Offsets Map Function:
     * Generates a list of offsets to be
     * Note - overloaded
     * @param offset to be updated
     * @return set of or single offset value
     */
    default Map<OffsetType, Double> generateDefaultOffsetsMap(OffsetType offset){
        if(offset == null) throw new NullPointerException("Offset is null");
        Map<OffsetType, Double> offsets_list = new HashMap<>();
        switch(offset) {
            case x -> offsets_list.put(OffsetType.x, offsets.getX());
            case y -> offsets_list.put(OffsetType.y, offsets.getY());
            case width -> offsets_list.put(OffsetType.width, offsets.getWidth());
            case height -> offsets_list.put(OffsetType.height, offsets.getHeight());
        }
        return offsets_list;
    }
    default Map<OffsetType, Double> generateDefaultOffsetsMap(List<OffsetType> offset_list){
        if(offset_list == null) throw new NullPointerException("Offset is null");
        Map<OffsetType, Double> offsets_list = new HashMap<>();
        offset_list.forEach(offset -> {
            switch(offset) {
                case x -> offsets_list.put(OffsetType.x, offsets.getX());
                case y -> offsets_list.put(OffsetType.y, offsets.getY());
                case width -> offsets_list.put(OffsetType.width, offsets.getWidth());
                case height -> offsets_list.put(OffsetType.height, offsets.getHeight());
            }
        });
        return offsets_list;
    }
    default Map<OffsetType, Double> generateDefaultOffsetsMap(){
        Map<OffsetType, Double> offsets_list = new HashMap<>();
        offsets_list.put(OffsetType.x, offsets.getX());
        offsets_list.put(OffsetType.y, offsets.getY());
        offsets_list.put(OffsetType.width, offsets.getWidth());
        offsets_list.put(OffsetType.height, offsets.getHeight());
        return offsets_list;
    }

    //TODO add javadoc comment
    default double computeSize(Button button, OffsetType type){
        if(type == OffsetType.x || type == OffsetType.y) throw new IllegalArgumentException("Type must be width or height");
        Text button_text = new Text(button.getText());
        button_text.setFont(new Font(button.getFont().getSize()));
        return type == OffsetType.width ? button_text.getLayoutBounds().getWidth() + 4: button_text.getLayoutBounds().getHeight() + 4;
    }
}
