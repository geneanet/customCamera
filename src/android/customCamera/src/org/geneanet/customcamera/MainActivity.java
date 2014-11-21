package org.geneanet.customcamera;

import org.geneanet.customcamera.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Just to test application.
 */
public class MainActivity extends Activity {
    
    Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void startCamera(View view) {
        if(this.checkCameraHardware(this)){
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Log.d("customCamera", "No camera hardware detected.");
        }
    }
    
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) || Camera.getNumberOfCameras() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
