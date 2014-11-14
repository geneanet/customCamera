package org.geneanet.customcamera;

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
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /******************************/
    /** WHEN THE VIEW IS CREATED **/
    /******************************/
    public void surfaceCreated(SurfaceHolder holder) {
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
    
    /******************************/
    /** WHEN THE VIEW IS CHANGED **/
    /******************************/
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null){
          return;
        }

        try {
        	mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        } catch (Exception e){}

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d("error", "Error starting camera preview: " + e.getMessage());
        }
    }
    
    /*******************************************/
    /** TO DESTROY THE SURFACE OF THE PREVIEW **/
    /*******************************************/
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera!=null){
        	mCamera.setPreviewCallback(null);
        	mCamera.stopPreview();
        	mCamera = null;
        }
    }
}