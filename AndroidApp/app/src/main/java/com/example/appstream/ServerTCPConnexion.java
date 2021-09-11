package com.example.appstream;

import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.appstream.ui.CheckCamerasViewModel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.net.Socket;

public class ServerTCPConnexion extends Thread {

    private Socket sendSocket;
    private ConnexHandler connexHandler = new ConnexHandler();
    private static int serverPortRecv;
    private static String serverIp;
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

    public static ServerTCPConnexion getInstance(ViewModelStoreOwner activityOwner) {
        if (instance == null)
            return null;
        if (activity != null)
            activity = activityOwner;
        return instance;
    }

    @Override
    public void run() {
        byte[] idSenderBuffer;
        try {
            sendSocket = new Socket(serverIp, serverPortRecv);
            dataOutputStream = new DataOutputStream(sendSocket.getOutputStream());
            dataInputStream = new DataInputStream(sendSocket.getInputStream());
            connexHandler.sendEmptyMessage(1);
        } catch (Exception e) {
            System.err.println("Error en el establecimiento de conexion con servidor: ");
            e.printStackTrace();
        }

        byte[] bytes = new byte[512];
        try {
            dataInputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String liveCameras = new String(bytes);
        CheckCamerasViewModel checkCamerasViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(CheckCamerasViewModel.class);
        checkCamerasViewModel.select(liveCameras);

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
                e.printStackTrace();
                stopRun();
            }
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
            e.printStackTrace();
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
