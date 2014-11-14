package org.geneanet.customcamera;

import org.geneanet.customcamera.CameraPreview;
import org.geneanet.customcamera.CustomCamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class CameraView extends Activity {
	
	private Camera mCamera;
    private CameraPreview mPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_camera_view);
		
		// Récupère les infos sur le device.
		Display display = getWindowManager().getDefaultDisplay();
		
		// récupère l'objet gérant la camera puis l'oriente en fonction de l'écran.
		mCamera = CustomCamera.getCameraInstance();
		switch (display.getRotation()) {
			case CustomCamera.LANDSCAPE:
				mCamera.setDisplayOrientation(0);
				break;
			case CustomCamera.PORTRAIT:
				mCamera.setDisplayOrientation(90);
				break;
			case CustomCamera.LANDSCAPE_INVERSED:
				mCamera.setDisplayOrientation(180);
				break;
			case CustomCamera.PORTRAIT_INVERSED:
				mCamera.setDisplayOrientation(270);
				break;
		}
		
		// On assigne le rendu à la vue.
		mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
	}
}
