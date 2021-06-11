package com.example.appstream;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private ImageView imgViewCam1;
    private String serverip = "192.168.18.11";
    Thread rcvThread1 = null;
    Thread rcvThread2 = null;
    Thread connexionThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Starting app...", Toast.LENGTH_SHORT).show();
        ImageView color_connexion = (ImageView) findViewById(R.id.color_conn);
        Resources res = this.getResources();
        final int green = res.getColor(R.color.grenn_conn);
        color_connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connexionThread.isAlive())
                    color_connexion.setColorFilter(green, PorterDuff.Mode.SRC_ATOP);
            }
        });
        try {
            connexionThread = new ServerTCPConnexion(serverip,8888);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void conn_to_server(View view){
        if(!connexionThread.isAlive())
            connexionThread.start();
        Toast.makeText(this, "Conexion establecida", Toast.LENGTH_SHORT).show();
    }

    public void back_to_start_page(View view){
        setContentView(R.layout.activity_main);
        if (rcvThread1!=null && rcvThread1.isAlive())
            rcvThread1.interrupt();
        if (rcvThread2!=null && rcvThread2.isAlive())
            rcvThread2.interrupt();
    }

    public void connex1(View view){
        setContentView(R.layout.activity_main2);
        imgViewCam1 = findViewById(R.id.img_view_cam1);
        try {
            rcvThread1 = new RcvThread(imgViewCam1, serverip, 9999);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (!rcvThread1.isAlive())
            rcvThread1.start();

    }

    public void connex2(View view){
        setContentView(R.layout.activity_main2);
        imgViewCam1 = findViewById(R.id.img_view_cam1);
        try {
            rcvThread2 = new RcvThread(imgViewCam1, serverip, 9988);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (!rcvThread2.isAlive())
            rcvThread2.start();
    }

    public void to_settings(View view){
        setContentView(R.layout.activity_settings);
    }
}
