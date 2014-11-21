package org.geneanet.customcamera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Interface between the view and the camera.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;

    /**
     * Constructor
     * @override
     */
    public CameraPreview(Context context, Camera camera) {
        super(context);
        // assign camera
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * When the view is created.
     * @param SurfaceHolder holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // Reset preview start camera.
            mCamera.setPreviewCallback(null);
            mCamera.setPreviewDisplay(holder);
            // Start link between the view and the camera.
            mCamera.startPreview();
        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
            Log.e("customCamera", "Error setting camera preview: " + e.getMessage());
        }
    }

    /**
     * When the view is changed.
     * @override.
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // check if the surface exist.
        if (mHolder.getSurface() == null){
            return;
        }

        try {
            // stop current instance.
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        } catch (Exception e) {}

        try {
            // Start new link between the view and the camera.
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d("error", "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * To destroy the surface of the preview.
     * @override
     */
    public void surfaceDestroyed(SurfaceHolder holder) {}
}