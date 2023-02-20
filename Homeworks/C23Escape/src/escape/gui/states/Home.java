package escape.gui.states;

import escape.gui.AppState;
import escape.gui.Offset.OffsetType;
import escape.gui.utilities.AppStateManager;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import escape.gui.State;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Map;

import static escape.gui.Screen.DEFAULT_LOAD_TIME;

public class Home extends Group implements AppState {
    private Rectangle background;
    private Text titleText;
    private Button playButton;
    private Button testsButton;
    private Button profileButton;
    private Button mapMakerButton;
    private Button settingsButton;
    private Text signatureText;
    public Home(){
        this.setId(State.HOME.name());
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

        titleText = new Text("Escape Game");
        titleText.setId("titleText");
        titleText.setFont(new Font(32));
        titleText.setFill(Color.rgb(0,0,0,1));
        titleText.setLayoutX((DEFAULT_SCREEN_WIDTH - titleText.getLayoutBounds().getWidth()) / 2);
        titleText.setLayoutY(offsets.getY() + titleText.getFont().getSize() + offsets.getHeight() / 16);
        this.getChildren().add(titleText);

        playButton = new Button("Play");
        playButton.setId("playButton");
        playButton.setFont(new Font(20));
        playButton.setLayoutX(offsets.getWidth() / 2);
        playButton.setLayoutY(titleText.getLayoutY() + (offsets.getY() * 2 <= 100 ?  offsets.getY() * 2 : 100) + playButton.getFont().getSize());
        playButton.setOnAction(event -> {
            AppStateManager.transitionStates(State.HOME, State.GAME_SELECT, generateData());
        });
        this.getChildren().add(playButton);

        testsButton = new Button("Tests");
        testsButton.setId("testsButton");
        testsButton.setFont(new Font(20));
        testsButton.setLayoutX(offsets.getWidth() / 2);
        testsButton.setLayoutY(playButton.getLayoutY() + (offsets.getY() <= 50 ?  offsets.getY() : 50) + testsButton.getFont().getSize());
        testsButton.setOnAction(event -> {

        });
        this.getChildren().add(testsButton);

        profileButton = new Button("Profile");
        profileButton.setId("profileButton");
        profileButton.setFont(new Font(20));
        profileButton.setLayoutX(offsets.getWidth() / 2);
        profileButton.setLayoutY(testsButton.getLayoutY() + (offsets.getY() <= 50 ?  offsets.getY() : 50) + profileButton.getFont().getSize());
        profileButton.setOnAction(event -> {

        });
        this.getChildren().add(profileButton);

        mapMakerButton = new Button("Map Maker");
        mapMakerButton.setId("mapMakerButton");
        mapMakerButton.setFont(new Font(20));
        mapMakerButton.setLayoutX(offsets.getWidth() / 2);
        mapMakerButton.setLayoutY(profileButton.getLayoutY() + (offsets.getY() <= 50 ?  offsets.getY() : 50) + mapMakerButton.getFont().getSize());
        mapMakerButton.setOnAction(event -> {

        });
        this.getChildren().add(mapMakerButton);

        settingsButton = new Button("Settings");
        settingsButton.setId("settingsButton");
        settingsButton.setFont(new Font(20));
        settingsButton.setLayoutX((DEFAULT_SCREEN_WIDTH - 94) / 2);
        settingsButton.setLayoutY(mapMakerButton.getLayoutY() + (offsets.getY() <= 50 ?  offsets.getY() : 50) + settingsButton.getFont().getSize());
        settingsButton.setOnAction(event -> {

        });
        this.getChildren().add(settingsButton);

//        Line middleLine = new Line((DEFAULT_SCREEN_WIDTH / 2) - 0.5, 0, (DEFAULT_SCREEN_WIDTH / 2) - 0.5, DEFAULT_SCREEN_HEIGHT);
//        middleLine.setStrokeWidth(1);
//        this.getChildren().add(middleLine);

        signatureText = new Text("Developed by: Apollinaris Rowe");
        signatureText.setId("signatureText");
        signatureText.setFont(new Font(14));
        signatureText.setLayoutX(DEFAULT_SCREEN_WIDTH - signatureText.getLayoutBounds().getWidth() - (offsets.getWidth() / 32));
        signatureText.setLayoutY(offsets.getHeight() + offsets.getY() + ((offsets.getY() + (titleText.getFont().getSize() / 2)) / 2));
        this.getChildren().add(signatureText);

        new Thread(() -> {
            try {
                Thread.sleep(DEFAULT_LOAD_TIME);
                playButton.setLayoutX((DEFAULT_SCREEN_WIDTH - playButton.getWidth()) / 2);
                testsButton.setLayoutX((DEFAULT_SCREEN_WIDTH - testsButton.getWidth()) / 2);
                profileButton.setLayoutX((DEFAULT_SCREEN_WIDTH - profileButton.getWidth()) / 2);
                mapMakerButton.setLayoutX((DEFAULT_SCREEN_WIDTH - mapMakerButton.getWidth()) / 2);
                settingsButton.setLayoutX((DEFAULT_SCREEN_WIDTH - settingsButton.getWidth()) / 2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void updateLayout(Number width, Number height) {

    }
    @Override
    public void restoreData(Map<String, String> data) {

    }

    @Override
    public Map<String, String> generateData() {
        return null;
    }
}
