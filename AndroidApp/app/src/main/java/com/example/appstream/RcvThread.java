package com.example.appstream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jcodec.api.android.AndroidSequenceEncoder;
import org.jcodec.common.Codec;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;


public class RcvThread extends Thread {

    private final String serverIp;    // Server IP
    private DatagramSocket rcvSocket;
    private boolean rec = false;
    private AndroidSequenceEncoder encoder;
    private SeekableByteChannel out = null;

    Bitmap frameBitMap;
    private boolean running = true;

    private final FrameHandler frameHandler = new FrameHandler();

    private ImageView imgViewCam;

    public RcvThread(ImageView imgViewCam, String serverIp, int serverPortSend) {
        this.serverIp = serverIp;
        this.imgViewCam = imgViewCam;
        try {
            rcvSocket = new DatagramSocket(null);
            rcvSocket.setReuseAddress(true);
            rcvSocket.bind(new InetSocketAddress(serverPortSend));
        } catch (SocketException e) {
            Log.println(Log.ERROR,"12",e.getMessage());
        }

    }

    @Override
    public void interrupt() {
        this.running = false;
    }

    @Override
    public void run() {
        int framesInVideo = 0;
        try
        {
            while (running) {
                byte[] inBuf= new byte[1024*1024];
                DatagramPacket inPacket = new DatagramPacket(inBuf,inBuf.length);
                rcvSocket.receive(inPacket);

                if(!inPacket.getAddress().equals(InetAddress.getByName(serverIp)))
                    throw new IOException("Mensaje desconocido: " + serverIp);

                Bitmap b = BitmapFactory.decodeByteArray(inPacket.getData(), 0, inPacket.getLength());
                b = Bitmap.createScaledBitmap(b, imgViewCam.getWidth(), imgViewCam.getHeight(), false);
                frameHandler.sendEmptyMessage(1);
                frameBitMap = b;
                if(rec){
                    Bitmap bitmapVideo = Bitmap.createScaledBitmap(b, 500, 500, false);
                    encoder.encodeImage(bitmapVideo);
                    framesInVideo++;
                    if(framesInVideo == 250){
                        Log.println(Log.WARN, "12","terminado el video");
                        rec = false;
                        encoder.finish();
                        NIOUtils.closeQuietly(out);
                        framesInVideo =0;
                    }
                }
            }
        } catch (Exception e) {
            Log.println(Log.WARN,"10","Error en recepción de imagen: ");
            Log.println(Log.ERROR,"12",e.getMessage());
        }finally {
            rcvSocket.close();
        }
    }

    public void recVideo(File file){
        File file1 = new File(file,File.separator + "newDir");
        if(!file1.exists()){
            file1.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File file2 = new File(file1,timeStamp + "_output2.mp4");
        String path = file2.getPath();
        Log.println(Log.WARN, "13",path);
        try {
            out = NIOUtils.writableFileChannel(path);
            encoder = new AndroidSequenceEncoder(out, Rational.R(25, 1));
            Log.println(Log.WARN, "14","no hay error");
        }catch (IOException e) {
            Log.println(Log.ERROR,"12",e.getMessage());
        }
        rec = true;
    }

    class FrameHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imgViewCam.setImageBitmap(frameBitMap);
        }
    }
}
