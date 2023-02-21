package escape.gui.utilities;

import escape.builder.LocationInitializer;
import escape.gui.Offset;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.Map;

import static escape.gui.Screen.DEFAULT_LOAD_TIME;

public class BoardManager{
    public static final double MAX_TILE_WIDTH = 25;
    public static final double MAX_TILE_HEIGHT = 25;
    public static Group drawTable(int rows, int cols, double width, double height, Map<String, String> data){
        Group group = new Group();
        Offset offsets = new Offset(0,0, width, height);

        Rectangle background = new Rectangle(offsets.getWidth(), offsets.getHeight());
        background.setId("background");
        background.setFill(Color.rgb(0,0,0,0));
        background.setStroke(Color.rgb(0,0,0,1));
        background.setStrokeWidth(3);
        background.setArcWidth(25);
        background.setArcHeight(25);
        group.getChildren().add(background);

        for(int i = 1; i < rows; i++){
            offsets.setY((offsets.getHeight() / rows) * i);
            Line l = new Line(offsets.getX(), offsets.getY(), offsets.getWidth(), offsets.getY());
            l.setStroke(Color.rgb(0,0,0,1));
            l.setStrokeWidth(1);
            l.setId("horizontalLine" + i);
            group.getChildren().add(l);
        }
        offsets.setY(0);
        for(int j = 1; j < cols; j++){
            offsets.setX((offsets.getWidth() / cols) * j);
            Line l = new Line(offsets.getX(), offsets.getY(), offsets.getX(), offsets.getHeight());
            l.setStroke(Color.rgb(0,0,0,1));
            l.setStrokeWidth(1);
            l.setId("verticalLine" + j);
            group.getChildren().add(l);
        }
        offsets.setX(0);

        Button[][] gameIcon = new Button[rows][cols];
        for(int col = 0, count = 0; col < cols && count < data.size(); col++, count++){
            for(int row = 0; row < rows && count < data.size(); row++, count++){
                //TODO: read the coord type and put it in the number system
                Button b = new Button(String.valueOf(count));
                b.setFont(new Font(12));
                b.setId(data.keySet().stream().toList().get(count));
                int configNumber = count;
                b.setOnAction(event -> AppStateManager.launchGame(data.get("EscapeConfig"+ configNumber)));
                gameIcon[row][col] = b;
                group.getChildren().add(b);
            }
        }

        new Thread(() -> {
            try{
                Thread.sleep(DEFAULT_LOAD_TIME);
                for(int row = 0; row < rows; row++){
                    for(int col = 0; col < cols; col++){
                        if(gameIcon[col][row] == null) break;
                        gameIcon[col][row].setLayoutX(((offsets.getWidth() / cols) - gameIcon[col][row].getWidth()) / 2 + ((offsets.getWidth() / cols) * col));
                        gameIcon[col][row].setLayoutY(((offsets.getHeight() / rows) - gameIcon[col][row].getHeight()) / 2 + ((offsets.getHeight() / rows) * row));
                    }
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }).start();

        return group;
    }
    public static Group drawGrid(int maxX, int maxY){
        Group group = new Group();

        return group;
    }
    public static Group drawLocation(LocationInitializer location){
        Group group = new Group();

        return group;
    }
}
