package org.aiotlab;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class TetrisTestTCPClient {

    int server_port = 10612;
    String server_address = "127.0.0.1";
    Socket socket;
    DataInputStream data_in;
    PrintWriter data_out;  // Send ASCII message only

    private int linesRemoved = -1;
    private boolean isGameOver = true;
    private BufferedImage receivedImage;

    public void connect(String address, int port) throws Exception {
        server_address = address;
        server_port = port;
        connect();
    }
    public void connect() throws Exception {
        socket = new Socket(server_address, server_port);
        data_in = new DataInputStream( socket.getInputStream())  ;
        data_out = new PrintWriter(socket.getOutputStream(),true);
    }

    public void disconnect() throws Exception {
        if (socket != null)
            socket.close();
    }

    public void sendRequest(String msg) throws Exception {
        data_out.println(msg);
        parsePacket(data_in);
    }

    public BufferedImage getScreenshot() {
        return receivedImage;
    }
    public boolean isGameOver() {
        return isGameOver;
    }
    public int getLinesRemoved() {
        return linesRemoved;
    }

    public void start() throws Exception {
        // Fill-in the correct command start
        sendRequest("start");
    }

    public void move(int x) throws Exception {
        // The move command is move [x_distance], for example, the command below
        // "move -1"
        // can move current piece to left
        sendRequest("move " + x);
    }

    public void rotate(Boolean clockwise) throws Exception {
        // 0: counter-clockwise, 1: clock-wise
        if (clockwise)
            sendRequest("rotate 1");
        else
            sendRequest("rotate 0");
    }

    public void dropDown() throws Exception {
        sendRequest("drop");
    }


    private void parsePacket(DataInputStream input) throws IOException {

        isGameOver = (input.readByte() == (byte)1);

        byte [] intBuf = new byte[4];
        input.read(intBuf, 0, 4);
        linesRemoved = byteToint(intBuf);

        input.read(intBuf, 0, 4);
        int img_size = byteToint(intBuf);

        byte [] byte_png = new byte[img_size];
        input.read(byte_png, 0, img_size);

        InputStream is = new ByteArrayInputStream(byte_png);
        receivedImage = ImageIO.read(is);
    }

    private int byteToint(byte [] byteBuf) {
        int num =   (byteBuf[0]<<24)&0xff000000|
                    (byteBuf[1]<<16)&0x00ff0000|
                    (byteBuf[2]<< 8)&0x0000ff00|
                    (byteBuf[3]<< 0)&0x000000ff;
        return num;
    }
}
