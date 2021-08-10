package com.example.appstream;

import androidx.annotation.NonNull;

public class Camera {

    private String ip;
    private int port;
    private String name;

    public Camera(String name, String ip, int port){
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " - " + port;
    }
}
