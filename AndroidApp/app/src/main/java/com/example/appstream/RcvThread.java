package com.example.appstream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class RcvThread extends Thread {

    private static String SERVER_IP;    // Server IP
    private static int SERVER_PORT_SEND;// Server sender port

    Bitmap frameBitMap;

    private DatagramSocket rcvSocket;
    private InetAddress serverAddress;

    private final FrameHandler frameHandler = new FrameHandler();

    private ImageView imgViewCam;

    public RcvThread(ImageView img_view_cam, String SERVER_IP, int SERVER_PORT) throws UnknownHostException {
        this.SERVER_IP =SERVER_IP;
        serverAddress = InetAddress.getByName(SERVER_IP);
        SERVER_PORT_SEND = SERVER_PORT;
        this.imgViewCam = img_view_cam;
    }

    @Override
    public void run()
    {
        try
        {
            rcvSocket = new DatagramSocket(null);
            rcvSocket.setReuseAddress(true);
            rcvSocket.bind(new InetSocketAddress(SERVER_PORT_SEND));
            serverAddress = InetAddress.getByName(SERVER_IP);

            while (true) {
                byte[] inBuf= new byte[1024*1024];
                DatagramPacket inPacket = new DatagramPacket(inBuf,inBuf.length);
                rcvSocket.receive(inPacket);

                if(!inPacket.getAddress().equals(serverAddress))
                    throw new IOException("Mensaje desconocido");

                ByteArrayInputStream in = new ByteArrayInputStream(inPacket.getData());
                frameHandler.sendEmptyMessage(1);
                frameBitMap = BitmapFactory.decodeStream(in);
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
