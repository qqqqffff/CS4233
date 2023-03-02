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

//        square_coord_test_manager = square_coord_test_builder.makeGameManager();
//        hex_coord_test_builder = new EscapeGameBuilder();
//        hex_coord_test_manager = hex_coord_test_builder.makeGameManager();
    }


    @Test
    public void testSquare_makeCoordinate() throws Exception {
        square_coord_test_manager = new EscapeGameBuilder("test/configs/square_coord_test.egc").makeGameManager();
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
    public void testSquare_initializer() throws Exception {
        square_coord_test_builder = new EscapeGameBuilder("test/configs/square_coord_test.egc");
        assertEquals(square_coord_test_builder.getGameInitializer().getCoordinateType(), Coordinate.CoordinateType.SQUARE);
        assertEquals(square_coord_test_builder.getGameInitializer().getxMax(),2);
        assertEquals(square_coord_test_builder.getGameInitializer().getyMax(),2);
        assertEquals(square_coord_test_builder.getGameInitializer().getPlayers().length, 2);

        assertEquals(square_coord_test_builder.getGameInitializer().getLocationInitializers()[0].locationType, LocationType.CLEAR);
        assertEquals(square_coord_test_builder.getGameInitializer().getLocationInitializers()[0].x,2);
        assertEquals(square_coord_test_builder.getGameInitializer().getLocationInitializers()[0].y,1);
    }

    @Test
    public void testHexManager_makeCoordinate() throws Exception {
        hex_coord_test_manager = new EscapeGameBuilder("test/configs/hex_coord_test.egc").makeGameManager();
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
    public void testSquareCoordinate_moveExit() throws Exception {
        EscapeGameManager<Coordinate> square_move_test = new EscapeGameBuilder("test/configs/square_exit_test.egc").makeGameManager();

        Coordinate dI = square_move_test.makeCoordinate(3,3); //fails (no jump/fly)
        Coordinate sI = square_move_test.makeCoordinate(5,4); //fails (no jump/fly)
        Coordinate bI = square_move_test.makeCoordinate(6,3); //success (has fly)
        Coordinate hI = square_move_test.makeCoordinate(5,2); //success (has jump)
        Coordinate exit = square_move_test.makeCoordinate(5,3);

        GameStatus status = square_move_test.move(dI, exit);
        assertTrue(status.isValidMove());
        assertNull(square_move_test.getPieceAt(exit));
        assertNull(square_move_test.getPieceAt(dI));
        //rip dog

        status = square_move_test.move(sI, exit);
        assertTrue(status.isValidMove());
        assertNull(square_move_test.getPieceAt(exit));
        assertNull(square_move_test.getPieceAt(sI));
        //rip mr snail

        status = square_move_test.move(bI, exit);
        assertTrue(status.isValidMove());
        assertTrue(status.isMoreInformation());
        //bye birdy

        status = square_move_test.move(hI, exit);
        assertTrue(status.isValidMove());
//        //horsey
    }
    @Test
    public void testSquareCoordinate_negativeMove() throws Exception {
        EscapeGameManager<Coordinate> square_move_test = new EscapeGameBuilder("test/configs/square_negative_move_test.egc").makeGameManager();

        Coordinate i = square_move_test.makeCoordinate(-10,-10);
        Coordinate f = square_move_test.makeCoordinate(-15,3);
        GameStatus status = square_move_test.move(i, f);
        assertTrue(status.isValidMove());

        Coordinate t1 = square_move_test.makeCoordinate(0,0);
        Coordinate t2 = square_move_test.makeCoordinate(1,0);
        square_move_test.move(t1,t2);
        //limit testing
        i = f;
        f = square_move_test.makeCoordinate(-150,170);
        status = square_move_test.move(i, f);
        assertTrue(status.isValidMove());
    }
    @Test
    public void testSquareCoordinate_moveDiagonal() throws Exception {
        EscapeGameManager<Coordinate> square_move_test = new EscapeGameBuilder("test/configs/square_diagonal_move_test.egc").makeGameManager();

        //diagonal dog test
        Coordinate i = square_move_test.makeCoordinate(3,3);
        Coordinate f = square_move_test.makeCoordinate(1,1);
        GameStatus status = square_move_test.move(i, f);
        assertTrue(status.isValidMove());

        Coordinate t1 = square_move_test.makeCoordinate(0,0);
        Coordinate t2 = square_move_test.makeCoordinate(1,0);
        square_move_test.move(t1,t2);
        //diagonal dog fail!
        i = f;
        f = square_move_test.makeCoordinate(2,1);
        status = square_move_test.move(i, f);
        assertFalse(status.isValidMove());
    }
    @Test
    public void testSquareCoordinate_moveOrthogonal() throws Exception {
        //(5, 0) clear Chris snail
        EscapeGameManager<Coordinate> square_move_test = new EscapeGameBuilder("test/configs/square_orthogonal_move_test.egc").makeGameManager();

        Coordinate i = square_move_test.makeCoordinate(3,3);
        Coordinate f = square_move_test.makeCoordinate(1,3);
        GameStatus status = square_move_test.move(i, f);
        assertTrue(status.isValidMove());
    }
    @Test
    public void testSquareCoordinate_capture() throws Exception {
        EscapeGameManager<Coordinate> square_move_test = new EscapeGameBuilder("test/configs/square_piece_capture_test.egc.egc").makeGameManager();
    }
    @Test
    public void testSquareCoordinate_moveLinear() throws Exception {
        EscapeGameManager<Coordinate> square_move_test = new EscapeGameBuilder("test/configs/square_linear_move_test.egc").makeGameManager();

        Coordinate t1 = square_move_test.makeCoordinate(0,0);
        Coordinate t2 = square_move_test.makeCoordinate(1,0);


        //checking all directions
        //1
        Coordinate i = square_move_test.makeCoordinate(5,3);
        Coordinate f = square_move_test.makeCoordinate(6,3);
        assertTrue(square_move_test.move(i, f).isValidMove());
        square_move_test.move(t1,t2);
        //2
        i = f;
        f = square_move_test.makeCoordinate(7,4);
        assertTrue(square_move_test.move(i, f).isValidMove());
        square_move_test.move(t2,t1);
        //3
        i = f;
        f = square_move_test.makeCoordinate(7,5);
        assertTrue(square_move_test.move(i, f).isValidMove());
        square_move_test.move(t1,t2);
        //4
        i = f;
        f = square_move_test.makeCoordinate(6,6);
        assertTrue(square_move_test.move(i, f).isValidMove());
        square_move_test.move(t2,t1);
        //5
        i = f;
        f = square_move_test.makeCoordinate(5,6);
        assertTrue(square_move_test.move(i, f).isValidMove());
        square_move_test.move(t1,t2);
        //6
        i = f;
        f = square_move_test.makeCoordinate(4,5);
        assertTrue(square_move_test.move(i, f).isValidMove());
        square_move_test.move(t2,t1);
        //7
        i = f;
        f = square_move_test.makeCoordinate(4,4);
        assertTrue(square_move_test.move(i, f).isValidMove());
        square_move_test.move(t1,t2);
        //8
        i = f;
        f = square_move_test.makeCoordinate(5,3);
        assertTrue(square_move_test.move(i, f).isValidMove());
        square_move_test.move(t2,t1);
        //how'd we end up back here!
    }
    @Test
    public void testSquareCoordinate_moveOmni() throws Exception {
        EscapeGameManager<Coordinate> square_move_test = new EscapeGameBuilder("test/configs/square_omni_move_test.egc").makeGameManager();

        //running full code utilization
        //doggy run test
        Coordinate i = square_move_test.makeCoordinate(3,2);
        Coordinate f = square_move_test.makeCoordinate(1,2);
        assertTrue(square_move_test.move(i, f).isValidMove());
        //run back!
//        System.out.println(square_move_test.getPieceAt(f).getName());

        //for testing sake
        Coordinate t1 = square_move_test.makeCoordinate(0,0);
        Coordinate t2 = square_move_test.makeCoordinate(1,0);
        square_move_test.move(t1, t2);

        i = f;
        f = square_move_test.makeCoordinate(3,2);
        assertTrue(square_move_test.move(i, f).isValidMove());
        square_move_test.move(t2, t1);

        //dog run test (too far!) oh nose
        i = f;
        f = square_move_test.makeCoordinate(1,1);
        assertFalse(square_move_test.move(i, f).isValidMove());
        f = square_move_test.makeCoordinate(3,4);
        assertTrue(square_move_test.move(i, f).isValidMove());
    }
    @Test
    public void testHex_initialization() throws Exception {
        hex_coord_test_builder = new EscapeGameBuilder("test/configs/hex_coord_test.egc");
        assertEquals(hex_coord_test_builder.getGameInitializer().getCoordinateType(), Coordinate.CoordinateType.HEX);
        assertEquals(hex_coord_test_builder.getGameInitializer().getxMax(),20);
        assertEquals(hex_coord_test_builder.getGameInitializer().getyMax(),20);
        assertEquals(hex_coord_test_builder.getGameInitializer().getPlayers().length,2);

        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].getLocationType(), LocationType.BLOCK);
        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].getX(),3);
        assertEquals(hex_coord_test_builder.getGameInitializer().getLocationInitializers()[0].getY(),5);
    }
}
