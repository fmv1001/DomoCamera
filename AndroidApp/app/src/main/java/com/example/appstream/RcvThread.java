package com.example.appstream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class RcvThread extends Thread {

    private static String serverIp;    // Server IP
    private static int serverPortSend;// Server sender port
    private DatagramSocket rcvSocket;
    private InetAddress serverAddress;

    Bitmap frameBitMap;
    private boolean running = true;

    private final FrameHandler frameHandler = new FrameHandler();

    private ImageView imgViewCam;

    public RcvThread(ImageView imgViewCam, String serverIp, int serverPortSend) {
        RcvThread.serverIp = serverIp;
        this.serverPortSend = serverPortSend;
        this.imgViewCam = imgViewCam;
        try {
            rcvSocket = new DatagramSocket(null);
            rcvSocket.setReuseAddress(true);
            rcvSocket.bind(new InetSocketAddress(serverPortSend));
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void interrupt() {
        this.running = false;
    }

    @Override
    public void run() {
        try
        {
            while (running) {
                byte[] inBuf= new byte[1024*1024];
                DatagramPacket inPacket = new DatagramPacket(inBuf,inBuf.length);
                rcvSocket.receive(inPacket);

                if(!inPacket.getAddress().equals(InetAddress.getByName(serverIp)))
                    throw new IOException("Mensaje desconocido: " + serverAddress.toString());

                Bitmap b = BitmapFactory.decodeByteArray(inPacket.getData(), 0, inPacket.getLength());
                b = Bitmap.createScaledBitmap(b, imgViewCam.getWidth(), imgViewCam.getHeight(), false);
                frameHandler.sendEmptyMessage(1);
                frameBitMap = b;//Bitmap.createScaledBitmap(b, imgViewCam.getWidth(), imgViewCam.getHeight(), false);
            }
        } catch (Exception e) {
            System.err.println("Error en recepción de imagen: ");
            e.printStackTrace();
        }finally {
            rcvSocket.close();
        }
    }

    class FrameHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imgViewCam.setImageBitmap(frameBitMap);
        }
    }
}
