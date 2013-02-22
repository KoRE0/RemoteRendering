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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ImageServerNIO {
    private ServerSocketChannel server;
    private int port = 7777;
 
    public ImageServerNIO() {
        try {
        	server = ServerSocketChannel.open();
        	InetSocketAddress isa
        	= new InetSocketAddress("192.168.178.31", port);
        	server.socket().bind(isa);
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        ImageServerNIO example = new ImageServerNIO();
        example.handleConnection();
    }
 
    public void handleConnection() {
        System.out.println("Waiting for client message...");
       // while (true) {
            try {
                SocketChannel socket = server.accept();
                new ConnectionHandlerNIO(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
     //   }
    }
}
 
class ConnectionHandlerNIO implements Runnable {
    private SocketChannel socket;
 
    public ConnectionHandlerNIO(SocketChannel socket) {
        this.socket = socket;
 
        Thread t = new Thread(this);
        t.start();
    }

	public void run() {
        try
        {
        	
            long timestamp = System.currentTimeMillis();
            System.out.println("done. - took " + (System.currentTimeMillis() - timestamp)/1000.0f + " seconds.");
            
            long timestamp2;
           // while(true){
            	try {
            		
            		if(socket.finishConnect())
                		System.out.println("finishConnect() = true");
            		else System.out.println("finishConnect() = false");;                	

                	ByteBuffer buf = ByteBuffer.allocateDirect(786432);
                	byte[] bytes = new byte[buf.capacity()];
                	buf.clear();
                	for (int i = 0; i < bytes.length; i++){
                		bytes[i] = (byte) (i%255);
                	}
                	buf.put(bytes);

                	buf.flip();
                	System.out.println("Goint to write " + buf.limit() + " bytes");
                	timestamp = System.currentTimeMillis();
                	while(buf.hasRemaining()) {
                	    socket.write(buf);
                	}
                	timestamp2 = System.currentTimeMillis();
                	System.out.println("Writing done...");
                	timestamp2 -= timestamp;
                	System.out.println("Sending took " + timestamp2 + " milliseconds." );

            		} catch (IOException e) {
            		e.printStackTrace();
            		}
			//}
        }finally{    	
        }
    }    
}

