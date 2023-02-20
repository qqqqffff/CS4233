package escape;

import com.google.gson.stream.JsonReader;
import escape.builder.EscapeGameBuilder;
import escape.builder.LocationType;
import org.junit.jupiter.api.Test;

import java.io.FileReader;

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
    public void testCoordinateHex0(){
        assertNotEquals(hex_coord_test_manager.makeCoordinate(1,-1).getRow(), hex_coord_test_manager.makeCoordinate(-1,1).getRow());
        assertNotEquals(hex_coord_test_manager.makeCoordinate(1,-1).getColumn(), hex_coord_test_manager.makeCoordinate(-1,1).getColumn());
        assertEquals(hex_coord_test_manager.makeCoordinate(1,1).getRow(), hex_coord_test_manager.makeCoordinate(1,1).getRow());
        assertEquals(hex_coord_test_manager.makeCoordinate(1,1).getColumn(), hex_coord_test_manager.makeCoordinate(1,1).getColumn());

        assertNull(hex_coord_test_manager.makeCoordinate(-4,-4));
        assertNull(hex_coord_test_manager.makeCoordinate(4,4));
    }
    @Test
    public void testCoordinateHex1(){
        assertEquals(hex_coord_test_builder.getGameInitializer().getCoordinateType(), Coordinate.CoordinateType.HEX);
        assertEquals(hex_coord_test_builder.getGameInitializer().getxMax(),3);
        assertEquals(hex_coord_test_builder.getGameInitializer().getyMax(),3);
        assertEquals(hex_coord_test_builder.getGameInitializer().getPlayers().length,2);
    }
    @Test
    public void testCoordinateHex2(){
        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].locationType, LocationType.LocationTypes.CLEAR);
        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].x,0);
        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].y,0);
    }

    @Test
    public void testSomething() throws Exception{
        JsonReader jsonReader = new JsonReader(new FileReader("test/escape_configs/test.json"));
        jsonReader.beginObject();
        System.out.println(jsonReader.nextName());
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
}
