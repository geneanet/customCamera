package org.geneanet.customcamera;

import android.hardware.Camera;
import android.util.Log;

/**
 * Manage camera resource.
 */
public class ManagerCamera {
    protected static Camera mCamera = null;
    
    // Constant to define differents orientations for the devices.
    public final static int PORTRAIT = 0;
    public final static int LANDSCAPE = 1;
    public final static int PORTRAIT_INVERSED = 2;
    public final static int LANDSCAPE_INVERSED = 3;

    /**
     * A safe way to get an instance of the Camera object.
     * @return Camera | null
     */
    public static Camera getCameraInstance(){
        // If camera is already instanced and available, return this resource.
        if (ManagerCamera.mCamera != null) {
            return mCamera;
        }

        // Start back camera.
        Camera c = null;
        try {
            c = Camera.open(0);
        }
        catch (RuntimeException e) {
            Log.d("customCamera", "Can't open the camera.");
        }
        
        ManagerCamera.mCamera = c;
        
        return c; // returns null if camera is unavailable
    }

    /**
     * To release the camera.
     */
    public static void clearCameraAccess() {
        if (ManagerCamera.mCamera != null) {
            ManagerCamera.mCamera.stopPreview();
            ManagerCamera.mCamera.release();
            ManagerCamera.mCamera = null;
        }
    }
}
