package com.example.appstream;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.appstream.ui.CheckCamerasViewModel;
import com.example.appstream.ui.settings.SettingViewModel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
    private boolean stringAvailable = false;
    private boolean newCamera = false, deleteCamera = false, stopServer = false,
            stopCamera = false, startCamera = false;
    private static ServerTCPConnexion instance;
    private Context context;
    private String nameC = "", ipC = "", liveCameras;
    private int portC = 0;
    private static ViewModelStoreOwner activity;
    private CheckCamerasViewModel checkCamerasViewModel;

    public ServerTCPConnexion(String SERVER_IP, int SERVER_PORT, Context context, ViewModelStoreOwner activityOwner)
            throws UnknownHostException { //, Context context
        serverAddress = InetAddress.getByName(SERVER_IP);
        SERVER_PORT_RECV = SERVER_PORT;
        this.SERVER_IP = SERVER_IP;
        this.context = context;
        instance = this;
        activity = activityOwner;
    }

    public static ServerTCPConnexion getInstance(ViewModelStoreOwner activityOwner) throws UnknownHostException {
        if (instance == null)
            return null;
        if (activity != null)
            activity = activityOwner;
        return instance;
    }

    public static boolean instanceIsNull() {
        return instance == null;
    }

    @Override
    public void run() {
        try {
            idSenderBuffer = "Hola servidor".getBytes();
            sendSocket = new Socket(SERVER_IP, SERVER_PORT_RECV);
            socketSalida = new DataOutputStream(sendSocket.getOutputStream());
            socketEntrada = new DataInputStream(sendSocket.getInputStream());
            socketSalida.write(idSenderBuffer);
            connexHandler.sendEmptyMessage(1);
        } catch (Exception e) {
            System.err.println("Error en el establecimiento de conexion con servidor: ");
            e.printStackTrace();
        }

        System.out.println("Entramos");
        byte[] bytes = new byte[512];
        try {
            socketEntrada.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("salimos");

        liveCameras = new String(bytes);
        checkCamerasViewModel =
                new ViewModelProvider((ViewModelStoreOwner) activity).get(CheckCamerasViewModel.class);
        checkCamerasViewModel.select(liveCameras);
        stringAvailable = true;

        byte[] idSenderBufferAction;
        while(running){
            try {
                if (newCamera){
                    idSenderBuffer = "1".getBytes();
                    socketSalida.write(idSenderBuffer);
                    idSenderBufferAction = (nameC + "-" + ipC + "-" + String.valueOf(portC)).getBytes();
                    socketSalida.write(idSenderBufferAction);
                    newCamera = false;
                }else if(deleteCamera){
                    idSenderBuffer = "2".getBytes();
                    socketSalida.write(idSenderBuffer);
                    idSenderBufferAction = nameC.getBytes();
                    socketSalida.write(idSenderBufferAction);
                    deleteCamera = false;
                }else if(stopCamera){
                    idSenderBuffer = "3".getBytes();
                    socketSalida.write(idSenderBuffer);
                    idSenderBufferAction = nameC.getBytes();
                    socketSalida.write(idSenderBufferAction);
                    stopCamera = false;
                }else if(startCamera){
                    idSenderBuffer = "4".getBytes();
                    socketSalida.write(idSenderBuffer);
                    idSenderBufferAction = nameC.getBytes();
                    socketSalida.write(idSenderBufferAction);
                    startCamera = false;
                }else if(stopServer) {
                    idSenderBuffer = "0".getBytes();
                    socketSalida.write(idSenderBuffer);
                    stopServer = false;
                    stop_run();
                }
            } catch (IOException e) {
                newCamera = false;
                deleteCamera = false;
                newCamera = false;
                stopCamera = false;
                startCamera = false;
                e.printStackTrace();
                stop_run();
            }
        }
        //stop_run();
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
            running = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add_camera(String name, String ip, int port) {
        nameC = name;
        ipC = ip;
        portC = port;
        newCamera = true;
    }

    public void stop_camera(String name){
        nameC = name;
        stopCamera = true;
    }

    public void start_camera(String name){
        nameC = name;
        startCamera = true;
    }

    public void del_camera(String name){
        nameC = name;
        deleteCamera = true;
    }

    public void stop_server(){
        stopServer = true;
    }

    public String getInitialCameras(){
        return liveCameras;
    }

    public boolean isStringAvailable() {
        return stringAvailable;
    }
}
