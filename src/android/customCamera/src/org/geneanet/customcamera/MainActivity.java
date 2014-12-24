package org.geneanet.customcamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.geneanet.customcamera.R;

/**
 * Just to test application.
 */
public class MainActivity extends Activity {

  Camera myCamera;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  /**
   * Start the camera.
   * 
   * @param view The current view.
   */
  public void startCamera(View view) {
    if (this.checkCameraHardware(this)) {
      Intent intent = new Intent(this, CameraActivity.class);
      startActivity(intent);
      finish();
    } else {
      Log.d("customCamera", "No camera hardware detected.");
    }
  }

  /**
   * Check if the device has cameras.
   * 
   * @param context Context of the application.
   * 
   * @return boolean True if the device has cameras. Else, return false.
   */
  private boolean checkCameraHardware(Context context) {
    if (context.getPackageManager().hasSystemFeature(
        PackageManager.FEATURE_CAMERA)
        || Camera.getNumberOfCameras() > 0) {
      return true;
    } else {
      return false;
    }
  }
}
