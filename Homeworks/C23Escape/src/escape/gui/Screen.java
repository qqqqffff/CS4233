package escape.gui;

import escape.gui.utilities.AppStateManager;
import escape.gui.utilities.DataManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Screen extends Application {
    public static final double DEFAULT_WIDTH = 800;
    public static final double DEFAULT_HEIGHT = 800;
    private final Pane root;
    private final Scene scene;
    public Screen(){
        AppStateManager.init();
        this.root = new Pane();

        this.scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    @Override
    public void start(Stage stage) throws Exception {
        root.getChildren().add(AppStateManager.getActiveAppState());
        root.setStyle("-fx-font-family: Arial");

        stage.setTitle("PW Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
