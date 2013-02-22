package com.example.hellojni;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
	 
public class ImageClientNIO {		
	InetAddress host;
	SocketChannel socket;
    public void connect(String IP) throws IOException{
        host = InetAddress.getByName(IP);
        socket = SocketChannel.open();
        socket.connect(new InetSocketAddress("192.168.178.31", 7777));          
    }
	
    public Bitmap requestImage() {
    	long timestamp;
    	long timestamp2;
    	Bitmap bmp = null;
        try { 
    		ByteBuffer buf = ByteBuffer.allocate(786432);
            timestamp = System.currentTimeMillis();
            int bytesRead = socket.read(buf);    
            timestamp2 = System.currentTimeMillis();
            timestamp2 -= timestamp;
            System.out.println("...done. Receiving took " + timestamp2 + " milliseconds for " + bytesRead + " bytes");

            Bitmap.Config conf = Bitmap.Config.ARGB_8888; 
            bmp = Bitmap.createBitmap(100, 100, conf);
            timestamp = System.currentTimeMillis();
            
            
            byte [] Src = new byte[786432]; //Comes from somewhere...
            System.out.println("Src size: " + Src.length);
            // testbytes:
            for (int i = 0; i < Src.length; i++){
            	Src[i] = (byte) (i%255);
            }
            
            
            byte [] Bits = new byte[Src.length*4]; //That's where the RGBA array goes.
            int i;
            for(i=0;i<Src.length;i++)
            {
                Bits[i*4] =
                    Bits[i*4+1] =
                    Bits[i*4+2] = Src[i]; //Invert the source bits
                Bits[i*4+3] = -1;//0xff, that's the alpha.
            }

            //Now put these nice RGBA pixels into a Bitmap object

            bmp = Bitmap.createBitmap(1024, 768, Bitmap.Config.ARGB_8888);
            timestamp = System.currentTimeMillis();
            bmp.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));            

            timestamp2 = System.currentTimeMillis();
            timestamp2 -= timestamp;
            System.out.println("...done drawing to bitmap. Took " + timestamp2 + " milliseconds for " + bmp.getWidth()*bmp.getHeight() + " pixel");

            

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }	        
        return bmp;
    }
}
