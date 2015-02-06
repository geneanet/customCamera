package org.geneanet.customcamera;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

/**
 * Manage camera resource.
 */
public class ManagerCamera {
  protected static Camera mCamera = null;

  // Constant to define different orientations for the devices.
  public static final int PORTRAIT = 0;
  public static final int LANDSCAPE = 1;
  public static final int PORTRAIT_INVERSED = 2;
  public static final int LANDSCAPE_INVERSED = 3;
  private static Integer currentCameraPosition = null;

  /**
   * A safe way to get an instance of the Camera object.
   * 
   * @return Camera | null
   */
  public static Camera getCameraInstance(int position) {
    // If camera is already instanced and available, return this resource.
    if (ManagerCamera.mCamera != null && position == currentCameraPosition) {
      return ManagerCamera.mCamera;
    } else if (ManagerCamera.mCamera != null) {
      clearCameraAccess();
    }

    // Start back camera.
    Camera cam = null;
    try {
      cam = Camera.open(position);
      currentCameraPosition = position;
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
  
  /**
   * Return a function to determine the front camera in use.
   * 
   * @return function 
   */
  public static int determinePositionFrontCamera() {
    return determineCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
  }
  
  /**
   * Return a function to determine the back camera in use.
   * 
   * @return function 
   */
  public static int determinePositionBackCamera() {
    return determineCamera(Camera.CameraInfo.CAMERA_FACING_BACK); 
  }
  
  /**
   * Determine the cameraId of the camera currently in use.
   * 
   * @param position Back or front camera.
   * 
   * @return the cameraId of the current camera if it exists.
   */
  protected static Integer determineCamera(int position) {
    CameraInfo info = new Camera.CameraInfo();
    if (Camera.getNumberOfCameras() == 0) {
      return null;
    }
    if (Camera.getNumberOfCameras() == 1) {
      return 0;
    }
    for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
      Camera.getCameraInfo(i, info);
      if (info.facing == position) {
        return i;
      }
    }
    
    return 0;
  }
  
  /**
   * Get the currentCamera.
   * 
   * @return the value of the variable.
   */
  private static int getCurrentFacingCamera() {
    return currentCameraPosition;
  }
  
  /**
   * Determine the opposite camera of which currently in use.
   * 
   * @return function.
   */
  public static int determineOppositeCamera() {
    if (getCurrentFacingCamera() == Camera.CameraInfo.CAMERA_FACING_BACK) {
      return determinePositionFrontCamera();
    } else {
      return determinePositionBackCamera();
    }
  }
  
  /**
   * Determine if the current camera is front.
   * 
   * @return True if the current camera is front. Else return false.
   */
  public static boolean currentCameraIsFacingBack() {
    if (getCurrentFacingCamera() == Camera.CameraInfo.CAMERA_FACING_BACK) {
      return true;
    }
    return false;
  }
  
  /**
   * Determine if the camera is back.
   * 
   * @return True if the current camera is back. Else return false.
   */
  public static boolean currentCameraIsFacingFront() {
    if (getCurrentFacingCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      return true;
    }
    return false;
  }
}
