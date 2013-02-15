package com.example.hellojni;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
	 
public class ImageClient {		
	InetAddress host;
	Socket socket;
    public void connect(String IP) throws IOException{
        host = InetAddress.getByName(IP);
        socket = new Socket(host.getHostName(), 7777);            
    }
	
    public Bitmap requestImage() {
    	long timestamp;
    	long timestamp2;
    	Bitmap bmp = null;
        try { 
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Requesting picture...");
            oos.writeObject("Requesting picture...");	 
            
            // Read and display the response message sent by server application	            
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Server answers: " + message);
            
            // receive picture
            System.out.println("reading picture...");
            InputStream is = socket.getInputStream();
            
            int length = (Integer) ois.readObject();
            System.out.println("LENGHT: " + length);
            byte[] img = new byte[length];    	          
            DataInputStream dis = new DataInputStream(is);
            timestamp = System.currentTimeMillis();
            if (length > 0) {
                dis.readFully(img);
            } 
            timestamp2 = System.currentTimeMillis();
            timestamp2 -= timestamp;
            System.out.println("...done. Receiving took " + timestamp2 + " milliseconds for " + img.length + " bytes." );
            timestamp = System.currentTimeMillis();
            bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            
            System.out.println("Decoding took " + (System.currentTimeMillis() - timestamp) + " milliseconds.");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }	        
        return bmp;
    }
}
