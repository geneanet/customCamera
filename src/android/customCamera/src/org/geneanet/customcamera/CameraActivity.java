package org.geneanet.customcamera;

// DON'T DELETE THIS LINE, IT'S NECESSARY FOR THE CORDOVA PLUGIN.
import org.geneanet.customcamera.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Activity to use customCamera.
 */
public class CameraActivity extends Activity {
    /**
     * Enable miniature mode.
     */
    private boolean modeMiniature = false;

    /**
     * Camera resource.
     */
    private Camera mCamera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Remove title bar */
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /* Remove notification bar */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera_view);

        // Get the base64 picture for the background only if it's exist.
        Bundle currentBundle = this.getIntent().getExtras();
        if (currentBundle != null) {
            String imgBackgroundBase64 = currentBundle.getString("imgBackgroundBase64");
        }
        
        // The opacity bar
        SeekBar switchOpacity = (SeekBar) findViewById(R.id.switchOpacity);
        
        // Event on change opacity.
        switchOpacity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                ImageView imageView = (ImageView) findViewById(R.id.normal);
                float newOpacity = (float) (0.2+progress*0.1);
                imageView.setAlpha(newOpacity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
     }
    
    @Override
    protected void onStart() {
        super.onStart();
        // Get informations about the default display for the device
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        
        // Init camera resource. 
        if (!initCameraResource()) {
            return;
        }
        
        // Get the default orientation of the device
        int defaultOrientation = getDeviceDefaultOrientation();
        
        // Change camera orientation function of the device's default orientation.
        if (defaultOrientation == 1 || defaultOrientation == 2) {
            int orientation;
            switch(defaultDisplay.getRotation()){
                case 0 :
                    orientation = (defaultOrientation == 1) ? 90 : 0;
                    mCamera.setDisplayOrientation(orientation);
                    break;
                case 1 :
                    orientation = (defaultOrientation == 1) ? 0 : 270;
                    mCamera.setDisplayOrientation(orientation);
                    break;
                case 2 :
                    orientation = (defaultOrientation == 1) ? 270 : 180;
                    mCamera.setDisplayOrientation(orientation);
                    break;
                case 3 :
                    orientation = (defaultOrientation == 1) ? 180 : 90;
                    mCamera.setDisplayOrientation(orientation);
                    break;
            }
        }
        
        // Assign the render camera to the view 
        CameraPreview mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    /**
     * Method to pause the activity.
     */
    protected void onPause() {
        super.onPause();
        ManagerCamera.clearCameraAccess();
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeAllViews();
    }

    // Event on touch screen to call the manager of the zoom & the auto focus.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();
 
        if (event.getPointerCount() > 1) {
            // If we touch with more than one finger
            float distanceBetweenFingers = 0;
            if (action == MotionEvent.ACTION_POINTER_UP) {
                distanceBetweenFingers = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                mCamera.cancelAutoFocus();
                handleZoom(event, params, distanceBetweenFingers);
            }
        } else {
            // If we touch with one finger -> auto-focus
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }
        }
        return true;
    }

    /**
     * Manage the zoom.
     * 
     * @param MotionEvent event                  Current event which start this action.
     * @param Parameters  params                 Camera's parameter.
     * @param float       distanceBetweenFingers Distance between two fingers.
     */
    private void handleZoom(MotionEvent event, Camera.Parameters params, float distanceBetweenFingers) {
        // take zoom max for the camera hardware.
        int maxZoom = params.getMaxZoom();
        // current value for the zoom.
        int zoom = params.getZoom();
        // new distance between fingers.
        float newDist = getFingerSpacing(event);
        
        if (newDist > distanceBetweenFingers) {
            //zoom in
            if (zoom < maxZoom/2)
                zoom+=2;
        } else if (newDist < distanceBetweenFingers) {
            //zoom out
            if (zoom > 0)
                zoom-=2;
            }
        distanceBetweenFingers = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    /**
     * Manage the focus.
     * 
     * @param MotionEvent event  Current event which start this action.
     * @param Parameters  params Camera's parameter.
     */
    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {}
            });
        }
    }

    /**
     * Determine the space between the first two fingers.
     * 
     * @param MotionEvent event  Current event which start this calculation.
     * 
     * @return float
     */
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Display the miniature.
     * @param View view Current view.
     */
    public void showMiniature(View view) {
        // Picture for the background.
        final ImageView imageView = (ImageView) findViewById(R.id.normal);
        // Button for show miniature picture.
        final Button miniature = (Button) view;
    
        // if it's not miniature mode.
        if (!modeMiniature) {
            modeMiniature = true;

            // Set new size for miniature layout.
            FrameLayout.LayoutParams paramsMiniature = new FrameLayout.LayoutParams(imageView.getWidth()/4, imageView.getHeight()/4);
            paramsMiniature.gravity = Gravity.BOTTOM;
            imageView.setLayoutParams(paramsMiniature);

            // Set current opacity for the miniature.
            // imageView.setAlpha(imageView.getAlpha());
            
            // Hide the miniature button.
            miniature.setVisibility(View.INVISIBLE);
            // Add event on click action for the miniature picture.
            imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    modeMiniature = false;

                    // resize miniature.
                    LayoutParams paramsReagrandissement = (LayoutParams) imageView.getLayoutParams();
                    paramsReagrandissement.width = -1;
                    paramsReagrandissement.height = -1;
                    imageView.setLayoutParams(paramsReagrandissement);

                    // imageView.setAlpha(imageView.getAlpha());
                    miniature.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    
    /**
     * Method to get the device default orientation.
     * 
     * @return int
     */
    public int getDeviceDefaultOrientation() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Configuration config = getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        if (
            (config.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                (
                    rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180
                )
            ) ||
            (config.orientation == Configuration.ORIENTATION_PORTRAIT &&
                (
                    rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
                )
            )
        ) {
            return Configuration.ORIENTATION_LANDSCAPE;
        } else { 
            return Configuration.ORIENTATION_PORTRAIT;
        }
    }
    
    /**
     * Method to take picture.
     * 
     * @param view Current view.
     */
    public void takePhoto(View view) {
        final CameraActivity cameraActivityCurrent = this;

        // Handles the moment where picture is taken
        ShutterCallback shutterCallback = new ShutterCallback() {
            public void onShutter() {}
        };

        // Handles data for raw picture
        PictureCallback rawCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {}
        };
        
        // Handles data for jpeg picture
        PictureCallback jpegCallback = new PictureCallback() {

            /**
             * Event when picture is taken.
             * 
             * @param byte[] data   Picture with byte format.
             * @param Camera camera Current resource camera.
             */
            public void onPictureTaken(final byte[] data, Camera camera) {
                // Show buttons to accept or decline the picture.

                final LinearLayout keepPhoto = (LinearLayout) findViewById(R.id.keepPhoto);
                keepPhoto.setVisibility(View.VISIBLE);
                Button accept = (Button)findViewById(R.id.accept);
                Button decline = (Button)findViewById(R.id.decline);

                // Hide the capture button.
                final Button photo = (Button)findViewById(R.id.capture);
                photo.setVisibility(View.INVISIBLE);

                // Put button miniature at the top of the page
                Button miniature = (Button)findViewById(R.id.miniature);
                LayoutParams params = (RelativeLayout.LayoutParams)miniature.getLayoutParams();
                ((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                ((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                miniature.setLayoutParams(params);

                // Stop link between view and camera to start the preview picture.
                mCamera.stopPreview();
                
                // Event started after accept picture.
                accept.setOnClickListener(new View.OnClickListener() {    
                    @Override
                    public void onClick(View v) {
                        try {
                            // Get path picture to storage.
                            String pathPicture = Environment.getExternalStorageDirectory().getPath()+"/"+Environment.DIRECTORY_DCIM+"/Camera/";
                            pathPicture = pathPicture+String.format("%d.jpeg", System.currentTimeMillis());

                            // Write data in file.
                            FileOutputStream outStream = new FileOutputStream(pathPicture);
                            outStream.write(data);
                            outStream.close();

                            // Return to success & finish current activity.
                            cameraActivityCurrent.setResult(1, new Intent().putExtra("pathPicture", pathPicture));
                            cameraActivityCurrent.finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Event started after decline picture.
                decline.setOnClickListener(new View.OnClickListener() {    
                    @Override
                    public void onClick(View v) {
                        keepPhoto.setVisibility(View.INVISIBLE);
                        photo.setVisibility(View.VISIBLE);
                        mCamera.startPreview();
                    }
                });
            };
        };

        // Start capture picture.
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    /**
     * To get camera resource or stop this activity.
     * 
     * @return boolean
     */
    protected boolean initCameraResource()
    {
        mCamera = ManagerCamera.getCameraInstance();

        if (mCamera == null) {
            this.setResult(2, new Intent().putExtra("errorMessage", "Camera is unavailable."));
            this.finish();

            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        this.setResult(3);
        this.finish();
    }
}
