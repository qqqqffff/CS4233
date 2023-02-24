package escape;

import com.google.gson.stream.JsonReader;
import escape.builder.EscapeGameBuilder;
import escape.builder.EscapeGameInitializer;
import escape.builder.EscapeJsonConverter;
import escape.required.EscapeException;
import escape.required.GameStatus;
import escape.required.LocationType;
import escape.required.Coordinate;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.sql.SQLOutput;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class EscapeGameBuilderTest {
//    private final EscapeGameBuilder square_coord_test_builder;
//    private final EscapeGameManager square_coord_test_manager;
    private final EscapeGameBuilder hex_coord_test_builder;
    private final EscapeGameManager hex_coord_test_manager;
    EscapeGameBuilderTest() throws Exception {
//        square_coord_test_builder = new EscapeGameBuilder("test/escape_configs/escape_square_coord_test.egc");
//        square_coord_test_manager = square_coord_test_builder.makeGameManager();
        hex_coord_test_builder = new EscapeGameBuilder("test/escape_configs/escape_hex_coord_test.json");
        hex_coord_test_manager = hex_coord_test_builder.makeGameManager();
    }


    @Test
    public void testCoordinateSquare0(){
//        assertNotEquals(square_coord_test_manager.makeCoordinate(0, 1).getRow(), square_coord_test_manager.makeCoordinate(1, 0).getRow());
//        assertNotEquals(square_coord_test_manager.makeCoordinate(0, 1).getColumn(), square_coord_test_manager.makeCoordinate(1, 0).getColumn());
//        assertEquals(square_coord_test_manager.makeCoordinate(2,1).getRow(), square_coord_test_manager.makeCoordinate(2,1).getRow());
//        assertEquals(square_coord_test_manager.makeCoordinate(2,1).getColumn(), square_coord_test_manager.makeCoordinate(2,1).getColumn());
//
//        assertNull(square_coord_test_manager.makeCoordinate(-1,-1));
//        assertNull(square_coord_test_manager.makeCoordinate(3,3));
    }
    @Test
    public void testCoordinateSquare1(){
//        assertEquals(square_coord_test_builder.getGameInitializer().getCoordinateType(), Coordinate.CoordinateType.SQUARE);
//        assertEquals(square_coord_test_builder.getGameInitializer().getxMax(),2);
//        assertEquals(square_coord_test_builder.getGameInitializer().getyMax(),2);
//        assertEquals(square_coord_test_builder.getGameInitializer().getPlayers().length, 2);
    }
    @Test
    public void testCoordinateSquare2(){
//        assertEquals(square_coord_test_builder.getGameInitializer().getLocationInitializers()[0].locationType, LocationType.LocationTypes.CLEAR);
//        assertEquals(square_coord_test_builder.getGameInitializer().getLocationInitializers()[0].x,2);
//        assertEquals(square_coord_test_builder.getGameInitializer().getLocationInitializers()[0].y,1);
    }

    @Test
    public void testHexManager_makeCoordinate(){
        Coordinate coord1 = hex_coord_test_manager.makeCoordinate(1,-1);
        Coordinate coord2 = hex_coord_test_manager.makeCoordinate(1,-1);
        Coordinate coord3 = hex_coord_test_manager.makeCoordinate(-1,1);
        assertNotEquals(coord1.getRow(), coord3.getRow());
        assertNotEquals(coord1.getColumn(), coord3.getColumn());
        assertEquals(coord1.getRow(), coord2.getRow());
        assertEquals(coord1.getColumn(), coord2.getColumn());
        assertTrue(coord1.equals(coord2));

        assertThrows(EscapeException.class, () -> hex_coord_test_manager.makeCoordinate(-4,-4));
        assertThrows(EscapeException.class, () -> hex_coord_test_manager.makeCoordinate(4,4));
    }

    @Test
    public void testManagerHexManager_move(){
        Coordinate i = hex_coord_test_manager.makeCoordinate(0,0);
        Coordinate f = hex_coord_test_manager.makeCoordinate(1,1);

        GameStatus status = hex_coord_test_manager.move(i, f);
    }
    @Test
    public void testBuilder_initialization(){
        assertEquals(hex_coord_test_builder.getGameInitializer().getCoordinateType(), Coordinate.CoordinateType.HEX);
        assertEquals(hex_coord_test_builder.getGameInitializer().getxMax(),3);
        assertEquals(hex_coord_test_builder.getGameInitializer().getyMax(),3);
        assertEquals(hex_coord_test_builder.getGameInitializer().getPlayers().length,2);

        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].getLocationType(), LocationType.CLEAR);
        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].getX(),0);
        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].getY(),0);
    }

    @Test
    public void testESGCJson() throws Exception{
        JsonReader jsonReader = new JsonReader(new FileReader("test/escape_configs/test.json"));
        jsonReader.beginObject();
        System.out.println(jsonReader.nextName() + ", " + jsonReader.nextString() + "\n" + jsonReader.nextName());
        jsonReader.beginArray();
        while(jsonReader.hasNext()) {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                System.out.println(jsonReader.nextName() + ", " + jsonReader.nextString());
            }
            jsonReader.endObject();
        }
        jsonReader.endArray();

        System.out.println(jsonReader.nextName());
        jsonReader.beginArray();
        while(jsonReader.hasNext()){
            System.out.println(jsonReader.nextString());
        }
        jsonReader.endArray();

        System.out.println(jsonReader.nextName());
        jsonReader.beginObject();
        while(jsonReader.hasNext()){
            System.out.println(jsonReader.nextName() + ", " + jsonReader.nextString());
        }
        jsonReader.endObject();

        jsonReader.close();
    }

    @Test
    public void testEGConverter() throws Exception{
//        EscapeGameInitializer egi = EscapeJsonConverter.esgConfigConverter("test/escape_configs/escape_square_coord_test.egc");

        EscapeGameInitializer egi2 = EscapeJsonConverter.esgConfigConverter("test/escape_configs/escape_hex_coord_test.egc");
        EscapeGameInitializer egi3 = new EscapeGameBuilder("test/escape_configs/escape_hex_coord_test.json").getGameInitializer();
        System.out.println(egi3.getCoordinateType());
        System.out.println(egi3.getxMax() + ", " + egi3.getyMax());
        System.out.println(Arrays.toString(egi3.getPlayers()));
        System.out.println(Arrays.toString(egi3.getLocationInitializers()));
        System.out.println(Arrays.toString(egi3.getPieceTypes()));
        System.out.println(Arrays.toString(egi3.getRules()));
    }
}
