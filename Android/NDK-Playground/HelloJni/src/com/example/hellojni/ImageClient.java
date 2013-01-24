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
        	//
            // Create a connection to the server socket on the server application
            //
            host = InetAddress.getByName(IP);
            socket = new Socket(host.getHostName(), 7777);
            
        }
		
	    public Bitmap requestImage() {
	    	
	    	Bitmap bmp = null;
	        try {
	        	
	        	System.out.println("requestImage");
	            
	 
	            //
	            // Send a message to the client application
	            //
	            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	            System.out.println("Requesting picture...");
	            oos.writeObject("Requesting picture...");
	            
	            
	 
	            //
	            // Read and display the response message sent by server application
	            //
	            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	            String message = (String) ois.readObject();
	            System.out.println("Server answers: " + message);
	            
	            // receive picture
	            System.out.println("reading picture...");
	            InputStream is = socket.getInputStream();
	            long timestamp = System.currentTimeMillis();	            
	            //byte[] img = (byte[]) ois.readObject();
	            //receive length
	            int length = (Integer) ois.readObject();
	            byte[] img = new byte[length];
	            
	            
	          
	            DataInputStream dis = new DataInputStream(is);

	            
	            if (length > 0) {
	                dis.readFully(img);
	            }
	            
	            
	            //is.read(img);
	            System.out.println("...done. Receiving took " + (System.currentTimeMillis() - timestamp)/1000.0f + " seconds for " + img.length + " bytes." );
	            //is.close();
	            bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
	            System.out.println("Receiving and decoding took " + (System.currentTimeMillis() - timestamp)/1000.0f + " seconds.");

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
