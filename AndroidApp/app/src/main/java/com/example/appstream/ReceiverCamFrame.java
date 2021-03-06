package com.example.appstream;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.UnknownHostException;

import static android.os.Environment.getExternalStorageDirectory;

public class ReceiverCamFrame extends AppCompatActivity {

    private ImageView imgViewCam;
    private String name;
    static ServerTCPConnexion connexion = null;
    private int height;
    RcvThread rcv1 = null;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_view);
        context = this;
        ImageView recButton = (ImageView) findViewById(R.id.rec_video);
        // Server IP
        String serverIp = getIntent().getStringExtra("server_ip");
        // Server sender port
        int serverPortSend = getIntent().getIntExtra("server_port", 9999);
        name = getIntent().getStringExtra("name");

        getInstanceServerTCPConnexion();

        if (connexion != null)
            connexion.startCamera(name);

        imgViewCam = (ImageView) findViewById(R.id.img_view_cam1);
        height = imgViewCam.getLayoutParams().height;


        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.hide();
        
        try {
            rcv1 = new RcvThread(imgViewCam, serverIp, serverPortSend);
            rcv1.start();
        } catch (Exception e) {
            Log.println(Log.ERROR,"12",e.getMessage());
        }
        File file = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcv1.recVideo(file);
                Toast.makeText(context, "grabando",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private static void getInstanceServerTCPConnexion(){
        try {
            connexion = ServerTCPConnexion.getInstance();
        } catch (Exception e) {
            Log.println(Log.ERROR,"12",e.getMessage());
        }
    }

    public void back_to_home(View view){
        Intent pagN = new Intent(this, NavMenuActivity.class);
        startActivity(pagN);
    }

    @Override
    public void onStop() {
        if (connexion != null)
            connexion.stopCamera(name);
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        float densityFactor = this.getResources().getDisplayMetrics().density;
        // Checks the orientation of the screen
        TextView rec = (TextView)  findViewById(R.id.rec_text);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rec.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams linearLayoutParams;
            linearLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            float leftRightMargin = densityFactor*60;
            linearLayoutParams.setMargins((int) leftRightMargin,0,(int) leftRightMargin,0);
            imgViewCam.setLayoutParams(linearLayoutParams);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            rec.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams linearLayoutParams;
            linearLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    height);
            float topMargin = densityFactor*30;
            linearLayoutParams.setMargins(0,(int) topMargin,0,0);
            imgViewCam.setLayoutParams(linearLayoutParams);
        }
    }
}

