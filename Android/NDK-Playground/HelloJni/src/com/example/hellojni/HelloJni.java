/*
 * Copyright (C) 2009 The Android Open Source Project
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




import android.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;


public class HelloJni extends Activity implements OnTouchListener
{
	
	static final int JAVA = 1;
	static final int NATIVE = 2;
	
	public BasicGLSurfaceView mView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(stringFromJNI());
        //mView = new BasicGLSurfaceView(getApplication());
        
        setContentView(R.layout.layout);
        mView = (BasicGLSurfaceView)findViewById(R.id.GLview);
        
        //setContentView(new ProcessedImage(this));
        
    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String stringFromJNI();
    public static native String bitmapFromServer(Bitmap bitmap);


    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("hello-jni");
    }
    
    public void onClickUseNativeFileTransfer(View v){
        final int W = 100;
        final int H = 100;
        Bitmap mBitmap;
        mBitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);   
        bitmapFromServer(mBitmap);
        mView.setTexture(mBitmap);
    }
    
    public void onClickUseJavaFileTransfer(View v){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);			
		alert.setTitle("Add new Entry");
		alert.setMessage("Insert title:");
		final EditText input = new EditText(this);
		input.setText("192.168.178.31");
		alert.setView(input);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			final String text = input.getText().toString();					
			connect(text, JAVA);
		  }
		});			
		alert.show(); 
    }
    public void connect(final String IP, int method){
    	switch (method) {
    	case JAVA:
    		mView.connect(IP);    		
    		break;

    	default:
    		break;
    	}
    }
    
    
public boolean onTouch (View v, MotionEvent event){
    	
    	
        
        AlertDialog.Builder alert = new AlertDialog.Builder(this);			
		alert.setTitle("Add new Entry");
		alert.setMessage("Insert title:");
		final EditText input = new EditText(this);
		input.setText("192.168.178.31");
		alert.setView(input);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String text = input.getText().toString();
			mView.connect(text);
		  }
		});			
		alert.show(); 
    	return false;
    	
    }

	public void onResume(){
		super.onResume();
	}
}



class ProcessedImage extends View {
    private Bitmap mBitmap;

    private static native void processBitmap(Bitmap bitmap);    

    public ProcessedImage(Context context) {
        super(context);

        final int W = 200;
        final int H = 200;

        mBitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);   
        
    }

    @Override protected void onDraw(Canvas canvas) {
        //canvas.drawColor(0xFFCCCCCC);
    	processBitmap(mBitmap);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        // force a redraw, with a different time-based pattern.
        invalidate();
    }
}
