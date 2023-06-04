package org.aiotlab;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class TetrisPlayer extends JFrame {

    private JLabel statusbar;
    private TetrisBoard board;

    public TetrisPlayer(TetrisBoard board) {
        this.board = board;
        initUI();
    }
    public TetrisPlayer() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        statusbar = new JLabel(" 0");
        statusbar.setFont(new Font("Serif", Font.PLAIN, 32));
        add(statusbar, BorderLayout.NORTH);

        if (board == null)
            board = new TetrisBoard();
        board.setStatusbar(statusbar);
        add(board);
        board.start();

        setTitle("Tetris");
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            TetrisPlayer game = new TetrisPlayer();
            game.setVisible(true);
        });
    }
}