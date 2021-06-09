package com.example.appstream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private final static String SEND_IP = "192.168.0.18";  // Enviar IP
    private final static int SEND_PORT = 8888;               // Enviar número de puerto
    private final static int RECEIVE_PORT = 9999;            // Recibir número de puerto

    private boolean listenStatus = true;  // El identificador de bucle del hilo receptor
    private byte[] buf;
    Bitmap bp;

    private DatagramSocket sendSocket;
    private DatagramSocket rcvSocket;
    private InetAddress serverAddr;
    private SendHandler sendHandler = new SendHandler();
    private ReceiveHandler receiveHandler = new ReceiveHandler();

    private ImageView imgShow;
    private Button btn;

    class ReceiveHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imgShow.setImageBitmap(bp);


        }
    }

    class SendHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imgShow = findViewById(R.id.img_show);
        btn = findViewById(R.id.btn_send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UdpSendThread().start();
            }
        });
        new UdpReceiveThread().start();

    }
    /*
     * Hilo de recepción de datos UDP
     * */
    public class UdpReceiveThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {

                rcvSocket = new DatagramSocket(RECEIVE_PORT);
                serverAddr = InetAddress.getByName(SEND_IP);
                //ByteArrayOutputStream out = new ByteArrayOutputStream();
                while(listenStatus)
                {
                    byte[] inBuf= new byte[1024*1024];
                    DatagramPacket inPacket=new DatagramPacket(inBuf,inBuf.length);
                    //out.write(inPacket.getData());
                    rcvSocket.receive(inPacket);
                    if(!inPacket.getAddress().equals(serverAddr)){
                        throw new IOException("Mensaje desconocido");
                    }

                    ByteArrayInputStream in = new ByteArrayInputStream(inPacket.getData());
                    receiveHandler.sendEmptyMessage(1);
                    bp = BitmapFactory.decodeStream(in);


                }


            } catch (Exception e)
            {
                e.printStackTrace();
            }finally {
                rcvSocket.close();
            }
        }
    }

    /*
     * Hilo de envío de datos UDP
     * */
    public class UdpSendThread extends Thread {
        @Override
        public void run() {
            try {
                buf = "Hola servidor".getBytes();

                // Crea un objeto DatagramSocket, usa el puerto 8888
                sendSocket = new DatagramSocket(8888);

                serverAddr = InetAddress.getByName(SEND_IP);

                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, serverAddr, SEND_PORT);
                sendSocket.send(outPacket);

                sendSocket.close();
                sendHandler.sendEmptyMessage(1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}