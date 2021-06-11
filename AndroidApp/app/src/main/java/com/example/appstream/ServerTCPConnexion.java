package com.example.appstream;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.io.DataInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerTCPConnexion extends Thread {

    private byte[] idSenderBuffer;
    private Socket sendSocket;
    private InetAddress serverAddress;
    private ConnexHandler connexHandler = new ConnexHandler();
    private static int SERVER_PORT_RECV;
    private static String SERVER_IP;
    private DataOutputStream socketSalida;
    private DataInputStream socketEntrada;
    private boolean running = true;

    public ServerTCPConnexion(String SERVER_IP, int SERVER_PORT) throws UnknownHostException {
        serverAddress = InetAddress.getByName(SERVER_IP);
        SERVER_PORT_RECV = SERVER_PORT;
        this.SERVER_IP = SERVER_IP;
    }

    @Override
    public void run() {
        try {
            idSenderBuffer = "Hola servidor".getBytes();

            sendSocket = new Socket(SERVER_IP, SERVER_PORT_RECV);
            socketSalida = new DataOutputStream(sendSocket.getOutputStream());
            socketEntrada = new DataInputStream(sendSocket.getInputStream());
            socketSalida.write(1);
            connexHandler.sendEmptyMessage(1);

        } catch (Exception e) {
            System.err.println("Error en el establecimiento de conexion con servidor: ");
            e.printStackTrace();
        }
        while(running){
            try {
                socketEntrada.readInt();
            } catch (IOException e) {
                stop_run();
            }
        }
        stop_run();
    }

    class ConnexHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public void stop_run(){
        try {
            socketSalida.close();
            socketEntrada.close();
            sendSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
