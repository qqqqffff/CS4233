package escape.gui.states;

import escape.gui.AppState;
import escape.gui.State;
import escape.gui.utilities.BoardManager;
import escape.gui.utilities.DataManager;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSelect extends Group implements AppState {
    public static final int DEFAULT_COL_ITEMS = 8;
    public static final int DEFAULT_ROW_ITEMS = 8;
    private Rectangle background;
    private Text titleText;
    private Group gameSelectTable;
    private Button nextPageButton;
    private Button previousPageButton;
    private Button pageCounter;
    private Button backButton;
    public GameSelect(){
        this.setId(State.GAME_SELECT.name());
        //TODO: remove me when adding css file
        this.setStyle("-fx-font-family: Arial");

        background = new Rectangle(offsets.getX(), offsets.getY(), offsets.getWidth(), offsets.getHeight());
        background.setId("background");
        background.setFill(Color.rgb(0,0,0,0));
        background.setStroke(Color.rgb(0,0,0,1));
        background.setStrokeWidth(5);
        background.setArcWidth(50);
        background.setArcHeight(50);
        this.getChildren().add(background);

        titleText = new Text("Game Select");
        titleText.setId("titleText");
        titleText.setFont(new Font(32));
        titleText.setFill(Color.rgb(0,0,0,1));
        titleText.setLayoutX((DEFAULT_SCREEN_WIDTH - titleText.getLayoutBounds().getWidth()) / 2);
        titleText.setLayoutY(offsets.getY() + titleText.getFont().getSize() + offsets.getHeight() / 16);
        this.getChildren().add(titleText);

        gameSelectTable = BoardManager.drawTable(DEFAULT_ROW_ITEMS, DEFAULT_COL_ITEMS,
                offsets.getWidth() - (offsets.getX() * 2), offsets.getHeight() - (offsets.getY() * 4.5), generateData());
        gameSelectTable.setId("gameSelectTable");
        gameSelectTable.setLayoutX(offsets.getX() * (2));
        gameSelectTable.setLayoutY(titleText.getLayoutY() + (offsets.getY() * 2));
        this.getChildren().add(gameSelectTable);


    }

    @Override
    public void updateLayout(Number width, Number height) {

    }
    @Override
    public void restoreData(Map<String, String> data) {

    }

    @Override
    public Map<String, String> generateData() {
        List<Path> escapeConfigs = DataManager.retrieveEscapeConfigs();
        Map<String, String> data = new HashMap<>();
        escapeConfigs.forEach(path -> data.put("EscapeConfig" + data.size(), path.toString()));
        return data;
    }
}
