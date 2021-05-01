package com.example.appstream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.*;

public class MainActivity extends AppCompatActivity {

    private TextView txv_1;
    private Socket socket;
    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Iniciando app...", Toast.LENGTH_SHORT).show();
        txv_1 = (TextView) findViewById(R.id.msg_1);
        Button connexion = (Button) findViewById(R.id.button_conex);
        handler = new Handler();
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread connexionThread = new ServerConnexion(txv_1, handler);
                connexionThread.start();
            }
        });
    }
}