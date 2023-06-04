package org.aiotlab;

import org.aiotlab.Shape.Tetrominoe;

import java.awt.*;


public class TetrisGame {

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private int displayWidth = 100;
    private int displayHeight = 200;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private org.aiotlab.Shape curPiece;
    private Tetrominoe[] board;
    private Color[] brick_colors;
    private String displayText;


    public TetrisGame() {
        curPiece = new org.aiotlab.Shape();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        brick_colors = new Color[]{
                new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };

        clearBoard();
    }

    public void start() {
        isStarted = true;
        isGameOver = false;
        isFallingFinished = false;
        displayText = "Lines: 0";
        clearBoard();
        newPiece();
        numLinesRemoved = 0;
    }
    public void pause() {
        if (!isStarted) {
            return;
        }
        isPaused = !isPaused;
        if (isPaused) {
            displayText = "paused";
        } else {
            displayText = "Lines: " + String.valueOf(numLinesRemoved);
        }
    }
    public boolean move_horz(int x_val) {
        return tryMove(getCurPiece(), curX + x_val, curY);
    }
    public boolean rotate(boolean clockwise) {
        org.aiotlab.Shape piece;
        // In Zetcode example, default rotation is counter-clockwise
        if (clockwise)
            piece = getCurPiece().rotateLeft();
        else
            piece = getCurPiece().rotateRight();
        return tryMove(piece, curX, curY);
    }
    public void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            --newY;
        }
        pieceDropped();
    }
    public void setNewPiece(Tetrominoe shape) {
        curPiece.setShape(shape);
        initPiece();
        isFallingFinished = false;
    }
    public void update() {
        if (isPaused) {
            return;
        }
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }
    public void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }
    public void doDrawing(Graphics g) {
        doResizeDrawing(g, displayHeight, squareWidth(), squareHeight());
    }
    public void doResizeDrawing(Graphics g, int displayH, int squareW, int squareH) {
        int boardTop = displayH - BOARD_HEIGHT * squareH;

        for (int i = 0; i < BOARD_HEIGHT; ++i) {
            for (int j = 0; j < BOARD_WIDTH; ++j) {

                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Tetrominoe.NoShape) {

                    drawSquare(g, 0 + j * squareW,
                            boardTop + i * squareH, shape);
                }
            }
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {
            for (int i = 0; i < 4; ++i) {

                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareW,
                        boardTop + (BOARD_HEIGHT - y - 1) * squareH,
                        curPiece.getShape());
            }
        }
    }
    public void setDisplaySize(Dimension size) {
        displayWidth = size.width;
        displayHeight = size.height;
    }

    public boolean isGameOver() { return isGameOver; }
    public boolean isStarted () {
        return isStarted && (curPiece.getShape() != org.aiotlab.Shape.Tetrominoe.NoShape);
    }
    public boolean isPaused() { return isPaused; }
    public String getDisplayText() { return displayText; }
    public int getLinesRemoved() {
        return numLinesRemoved;
    }

    public int getBOARD_HEIGHT() { return BOARD_HEIGHT; }
    public int getBOARD_WIDTH() { return BOARD_WIDTH; }

    public int getAllBricksHeight() {
        int height = 0;
        // (0, 0) is bottom-left
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            int x;
            for (x = 0; x < BOARD_WIDTH; x++) {
                if (shapeAt(x, y) != Tetrominoe.NoShape) {
                    height++;
                    break;
                }
            }
            if (x == BOARD_WIDTH) // Empty line
                break;
        }
        return height;
    }

    public int getAllBricksHoles() {
        int total_holes = 0;
        for (int x = 0; x < BOARD_WIDTH; x++) {
            int line_holes = 0;
            boolean bFindTop = false;
            for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
                if (!bFindTop) {
                    // Skip NoShape and find top first
                    if (shapeAt(x, y) != Tetrominoe.NoShape)
                        bFindTop = true;
                } else {
                    // Count holes
                    if (shapeAt(x, y) == Tetrominoe.NoShape)
                        line_holes++;
                }
            }
            total_holes += line_holes;
        }
        return total_holes;
    }

    private int squareWidth() {
        return (int) displayWidth / BOARD_WIDTH;
    }
    private int squareHeight() {
        return (int) displayHeight / BOARD_HEIGHT;
    }

    private Tetrominoe shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }

    private org.aiotlab.Shape getCurPiece() {
        return curPiece;
    }
    private boolean tryMove(Shape newPiece, int newX, int newY) {

        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }
            if (shapeAt(x, y) != Tetrominoe.NoShape) {
                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;

        return true;
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; ++i) {
            board[i] = Tetrominoe.NoShape;
        }
    }

    private void pieceDropped() {

        for (int i = 0; i < 4; ++i) {

            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }

        removeFullLines();

        // 2023/5/22 K. T.
        // TODO: Replace newPiece() with isFallingFinished = true
        //       Double-check if it is ok
        isFallingFinished = true;
        /*
        if (!isFallingFinished) {
            newPiece();
        }*/
    }

    private void newPiece() {
        curPiece.setRandomShape();
        initPiece();
    }

    private boolean initPiece() {

        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoe.NoShape);

            isStarted = false;
            displayText = "Game over. Your Score is " + numLinesRemoved;
            isGameOver = true;

            return false;
        } else {
            return true;
        }
    }

    private void removeFullLines() {

        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BOARD_WIDTH; ++j) {

                if (shapeAt(j, i) == Tetrominoe.NoShape) {

                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {

                ++numFullLines;

                for (int k = i; k < BOARD_HEIGHT - 1; ++k) {
                    for (int j = 0; j < BOARD_WIDTH; ++j) {

                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            displayText = "Lines: " +  String.valueOf(numLinesRemoved);
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoe shape) {

        Color color = brick_colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    /*
    // Get all shape ID
    public byte [] getScreenShapes() {
        byte [] bBoard = new byte[board.length];
        for(int i = 0; i < board.length; i++) {
            //bBoard[i] = ByteBuffer.allocate(4).putInt(board[i].ordinal()).array()[3];
            bBoard[i] = (byte)board[i].ordinal(); // We only need last byte
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {
            byte currID = (byte)curPiece.getShape().ordinal();
            for (int i = 0; i < 4; ++i) {

                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                bBoard[y*BOARD_WIDTH + x] = currID;
            }
        }
        return bBoard;
    }
     */
}