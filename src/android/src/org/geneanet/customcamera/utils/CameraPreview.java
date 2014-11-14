package org.geneanet.customcamera.utils;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
    	try {
    		mCamera.setPreviewCallback(null);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
        	mCamera.release();
        	mCamera = null;
            Log.d("error", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	System.out.println("surfaceChanged");
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }
//  
        // stop preview before making changes
        try {
        	System.out.println("SHIT");
        	mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }
//
//        // set preview size and make any resize, rotate or
//        // reformatting changes here
//
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("error", "Error starting camera preview: " + e.getMessage());
        }
    }
    
    /********************************************/
    /** POUR DETRUIRE LA SURFACE DE LA PREVIEW **/
    /********************************************/
    public void surfaceDestroyed(SurfaceHolder holder) {
    	System.out.println("SURFACE DESTROYED");
        if(mCamera!=null){
        	System.out.println("surfaceDestroyed -> LA CAMERA N'EST PAS NULLE");
        	mCamera.setPreviewCallback(null);
        	mCamera.stopPreview();
//        	mCamera.release();
        	mCamera = null;
        	System.out.println("surfaceDestroy -> DESTRUCTION TERMINEE ");
        }
    }
}
