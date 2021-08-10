package com.example.appstream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseCameras extends SQLiteOpenHelper implements DataBaseCameraInterface{

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DataBase.db";
    private Context context;
    private SQLiteDatabase db;
    private DataBaseCameras dataBaseCameras;

    public DataBaseCameras(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public SQLiteDatabase getDb(){
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table serverinf(ipServer text primary key)");
        db.execSQL("create table cameras(id int primary key, name text unique, ip text, port int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists cameras");
        db.execSQL("drop table if exists serverinf");
        onCreate(db);
    }

    @Override
    public boolean startDataBase() {
        dataBaseCameras = new DataBaseCameras(this.context);
        return !dataBaseCameras.equals(null);
    }

    @Override
    public boolean upDataBase() {
        db = dataBaseCameras.getWritableDatabase();
        return !db.equals(null);
    }

    @Override
    public void closeDataBase() {
        db.close();
    }

    @Override
    public boolean saveCamera(int id, String name, String ip, int port) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name",name);
        values.put("ip",ip);
        values.put("port",port);
        return db.insert("cameras", null, values)!=-1;
    }

    @Override
    public boolean saveIpServer(String ip) {
        ContentValues values = new ContentValues();
        values.put("ipServer",ip);
        return db.insert("serverinf", null, values)!=-1;
    }

    @Override
    public int deleteIpServer() {
        int deletedRows = db.delete("serverinf", null, null);
        return deletedRows;
    }

    @Override
    public String getIpServer() {
        String [] columns = new String []{"ipServer"};
        Cursor cursor = this.db.query("serverinf",columns,null,null,null,null,null);
        String ip = "";
        while(cursor.moveToNext()) {
            ip = cursor.getString(
                    cursor.getColumnIndexOrThrow("ipServer"));
        }
        cursor.close();
        return ip;
    }

    @Override
    public List<String> getCamera(String name) {
        List<String> camera = new ArrayList<String>();
        String [] columns = new String []{"name","ip", "port"};
        String selection = "name = ?";
        String[] selectionArgs = { name };
        Cursor cursor = this.db.query("cameras",columns,selection,selectionArgs,null,null,null);
        while(cursor.moveToNext()) {
            String itemName = cursor.getString(
                    cursor.getColumnIndexOrThrow("name"));
            camera.add(itemName);
            String itemIp = cursor.getString(
                    cursor.getColumnIndexOrThrow("ip"));
            camera.add(itemIp);
            int itemPort = cursor.getInt(
                    cursor.getColumnIndexOrThrow("port"));
            camera.add(String.valueOf(itemPort));
        }
        cursor.close();
        return camera;
    }

    @Override
    public List<String[]> getCameras() {
        List<String[]> cameras = new ArrayList<String[]>();
        String [] columns = new String []{"name","ip", "port"};
        Cursor cursor = this.db.query("cameras",columns,null,null,null,null,null);
        while(cursor.moveToNext()) {
            String itemName = cursor.getString(
                    cursor.getColumnIndexOrThrow("name"));
            String itemIp = cursor.getString(
                    cursor.getColumnIndexOrThrow("ip"));
            int itemPort = cursor.getInt(
                    cursor.getColumnIndexOrThrow("port"));
            String[] cameraX = {itemName, itemIp, String.valueOf(itemPort)};
            cameras.add(cameraX);
        }
        cursor.close();
        return cameras;
    }

    @Override
    public void cleanDataBase() {
        db.execSQL("drop table if exists cameras");
        db.execSQL("drop table if exists serverinf");
        onCreate(db);
    }

    @Override
    public int deleteCamera(String name) {
        // Define 'where' part of query.
        String selection = "name LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { name };
        // Issue SQL statement.
        int deletedRows = db.delete("cameras", selection, selectionArgs);
        return deletedRows;
    }

    @Override
    public int getIdCounter(){
        String [] columns = new String []{"id"};
        Cursor cursor = this.db.query("cameras",columns,null,null,null,null,"id DESC");
        int itemID = 0;
        while(cursor.moveToNext()) {
            itemID = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id"));
            break;
        }
        cursor.close();
        return itemID+1;
    }
}
