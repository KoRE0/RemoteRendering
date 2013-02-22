package kore.remoterenderer.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.nio.ByteBuffer;

public class ImageServer {
    private ServerSocket server;
    private int port = 7777;
 
    public ImageServer() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        ImageServer example = new ImageServer();
        example.handleConnection();
    }
 
    public void handleConnection() {
        System.out.println("Waiting for client message...");
        while (true) {
            try {
                Socket socket = server.accept();
                new ConnectionHandler(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
 
class ConnectionHandler implements Runnable {
    private Socket socket;
 
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
 
        Thread t = new Thread(this);
        t.start();
    }
 
    public void run() {
        try
        {
        	boolean swap = false;
            System.out.print("Reading pictures...");
            long timestamp = System.currentTimeMillis();
            File imagefile = new File(System.getProperty( "user.dir" ) + "/Koala.png");	
            BufferedImage imageKoala = ImageIO.read(imagefile);
            imagefile = new File(System.getProperty( "user.dir" ) + "/Penguins.jpg");	
            
            BufferedImage imagePenguins = ImageIO.read(imagefile);
            System.out.println("done. - took " + (System.currentTimeMillis() - timestamp)/1000.0f + " seconds.");
            
            ObjectInputStream ois;
            ObjectOutputStream oos;
            DataOutputStream dos;
            try {
            	
            	while(true){
	            ois = new ObjectInputStream(socket.getInputStream());
	            String message = (String) ois.readObject();
	            System.out.println("Message Received: " + message);	            
	            
	            // Send a response information to the client application
	            oos = new ObjectOutputStream(socket.getOutputStream());	            
	            oos.writeObject("Sending picture...");   
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            if (swap){
	            	ImageIO.write(imagePenguins, "BMP", baos);	            	
	            	swap = false;
	            }else{
	            	ImageIO.write(imageKoala, "BMP", baos);
	            	swap = true;
	            }
	            byte[] data = baos.toByteArray();	            
	            //send length of bytearray
	            oos.writeObject(data.length);	            
	            dos = new DataOutputStream(socket.getOutputStream());	            
	            dos.write(data);
            	}            
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
        } catch (IOException e) {
			e.printStackTrace();
		}finally{    	
        }
    }    
}

