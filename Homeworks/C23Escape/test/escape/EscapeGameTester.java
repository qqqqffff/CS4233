package escape;

import escape.builder.EscapeGameBuilder;
import escape.required.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EscapeGameTester {
    private EscapeGameBuilder square_coord_test_builder;
    private EscapeGameManager<Coordinate> square_coord_test_manager;
    private EscapeGameBuilder hex_coord_test_builder;
    private EscapeGameManager<Coordinate> hex_coord_test_manager;
    public EscapeGameTester() throws Exception {
//        square_coord_test_builder = new EscapeGameBuilder("test/configs/square_coord_test.egc");
//        square_coord_test_manager = square_coord_test_builder.makeGameManager();
//        hex_coord_test_builder = new EscapeGameBuilder("test/configs/hex_coord_test.egc");
//        hex_coord_test_manager = hex_coord_test_builder.makeGameManager();
    }


    @Test
    public void testSquare_makeCoordinate(){
        Coordinate coord1 = square_coord_test_manager.makeCoordinate(0, 1);
        Coordinate coord2 = square_coord_test_manager.makeCoordinate(1, 0);
        Coordinate coord3 = square_coord_test_manager.makeCoordinate(0, 1);
        assertFalse(coord1.equals(coord2));
        assertTrue(coord1.equals(coord3));

        assertEquals(coord1.getRow(), coord3.getRow());
        assertNotEquals(coord1.getColumn(), coord2.getColumn());

        assertThrows(EscapeException.class, () -> square_coord_test_manager.makeCoordinate(-1,-1));
        assertThrows(EscapeException.class, () -> square_coord_test_manager.makeCoordinate(3,3));
    }
    @Test
    public void testSquare_initializer(){
        assertEquals(square_coord_test_builder.getGameInitializer().getCoordinateType(), Coordinate.CoordinateType.SQUARE);
        assertEquals(square_coord_test_builder.getGameInitializer().getxMax(),2);
        assertEquals(square_coord_test_builder.getGameInitializer().getyMax(),2);
        assertEquals(square_coord_test_builder.getGameInitializer().getPlayers().length, 2);

        assertEquals(square_coord_test_builder.getGameInitializer().getLocationInitializers()[0].locationType, LocationType.CLEAR);
        assertEquals(square_coord_test_builder.getGameInitializer().getLocationInitializers()[0].x,2);
        assertEquals(square_coord_test_builder.getGameInitializer().getLocationInitializers()[0].y,1);
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

        assertThrows(EscapeException.class, () -> hex_coord_test_manager.makeCoordinate(-21,-21));
        assertThrows(EscapeException.class, () -> hex_coord_test_manager.makeCoordinate(21,21));
    }

    @Test
    public void testManagerHexManager_move_clear(){
//        Coordinate i = hex_coord_test_manager.makeCoordinate(0,0);
//        assertEquals(hex_coord_test_manager.getPieceAt(i).getName(), EscapePiece.PieceName.DOG);
//        Coordinate f = hex_coord_test_manager.makeCoordinate(1,1);
//        assertNull(hex_coord_test_manager.getPieceAt(f));
//
//        GameStatus status = hex_coord_test_manager.move(i, f);
//        assertTrue(f.equals(status.finalLocation()));
//        assertTrue(status.isValidMove());
    }
    @Test
    public void testSquareCoordinate_move() throws Exception {
        EscapeGameManager<Coordinate> square_move_test = new EscapeGameBuilder("test/configs/square_move_test.egc").makeGameManager();
        Coordinate i = square_move_test.makeCoordinate(2,0);
        Coordinate f = square_move_test.makeCoordinate(2,6);
        GameStatus status = square_move_test.move(i, f);
        System.out.println(square_move_test.getPieceAt(f).getName());
    }
    @Test
    public void testBuilder_initialization(){
        assertEquals(hex_coord_test_builder.getGameInitializer().getCoordinateType(), Coordinate.CoordinateType.HEX);
        assertEquals(hex_coord_test_builder.getGameInitializer().getxMax(),20);
        assertEquals(hex_coord_test_builder.getGameInitializer().getyMax(),20);
        assertEquals(hex_coord_test_builder.getGameInitializer().getPlayers().length,2);

        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].getLocationType(), LocationType.BLOCK);
        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].getX(),3);
        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].getY(),5);
    }
}
