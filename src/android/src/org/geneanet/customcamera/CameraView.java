package org.geneanet.customcamera;

import java.util.List;
import java.lang.Math;

import org.geneanet.customcamera.utils.CameraPreview;
import org.geneanet.customcamera.utils.CustomCamera;
import org.geneanet.testcustomcamera.R;

import android.app.Activity;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CameraView extends Activity {
	
    private float mDist;
    private int modeMiniature = 0;
    private CameraPreview mPreview;
    static boolean clickOn = false;
    private static Camera mCamera = null;
    public static final int MEDIA_TYPE_IMAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		System.out.println("ON RENTRE DANS L'APPLICATION");
		
		
		super.onCreate(savedInstanceState);
		
		/* Remove title bar */
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    /* Remove notification bar */
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_camera_view);
		
		/* Get informations about the device */
		Display display = getWindowManager().getDefaultDisplay();
		
		/* Get object which use the camera and orient it in function of the screen */
		mCamera = CustomCamera.getCameraInstance();
		
		if (getResources().getConfiguration().orientation == 0){
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
		}
			
		/* Assign the render to the view */
		mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
        /* The opacity bar */
        SeekBar switchOpacity = (SeekBar) findViewById(R.id.switchOpacity);
        
        switchOpacity.setOnSeekBarChangeListener(
        		new OnSeekBarChangeListener() {
        			int progress = 0;
        		@Override
        		public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        			progress = progresValue;
        			ImageView imageView = (ImageView) findViewById(R.id.normal);
        			switch(progress){
        			case 0 : 
        				imageView.setAlpha((float)0.2);
        				break;
        			case 1 :
        				imageView.setAlpha((float)0.3);
        				break;
        			case 2 :
        				imageView.setAlpha((float)0.4);
        				break;
        			case 3 :
        				imageView.setAlpha((float)0.5);
        				break;
        			case 4 :
        				imageView.setAlpha((float)0.6);
        				break;
        			case 5 :
        				imageView.setAlpha((float)0.7);
        				break;
        			case 6 :
        				imageView.setAlpha((float)0.8);
        				break;
        			case 7 :
        				imageView.setAlpha((float)0.9);
        				break;
        			}
        		}

        		@Override
        		public void onStartTrackingTouch(SeekBar seekBar) {}

        		@Override
        		public void onStopTrackingTouch(SeekBar seekBar) {}
        });
 	}
	
	/*******************************************************/
	/** BLOCK THE ROTATION IN FUNCTION OF THE POSTAL CARD **/
	/*******************************************************/
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus){
//		
//		ImageView imageView = (ImageView) findViewById(R.id.normal);
//	    int widthImage=imageView.getWidth();
//	    int heightImage=imageView.getHeight();
//	   
//	    if(heightImage < widthImage || imageView.getRotation() != 0){
//	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//	    	imageView.setRotation(0);
//	    }
//	    else{
//	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	    }
//	}
	
	/******************/
	/** FOR THE ZOOM **/
	/******************/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    Camera.Parameters params = mCamera.getParameters();
	    int action = event.getAction();
 
	    if (event.getPointerCount() > 1) {
	        // If we touch with more than one finger
	        if (action == MotionEvent.ACTION_POINTER_UP) {
	           mDist = getFingerSpacing(event);
	        } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
	            mCamera.cancelAutoFocus();
				handleZoom(event, params, mDist);
	        }
	    } else {
	        // If we touch with one finger -> auto-focus
	        if (action == MotionEvent.ACTION_UP) {
	            handleFocus(event, params);
	        }
	    }
	    return true;
	}

	/******************/
	/** GERE LE ZOOM **/
	/******************/
	private void handleZoom(MotionEvent event, Camera.Parameters params, float mDist) {
	    int maxZoom = params.getMaxZoom();
	    int zoom = params.getZoom();
	    float newDist = getFingerSpacing(event);
	    
	    if (newDist > mDist) {
	        //zoom in
	        if (zoom < maxZoom/2)
	            zoom+=2;
	    } else if (newDist < mDist) {
	    	//zoom out
		    if (zoom > 0)
		        zoom-=2;
	    	}
		mDist = newDist;
		params.setZoom(zoom);
		mCamera.setParameters(params);
	}

	/*******************/
	/** GERE LE FOCUS **/
	/*******************/
	public void handleFocus(MotionEvent event, Camera.Parameters params) {
	    List<String> supportedFocusModes = params.getSupportedFocusModes();
	    if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
	        mCamera.autoFocus(new Camera.AutoFocusCallback() {
	            @Override
	            public void onAutoFocus(boolean b, Camera camera) {}
	        });
	    }
	}

	/*******************************************************/
	/** DETERMINE L'ESPACE ENTRE LES DEUX PREMIERS DOIGTS **/
	/*******************************************************/
	private float getFingerSpacing(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return (float) Math.sqrt(x * x + y * y);
	}
	
	/***************************/
	/** AFFICHER LA MINIATURE **/
	/***************************/
	public void showMiniature(View view){
		ImageView imageView = (ImageView) findViewById(R.id.normal);
		Button miniature = (Button) findViewById(R.id.Miniature);
		
		if(modeMiniature == 0){
			FrameLayout.LayoutParams paramsMiniature = new FrameLayout.LayoutParams(imageView.getWidth()/4, imageView.getHeight()/4);
			
			paramsMiniature.gravity=Gravity.BOTTOM;
			imageView.setAlpha(imageView.getAlpha());
			
			modeMiniature = 1;
			
			imageView.setLayoutParams(paramsMiniature);
			
			miniature.setEnabled(false);
			miniature.setVisibility(View.INVISIBLE);
			imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	ImageView imageView = (ImageView) findViewById(R.id.normal);
                	Button miniature = (Button) findViewById(R.id.Miniature);
                	LayoutParams paramsReagrandissement = (LayoutParams) imageView.getLayoutParams();
        			paramsReagrandissement.width = -1;
        			paramsReagrandissement.height = -1;
        			imageView.setAlpha(imageView.getAlpha());
        			modeMiniature = 0;
        			imageView.setLayoutParams(paramsReagrandissement);
        			miniature.setVisibility(View.VISIBLE);
        			miniature.setEnabled(true);
                }
			});
		}
	}
	
	/****************************************************/
	/** METHODE POUR DETRUIRE LA VUE (ICI, l'ACTIVITE) **/
	/****************************************************/
	protected void onDestroy(){
		System.out.println("onDestroy -> JE DETRUIS LA VUE ! ");
		super.onDestroy();
		if(mCamera!=null){
			System.out.println("onDestroy -> LA CAMERA N'EST PAS NULL ");
			mCamera.stopPreview();
		    mCamera = null;
		    System.out.println("onDestroy -> DESTRUCTION TERMINEE ");
		}
	}
	
	/********************************************************/
	/** METHODE POUR METTRE LA NOUVELLE VUE APRES ROTATION **/
	/********************************************************/
	protected void onResume(){
		System.out.println("onResume -> JE REMET LA VUE ! ");
		super.onResume();
//		mPreview.getHolder().removeCallback(mPreview);
		System.out.println("onResume -> test ");
	}
}