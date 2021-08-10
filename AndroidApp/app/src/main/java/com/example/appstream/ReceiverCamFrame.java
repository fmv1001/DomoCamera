package com.example.appstream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.net.UnknownHostException;

public class ReceiverCamFrame extends AppCompatActivity {

    private static String SERVER_IP;    // Server IP
    private static int SERVER_PORT_SEND;// Server sender port

    private ImageView imgViewCam;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_view);
        SERVER_IP = getIntent().getStringExtra("server_ip");
        SERVER_PORT_SEND = getIntent().getIntExtra("server_port", 9999);
        name = getIntent().getStringExtra("name");
        /*
        try {
            serverAddress = InetAddress.getByName(SERVER_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
         */
        imgViewCam = (ImageView) findViewById(R.id.img_view_cam1);

        Toast.makeText(this, "Iniciando conexion", Toast.LENGTH_SHORT).show();
        Thread rcv1 = null;
        try {
            rcv1 = new RcvThread(imgViewCam, SERVER_IP, SERVER_PORT_SEND);
            rcv1.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void back_to_home(View view){
        Intent pagN = new Intent(this, NavMenuActivity.class);
        startActivity(pagN);
    }

    @Override
    public void onStop() {
        ServerTCPConnexion connexion = null;
        try {
            connexion = ServerTCPConnexion.getInstance(null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        connexion.stop_camera(name);
        super.onStop();
    }
}

