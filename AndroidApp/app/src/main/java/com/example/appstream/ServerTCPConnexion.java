package com.example.appstream;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.appstream.ui.CheckCamerasViewModel;
import com.example.appstream.ui.information.InformationViewModel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerTCPConnexion extends Thread {

    private Socket sendSocket;
    private ConnexHandler connexHandler = new ConnexHandler();
    private final int serverPortRecv;
    private final String serverIp;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private boolean running = true;
    private boolean newCamera = false;
    private boolean deleteCamera = false;
    private boolean stopServer = false;
    private boolean stopCamera = false;
    private boolean startCamera = false;
    private boolean disconnectToServer = false;
    private static ServerTCPConnexion instance;
    private String nameC = "";
    private String ipC = "";
    private int portC = 0;
    private static ViewModelStoreOwner activity;

    public ServerTCPConnexion(String serverIp, int serverPort, ViewModelStoreOwner activityOwner) {
        serverPortRecv = serverPort;
        this.serverIp = serverIp;
        instance = this;
        activity = activityOwner;
    }

    public static ServerTCPConnexion getInstance() {
        return instance;
    }

    @Override
    public void run() {
        startConnexion();
        if (running){
            byte[] bytes = new byte[512];
            try {
                int count = 0;
                count = dataInputStream.read(bytes);
                if(count<=0)
                    throw new IOException("no se ha leído nada");
            } catch (Exception e) {
                Log.println(Log.ERROR,"12",e.getMessage());
            }

            String liveCameras = new String(bytes);
            CheckCamerasViewModel checkCamerasViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(CheckCamerasViewModel.class);
            checkCamerasViewModel.select(liveCameras);
        }
        byte[] idSenderBuffer;
        byte[] idSenderBufferAction;
        while (running) {
            try {
                if (newCamera) {
                    idSenderBuffer = "1".getBytes();
                    dataOutputStream.write(idSenderBuffer);
                    idSenderBufferAction = (nameC + "-" + ipC + "-" + portC).getBytes();
                    dataOutputStream.write(idSenderBufferAction);
                    newCamera = false;
                } else if (deleteCamera) {
                    idSenderBuffer = "2".getBytes();
                    dataOutputStream.write(idSenderBuffer);
                    idSenderBufferAction = nameC.getBytes();
                    dataOutputStream.write(idSenderBufferAction);
                    deleteCamera = false;
                } else if (stopCamera) {
                    idSenderBuffer = "3".getBytes();
                    dataOutputStream.write(idSenderBuffer);
                    idSenderBufferAction = nameC.getBytes();
                    dataOutputStream.write(idSenderBufferAction);
                    stopCamera = false;
                } else if (startCamera) {
                    idSenderBuffer = "4".getBytes();
                    dataOutputStream.write(idSenderBuffer);
                    idSenderBufferAction = nameC.getBytes();
                    dataOutputStream.write(idSenderBufferAction);
                    startCamera = false;
                } else if (disconnectToServer) {
                    idSenderBuffer = "5".getBytes();
                    dataOutputStream.write(idSenderBuffer);
                    disconnectToServer = false;
                    stopRun();
                }else if (stopServer) {
                    idSenderBuffer = "0".getBytes();
                    dataOutputStream.write(idSenderBuffer);
                    stopServer = false;
                    stopRun();
                }
            } catch (IOException e) {
                newCamera = false;
                deleteCamera = false;
                newCamera = false;
                stopCamera = false;
                startCamera = false;
                Log.println(Log.ERROR,"12",e.getMessage());
                stopRun();
            }
        }
    }

    private void startConnexion(){
        try {
            sendSocket = new Socket(serverIp, serverPortRecv);
            dataOutputStream = new DataOutputStream(sendSocket.getOutputStream());
            dataInputStream = new DataInputStream(sendSocket.getInputStream());
            connexHandler.sendEmptyMessage(1);
        } catch (IOException e) {
            Log.println(Log.WARN,"11","Error en el establecimiento de conexión con servidor: ");
            Log.println(Log.ERROR,"12",e.getMessage());
            running = false;
        }
    }

    class ConnexHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public void stopRun() {
        try {
            dataOutputStream.close();
            dataInputStream.close();
            sendSocket.close();
            running = false;
        } catch (IOException e) {
            Log.println(Log.ERROR,"12",e.getMessage());
        }
    }

    public void disconnectToServer() { disconnectToServer = true; }

    public void addCamera(String name, String ip, int port) {
        nameC = name;
        ipC = ip;
        portC = port;
        newCamera = true;
    }

    public void stopCamera(String name) {
        nameC = name;
        stopCamera = true;
    }

    public void startCamera(String name) {
        nameC = name;
        startCamera = true;
    }

    public void delCamera(String name) {
        nameC = name;
        deleteCamera = true;
    }

    public void stopServer() {
        stopServer = true;
    }
}
