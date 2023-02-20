package escape.gui;

import escape.gui.utilities.AppStateManager;
import escape.gui.utilities.DataManager;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Screen extends Application {
    public static final double DEFAULT_WIDTH = 800;
    public static final double DEFAULT_HEIGHT = 800;
    public static final long DEFAULT_LOAD_TIME = 200;
    private static final Pane root = new Pane();
    private final Scene scene;
    public Screen(){
        AppStateManager.init();
        DataManager.init();


        this.scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    @Override
    public void start(Stage stage) throws Exception {
        root.setStyle("-fx-font-family: Arial");

        stage.setTitle("Escape Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void removeChild(String id){
        root.getChildren().removeIf(node -> node.getId().equals(id));
    }
    public static void addChild(Node child){
        root.getChildren().add(child);
    }
}
