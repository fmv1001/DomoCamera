package com.example.appstream;

import java.util.List;

public interface DataBaseCameraInterface {

    public boolean startDataBase();

    public boolean upDataBase();

    public void closeDataBase();

    public boolean saveCamera(int id, String name, String ip, int port);

    public boolean saveIpServer(String ip);

    public int deleteIpServer();

    public String getIpServer();

    public List<String> getCamera(String name);

    public List<String[]> getCameras();

    public void cleanDataBase();

    public int deleteCamera(String name);

    public int getIdCounter();
}
