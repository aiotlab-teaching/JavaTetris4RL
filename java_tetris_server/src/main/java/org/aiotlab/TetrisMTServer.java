package org.aiotlab;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class TetrisMTServer {
    private int port = 10612;

    public TetrisMTServer() {
    }

    public void startServer() {
        TetriServerThread server = new TetriServerThread();
        server.start();
    }

    // The function is called after receiving and parsing client command
    public void updateCallback() {

    }
    public void showMessage(String msg) {
        System.out.println(msg);
    }

    class TetriServerThread extends Thread {

        public void run() {
            try {
                ServerSocket server = new ServerSocket(port);

                while (true) {
                    Socket client = server.accept();
                    new ClientHandlerThread(client).start();

                } // Server loop
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    class ClientHandlerThread extends Thread {
        private TetrisGame tetrisGame;
        private Socket client_socket;

        public ClientHandlerThread(Socket sock) {
            client_socket = sock;
            tetrisGame = new TetrisGame();
        }

        public void run() {
            showMessage("Client has joined the game");
            try{
                BufferedReader input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
                OutputStream output = client_socket.getOutputStream();

                String line;
                try {
                    while (true) {
                        line = input.readLine();
                        if (line == null) // Client disconnected
                            break;

                        parseRequest(line);
                        tetrisGame.update();
                        updateCallback();

                        if (!writeResponse(output)) // Connot send message
                            break;

                    } // Request processing loop
                    client_socket.close();
                    showMessage("Client has exited the game");

                } catch (SocketException se) {
                    System.out.println(se.getMessage());
                }
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        /*
        Server -> Client
        The response is binary data (1 + 4 + img_size + PNG image).
        Data foramt:
            Is game over  (1 byte)   - 1-byte boolean. 0: False, 1: Ture (Game over)
            Removed lines (4 bytes)  - Number of lines removed. 4-byte integer.
            Image size (4 bytes)     - Bytes of the PNG image
            PNG image (variable)     - Screenshot (100*200*3) in PNG format
        */
        private boolean writeResponse(OutputStream outStream) {

            try {
                byte game_state = (byte) (tetrisGame.isGameOver() ? 1 : 0);
                outStream.write(game_state);
                int lines = tetrisGame.getLinesRemoved();
                byte [] line_num = ByteBuffer.allocate(4).putInt(lines).array();
                outStream.write(line_num);

                byte [] img = getScreenshot();
                byte [] img_size = ByteBuffer.allocate(4).putInt(img.length).array();
                outStream.write(img_size);
                outStream.flush();

                outStream.write(img);
                outStream.flush();
            }
            catch (Exception exp) {
                exp.printStackTrace();
                return false;
            }

            return true;
        }

        /*
           Our Tetris commands are listed below:

           Client -> Server
               start                - Start the game
               move X               - Move current piece horizontally
               rotate [0 | 1]       - Rotate 0: counter-clockwise, 1: clockwise
               drop
               line_down            - Force to remove 1 line (1 line down). Reserved for reinforcement learning

         */
        public boolean parseRequest(String request) {

            String [] cmds = request.split(" ");

            try {
                switch (cmds[0]) {

                    case "start":
                        tetrisGame.start();
                        break;

                    case "move":
                        int x_val = Integer.parseInt(cmds[1]);
                        tetrisGame.move_horz(x_val);
                        break;

                    case "rotate": // 0: counterclockwise, 1: Clockwise
                        boolean clockwise = (cmds[1].charAt(0) == '1'); // Let's make != 1 c-clockwise
                        tetrisGame.rotate(clockwise);
                        break;

                    case "drop":
                        tetrisGame.dropDown();
                        break;

                    case "new_piece":
                        int id = Integer.parseInt(cmds[1]);
                        tetrisGame.dropDown();
                        tetrisGame.setNewPiece(Shape.Tetrominoe.getById(id));
                        break;
                    /*
                    case "line_down":
                        tetrisGame.oneLineDown();
                        break;
                    */

                    default:
                        System.out.println("Unknown client request: " + request);
                        return false;
                }

                return true;
            }
            catch (Exception exp) {
                System.out.println("Error while parsing command: " + request);
                System.out.println(exp.getMessage());
                return false;
            }
        }

        byte [] getScreenshot() {
            byte[] imageData = null;

            BufferedImage bImg = new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB);
            Graphics g = bImg.getGraphics();
            tetrisGame.doDrawing(g);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            try {
                ImageIO.write(bImg, "PNG", bs);
                imageData = bs.toByteArray();
            }
            catch (IOException exp) {
                exp.printStackTrace();
            }
            return imageData;
        }
    } // End of ServerThread
}
