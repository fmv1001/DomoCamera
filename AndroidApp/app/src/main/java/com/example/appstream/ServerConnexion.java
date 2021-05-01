package com.example.appstream;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnexion extends Thread {

    private TextView txv_1;
    private Socket socket = null;
    private static Handler handler;

    public ServerConnexion(TextView txv_1, Handler handler){
        this.txv_1 = txv_1;
        this.handler = handler;
    }

    @Override
    public void run() {
        String hostName = "192.168.18.35";
        int portNumber = 9999;

        try  {
            System.out.println("Conectando...");
            socket = new Socket(hostName, portNumber);
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), "UTF8");
            InputStreamReader in = new InputStreamReader(socket.getInputStream(), "UTF8");

            System.out.println("Conectado");
            char[] cbuf = new char[256];

            String message = "Hola servidor";
            System.out.println("Enviando...");
            out.write(message.toCharArray());
            out.flush();
            System.out.println(message);

            //Recibimos
            //Toast.makeText(this, "Recibiendo...", Toast.LENGTH_SHORT).show();
            System.out.println("Recibiendo...");
            in.read(cbuf);

            String message1 = "";
            for (char c : cbuf) {
                message1 += c;
                if (c == 00) {
                    break;
                }
            }
            System.out.println("Mensaje:");
            System.out.println(message1);
            String finalMessage = message1;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    txv_1.setText(finalMessage);
                }
            });

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (SecurityException e) {
            System.err.println("SecurityException " + hostName);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}
