package org.aiotlab;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TetrisTest {
    static private TetrisPlayer tetris;
    static private TetrisBoard tetris_board;

    @BeforeAll
    public static void beforeTest() {
        System.setProperty("java.awt.headless", "false");
        tetris_board = new TetrisBoard();

        boolean isHeadless = java.awt.
                GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadless();

        if (isHeadless == false) {
            tetris = new TetrisPlayer(tetris_board);
            tetris.setVisible(true);
        }
    }

    @Test
    public void testRandomMove() {
        tetris_board.start();
        // Random move
        int t = 0;
        try {
            while (t < 100) {
                if (Math.random() > 0.5)
                    tetris_board.getTetrisGame().move_horz(1);
                else
                    tetris_board.getTetrisGame().move_horz(-1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                if (Math.random() > 0.5)
                    tetris_board.getTetrisGame().rotate(false);
                else
                    tetris_board.getTetrisGame().rotate(true);
                t++;
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGameOver() {
        tetris_board.start();
        for (int i=0; i<10; i++) {
            tetris_board.getTetrisGame().dropDown();
            tetris_board.getTetrisGame().update();
        }
        boolean ret = tetris_board.getTetrisGame().isGameOver();
        assertTrue(ret);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testSquareShapes() {
        tetris_board.start();
        for (int x=-6; x<=4; x += 2) {
            tetris_board.getTetrisGame().setNewPiece(Shape.Tetrominoe.SquareShape);
            tetris_board.getTetrisGame().move_horz(x);

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }
            tetris_board.getTetrisGame().dropDown();
        }
        int lines = tetris_board.getTetrisGame().getLinesRemoved();
        assertTrue(lines == 2);
    }


    @Test
    public void testLineShapes() {
        tetris_board.start();
        tetris_board.getTetrisGame().setNewPiece(Shape.Tetrominoe.LineShape);
        tetris_board.getTetrisGame().move_horz(-6);
        tetris_board.getTetrisGame().dropDown();

        tetris_board.getTetrisGame().setNewPiece(Shape.Tetrominoe.LineShape);
        tetris_board.getTetrisGame().move_horz(-5);
        tetris_board.getTetrisGame().dropDown();

        tetris_board.getTetrisGame().setNewPiece(Shape.Tetrominoe.LineShape);
        tetris_board.getTetrisGame().rotate(false);
        tetris_board.getTetrisGame().move_horz(-2);
        tetris_board.getTetrisGame().dropDown();

        tetris_board.getTetrisGame().setNewPiece(Shape.Tetrominoe.LineShape);
        tetris_board.getTetrisGame().rotate(false);
        tetris_board.getTetrisGame().move_horz(2);
        tetris_board.getTetrisGame().dropDown();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        int lines = tetris_board.getTetrisGame().getLinesRemoved();
        assertTrue(lines == 1);
    }
}
