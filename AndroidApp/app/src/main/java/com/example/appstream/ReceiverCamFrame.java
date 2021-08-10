package com.example.appstream;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class RecieverCamFrame extends AppCompatActivity {

    private static String SERVER_IP = "192.168.18.11";    // Server IP
    private static int SERVER_PORT_SEND = 8888;// Server sender port
    private DatagramSocket rcvSocket;
    private InetAddress serverAddress;

    Bitmap frameBitMap;
    private boolean running = true;

    private final FrameHandler frameHandler = new FrameHandler();

    private ImageView imgViewCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_view);
        try {
            serverAddress = InetAddress.getByName(SERVER_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        imgViewCam = (ImageView) findViewById(R.id.img_view_cam1);
        //Iniciando sockets
        try {
            rcvSocket = new DatagramSocket(null);
            rcvSocket.setReuseAddress(true);
            rcvSocket.bind(new InetSocketAddress(SERVER_PORT_SEND));
            serverAddress = InetAddress.getByName(SERVER_IP);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        //Comenzamos a recibir
        try
        {
            while (running) {
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

    class FrameHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imgViewCam.setImageBitmap(frameBitMap);
        }
    }
}

