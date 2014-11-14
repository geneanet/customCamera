package org.geneanet.customcamera;

import org.geneanet.testcustomcamera.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
	
	Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Enfin, on lance l'intent pour que l'application de photo se lance
	    // startActivityForResult(intent, 42);
    }
    
    public void startCamera(View view) {
    	if(this.checkCameraHardware(this)){
    		Intent intent = new Intent(this, CameraView.class);
            startActivity(intent);
            finish();
    	}
    	else {
    		System.out.println("NO"); 
    	}
    }
    
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) || Camera.getNumberOfCameras() > 0){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
