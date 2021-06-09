package com.example.appstream;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerUDPConnexion extends Thread {

    private byte[] idSenderBuffer;
    private DatagramSocket sendSocket;
    private InetAddress serverAddress;
    private ConnexHandler connexHandler = new ConnexHandler();
    private static int SERVER_PORT_RECV;

    public ServerUDPConnexion(String SERVER_IP, int SERVER_PORT) throws UnknownHostException {
        serverAddress = InetAddress.getByName(SERVER_IP);
        SERVER_PORT_RECV = SERVER_PORT;
    }

    @Override
    public void run() {
        try {
            idSenderBuffer = "Hola servidor".getBytes();

            sendSocket = new DatagramSocket(SERVER_PORT_RECV);
            DatagramPacket outPacket = new DatagramPacket(idSenderBuffer, idSenderBuffer.length,
                    serverAddress, SERVER_PORT_RECV);
            sendSocket.send(outPacket);

            sendSocket.close();
            connexHandler.sendEmptyMessage(1);

        } catch (Exception e) {
            System.err.println("Error en el establecimiento de conexion con servidor: ");
            e.printStackTrace();
        }
    }

    class ConnexHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
