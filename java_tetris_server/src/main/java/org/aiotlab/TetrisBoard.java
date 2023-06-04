package org.aiotlab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class TetrisBoard extends JPanel {
    private final int INITIAL_DELAY = 100;
    private final int PERIOD_INTERVAL = 300;
    private Timer timer;
    private JLabel statusbar;
    private TetrisGame tetrisGame;

    public TetrisBoard() {
        tetrisGame = new TetrisGame();

        setFocusable(true);
        addKeyListener(new TetrisBoard.TAdapter());
    }

    public void start() {
        tetrisGame.start();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TetrisBoard.ScheduleTask(),
                INITIAL_DELAY, PERIOD_INTERVAL);
    }
    public void setStatusbar(JLabel status_label) { statusbar = status_label; }
    public TetrisGame getTetrisGame() { return tetrisGame; };

    private void doGameCycle() {
        tetrisGame.update();
        if (statusbar != null)
            statusbar.setText("Lines: " + tetrisGame.getLinesRemoved() + "  Height: " + tetrisGame.getAllBricksHeight() + "  Holes: " + tetrisGame.getAllBricksHoles());
        repaint();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        tetrisGame.setDisplaySize(getSize());
        tetrisGame.doDrawing(g);
    }

    private class ScheduleTask extends TimerTask {
        @Override
        public void run() {
            doGameCycle();
            if (tetrisGame.isGameOver())
                timer.cancel();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int keycode = e.getKeyCode();

            if (keycode == KeyEvent.VK_R) {
                // Restart the game
                tetrisGame.start();
            }

            if (!tetrisGame.isStarted()) {
                return;
            }

            if (keycode == KeyEvent.VK_P) {
                tetrisGame.pause();
                return;
            }

            if (tetrisGame.isPaused()) {
                return;
            }

            switch (keycode) {

                case KeyEvent.VK_LEFT:
                    tetrisGame.move_horz(-1);
                    break;

                case KeyEvent.VK_RIGHT:
                    tetrisGame.move_horz(1);
                    break;

                case KeyEvent.VK_DOWN:
                    tetrisGame.rotate(true);
                    break;

                case KeyEvent.VK_UP:
                    tetrisGame.rotate(false);
                    break;

                case KeyEvent.VK_SPACE:
                    tetrisGame.dropDown();
                    break;

                case KeyEvent.VK_P:
                    tetrisGame.pause();
                    break;

                //case KeyEvent.VK_D:
                //    tetrisGame.oneLineDown();
                //    break;
            }

            repaint();
        }
    }
}
