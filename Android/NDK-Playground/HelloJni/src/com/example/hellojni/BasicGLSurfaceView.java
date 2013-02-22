/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.hellojni;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLSurfaceView;

import android.opengl.GLES20;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.widget.EditText;
import android.widget.Toast;




class BasicGLSurfaceView extends GLSurfaceView {
	
	public GLES20TriangleRenderer renderer;
	public String IP = "192.168.178.31";
    public BasicGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        renderer = new GLES20TriangleRenderer(context);
        setRenderer(renderer);
        
    }
    
    
    public BasicGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        renderer = new GLES20TriangleRenderer(context);
        setRenderer(renderer);        
    }
    
    public void connect(String IP){
    	System.out.println(IP);
    	this.IP = IP;
    	t.start();
    }
    
    public void setTexture(Bitmap bitmap){
    	final Bitmap bmp = bitmap;
		queueEvent(new Runnable() {				
			public void run() {
				renderer.newTex(bmp);					
			}
		});
    }
    
    

    

    

    Thread t = new Thread(){
		public void run(){
			// ImageClient or ImageClientNIO
			ImageClientNIO c = new ImageClientNIO();
			//ImageClient c = new ImageClient();
			try {
				c.connect(IP);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
//			int[] colors = new int[100];
//			
//			for (int i = 0; i < 100; i++){
//				colors[i] = Color.GREEN;
//			}			
//			final Bitmap bmp = Bitmap.createBitmap(colors, 10, 10, Bitmap.Config.ARGB_8888);
			//while(true){
//				try {
//					Thread.sleep(4000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				final Bitmap bmp = c.requestImage();
				queueEvent(new Runnable() {				
					public void run() {
						renderer.newTex(bmp);					
					}
				});
			//}
			
		}
	};

}

