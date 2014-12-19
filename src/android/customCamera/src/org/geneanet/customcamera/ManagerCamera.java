package org.geneanet.customcamera;

import android.hardware.Camera;
import android.util.Log;

/**
 * Manage camera resource.
 */
public class ManagerCamera {
  protected static Camera mCamera = null;

  // Constant to define differents orientations for the devices.
  public static final int PORTRAIT = 0;
  public static final int LANDSCAPE = 1;
  public static final int PORTRAIT_INVERSED = 2;
  public static final int LANDSCAPE_INVERSED = 3;

  /**
   * A safe way to get an instance of the Camera object.
   * 
   * @return Camera | null
   */
  public static Camera getCameraInstance() {
    // If camera is already instanced and available, return this resource.
    if (ManagerCamera.mCamera != null) {
      return ManagerCamera.mCamera;
    }

    // Start back camera.
    Camera cam = null;
    try {
      cam = Camera.open(0);
    } catch (RuntimeException e) {
      Log.e("customCamera", "Can't open the camera back.");
    }

    ManagerCamera.mCamera = cam;

    return cam; // returns null if camera is unavailable
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
