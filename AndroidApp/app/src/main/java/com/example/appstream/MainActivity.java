package com.example.appstream;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private ImageView imgViewCam1;
    private ImageView imgViewCam2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toast.makeText(this, "Starting app...", Toast.LENGTH_SHORT).show();

        imgViewCam1 = findViewById(R.id.img_view_cam1);
        imgViewCam2 = findViewById(R.id.img_view_cam2);
        Button connexButton = findViewById(R.id.button_conex_cam1);
        String serverip = "192.168.18.11";
        connexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread rcvThread1 = null;
                Thread rcvThread2 = null;
                Thread connexionThread = null;
                try {
                    connexionThread = new ServerUDPConnexion(serverip,8888);
                    rcvThread2 = new RcvThread(imgViewCam2,serverip,9999);
                    rcvThread1 = new RcvThread(imgViewCam1,serverip,9988);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                connexionThread.start();
                rcvThread2.start();
                rcvThread1.start();
            }
        });
    }
}
