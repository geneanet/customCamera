package org.geneanet.customcamera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Interface between the view and the camera.
 */
public class CameraPreview extends SurfaceView implements
    SurfaceHolder.Callback {

  private SurfaceHolder myHolder;
  private Camera myCamera;

  /**
   * Constructor.
   * 
   * @override
   */
  public CameraPreview(Context context, Camera camera) {
    super(context);

    // assign camera
    myCamera = camera;
    myHolder = getHolder();
    myHolder.addCallback(this);
    myHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  /**
   * When the view is created.
   * 
   * @override.
   */
  public void surfaceCreated(SurfaceHolder holder) {
    try {
      // Reset preview start camera.
      myCamera.setPreviewCallback(null);
      myCamera.setPreviewDisplay(holder);
      // Start link between the view and the camera.
      myCamera.startPreview();
    } catch (IOException e) {
      Log.e("customCamera", "Error setting camera preview to create surface: "
          + e.getMessage());
    }
  }

  /**
   * When the view is changed.
   * 
   * @override.
   */
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    // check if the surface exist.
    if (myHolder.getSurface() == null) {
      return;
    }

    try {
      // stop current instance.
      myCamera.setPreviewCallback(null);
      myCamera.stopPreview();
    } catch (Exception e) {
      Log.e("customCamera",
          "Error setting camera preview at null: " + e.getMessage());
    }

    try {
      // Start new link between the view and the camera.
      myCamera.setPreviewDisplay(myHolder);
      myCamera.startPreview();
    } catch (Exception e) {
      Log.e("error",
          "Error starting camera preview with myHolder: " + e.getMessage());
    }
  }

  /**
   * To destroy the surface of the preview.
   * 
   * @override
   */
  public void surfaceDestroyed(SurfaceHolder holder) {
  }
}