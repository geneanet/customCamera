package org.geneanet.customcamera;

// DON'T DELETE THIS LINE, IT'S NECESSARY FOR THE CORDOVA PLUGIN.
import org.geneanet.customcamera.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
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
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
     * Enable when a photo is taken
     */
    private boolean photoTaken = false;

    /**
     * Camera resource.
     */
    private Camera mCamera = null;
    
    /**
     * Distance between fingers for the zoom
     */
    private static float distanceBetweenFingers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Remove title bar */
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /* Remove notification bar */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera_view);

        setBackground();
        
        // The opacity bar
        SeekBar switchOpacity = (SeekBar) findViewById(R.id.switchOpacity);
        
        // Event on change opacity.
        switchOpacity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                ImageView imageView = (ImageView) findViewById(R.id.background);
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
            switch (defaultDisplay.getRotation()) {
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
        
        // The zoom bar progress
        final Camera.Parameters params = mCamera.getParameters();
        final SeekBar niveauZoom = (SeekBar) findViewById(R.id.niveauZoom);
        int maxZoom = params.getMaxZoom();
        final int zoom = params.getZoom();
        niveauZoom.setMax(maxZoom);
        niveauZoom.setProgress(zoom);
        niveauZoom.setVisibility(View.VISIBLE);     
        
        // Event on change zoom with the bar.
        niveauZoom.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                int newZoom = (int)(zoom+progress);
                niveauZoom.setProgress(newZoom);
                params.setZoom(newZoom);
                mCamera.setParameters(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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
        if (!photoTaken) {
            Camera.Parameters params = mCamera.getParameters();
            int action = event.getAction();
     
            if (event.getPointerCount() > 1) {
                // If we touch with more than one finger
                if (action == MotionEvent.ACTION_POINTER_2_DOWN) {
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
        }
        return true;
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
        setZoomProgress(maxZoom, zoom);
        // new distance between fingers.
        float newDist = getFingerSpacing(event);
        
        if (newDist > distanceBetweenFingers) {
            //zoom in
            if (zoom < maxZoom) {
                zoom++;
            }
        } else if (newDist < distanceBetweenFingers) {
            //zoom out
            if (zoom > 0) {
                zoom--;
            }
        }
        distanceBetweenFingers = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }
    
    /**
     * To set the seekBar zoom with the pinchZoom
     * 
     * @param maxZoom int the max zoom of the device
     * @param zoom    int the current zoom
     */
    private void setZoomProgress(int maxZoom, int zoom) {    
        SeekBar niveauZoom = (SeekBar) findViewById(R.id.niveauZoom);   
        niveauZoom.setMax(maxZoom);
        niveauZoom.setProgress(zoom*2);
        niveauZoom.setVisibility(View.VISIBLE);
    }

    /**
     * Manage the focus.
     * 
     * @param MotionEvent event  Current event which start this action.
     * @param Parameters  params Camera's parameter.
     */
    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        if (photoTaken == false) {
            List<String> supportedFocusModes = params.getSupportedFocusModes();
            if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {}
                });
            }
        }
    }
    
    /**
     * Display the miniature.
     * @param View view Current view.
     */
    public void showMiniature(View view) {
        // Picture for the background.
        ImageView imageView = (ImageView) findViewById(R.id.background);
        // Button for show miniature picture.
        final Button miniature = (Button) view;
    
        // if it's not miniature mode.
        if (!modeMiniature) {
            modeMiniature = true;
            // Set new size for miniature layout.
            setParamsMiniature(imageView, true);
            
            // Hide the miniature button.
            miniature.setVisibility(View.INVISIBLE);
            // Add event on click action for the miniature picture.
            imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    modeMiniature = false;
                    ImageView imageView = (ImageView) findViewById(R.id.background);
                    // resize miniature.
                    imageView.setClickable(false);
                    setBackground();

                    miniature.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    
    /**
     * Set the size and the gravity of the miniature function of photo is taken or not.
     * 
     * @param ImageView imageView  Reference to the background image.
     * @param Boolean   Resize     Should we resize or not ? Only when click on "miniature"
     */
    public void setParamsMiniature(ImageView imageView, boolean resize) {
        RelativeLayout.LayoutParams paramsMiniature = new RelativeLayout.LayoutParams(imageView.getWidth(), imageView.getHeight());    
        if (resize == true) {
            paramsMiniature.width = imageView.getWidth()/4;
            paramsMiniature.height = imageView.getHeight()/4;
        }
        if (!photoTaken) {
            paramsMiniature.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        }else {
            paramsMiniature.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        }
        imageView.setLayoutParams(paramsMiniature);       
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
                
                // Hide the zoom progressBar
                final SeekBar niveauZoom = (SeekBar) findViewById(R.id.niveauZoom);
                niveauZoom.setVisibility(View.INVISIBLE);

                // Put button miniature at the top of the page
                final Button miniature = (Button)findViewById(R.id.miniature);
                final LayoutParams params = (LinearLayout.LayoutParams)miniature.getLayoutParams();
                ((LinearLayout.LayoutParams) params).gravity = Gravity.TOP;
                miniature.setLayoutParams(params);
  
                photoTaken = true;
                
                // If miniature mode when photo is taken, the miniature goes to the top
                if (modeMiniature) {
                    ImageView imageView = (ImageView) findViewById(R.id.background);
                    setParamsMiniature(imageView, false);
                }               

                // Stop link between view and camera to start the preview picture.
                mCamera.stopPreview();
                
                // Event started after accept picture.
                accept.setOnClickListener(new View.OnClickListener() {    
                    @Override
                    public void onClick(View v) {
                        try {
                            BitmapFactory.Options opt;
                            opt = new BitmapFactory.Options();
                            // Temp storage to use for decoding
                            opt.inTempStorage = new byte[16 * 1024];
                            Parameters parameters = mCamera.getParameters();
                            Size size = parameters.getPictureSize();

                            int height = size.height;
                            int width = size.width;
                            float res = (width * height) / 1024000;

                            // Return a smaller image for now
                            if (res > 4f)
                                opt.inSampleSize = 4;
                            else if (res > 3f)
                                opt.inSampleSize = 2;

                            // Preview from camera
                            Bitmap storedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length,opt); 
                            
                            // Matrix to perform rotation
                            Matrix mat = new Matrix();  
                            float redirect = redirectPhotoInGallery();
                            mat.postRotate(redirect);
                            
                            // Creation of the bitmap
                            storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            storedBitmap.compress(CompressFormat.JPEG, 70, stream);
                            
                            photoTaken = false;
                            // Get path picture to storage.
                            String pathPicture = Environment.getExternalStorageDirectory().getPath()+"/"+Environment.DIRECTORY_DCIM+"/Camera/";
                            pathPicture = pathPicture+String.format("%d.jpeg", System.currentTimeMillis());

                            // Write data in file.
                            FileOutputStream outStream = new FileOutputStream(pathPicture);
                            outStream.write(stream.toByteArray());
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
                        photoTaken = false;
                        ((LinearLayout.LayoutParams) params).gravity = Gravity.BOTTOM;
                        miniature.setLayoutParams(params);
                        
                        // If mode miniature and photo is declined, the miniature goes back to the bottom
                        if (modeMiniature) {
                            ImageView imageView = (ImageView) findViewById(R.id.background);
                            setParamsMiniature(imageView, false);
                        }
                        
                        keepPhoto.setVisibility(View.INVISIBLE);
                        photo.setVisibility(View.VISIBLE);
                        niveauZoom.setVisibility(View.VISIBLE);
                        mCamera.startPreview();
                    }
                });
            };
        };
        // Start capture picture.
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }
    
    /**
     * To redirect (by rotation) the image stored in gallery
     * 
     * @return integer
     */
    public float redirectPhotoInGallery(){
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        int defaultOrientation = getDeviceDefaultOrientation();    
        int redirect = 0;
        int orientationCamera = getOrientationOfCamera();
        
        if (defaultOrientation == 1 || defaultOrientation == 2) {
            switch(windowManager.getDefaultDisplay().getRotation()){
                case 0 :
                    redirect = (defaultOrientation == 1) ? 90 : 0;
                    // If the device is in front camera by default
                    if (orientationCamera == 1 && defaultOrientation == 1) {
                        redirect = 270;
                    }
                    break;
                case 1 :
                    redirect = (defaultOrientation == 1) ? 0 : 270;
                    break;
                case 2 :
                    redirect = (defaultOrientation == 1) ? 270 : 180;
                    // If the device is in front camera by default
                    if (orientationCamera == 1 && defaultOrientation == 1) {
                        redirect = 90;
                    }
                    break;
                case 3 :
                    redirect = (defaultOrientation == 1) ? 180 : 90;
                    break;
            }
        }
        
        return redirect;
    }
    
    /**
     * Get the orientation of the current camera
     * 
     * @return The orientation of the current camera (FRONT OR BACK)
     */
    public int getOrientationOfCamera() {
        CameraInfo info =new Camera.CameraInfo();
        // Get info of the default camera (which is called by default)
        Camera.getCameraInfo(0, info);
        
        return info.facing;
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
    
    /**
     * When the back button is pressed
     */
    @Override
    public void onBackPressed() {
        this.setResult(3);
        this.finish();
    }

    /**
     * To set background in the view.
     */
    protected void setBackground() {
        // Get the base64 picture for the background only if it's exist.
        byte[] imgBackgroundBase64 = TransferBigData.getImgBackgroundBase64();
        if (imgBackgroundBase64 != null) {
            // Get picture.
            Bitmap imgBackgroundBitmap = BitmapFactory.decodeByteArray(imgBackgroundBase64, 0, imgBackgroundBase64.length);

            // Get sizes screen.
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            int displayWidthPx = (int) displayMetrics.widthPixels;
            int displayHeightPx = (int) displayMetrics.heightPixels;

            // Get sizes picture.
            int widthBackground = (int) (imgBackgroundBitmap.getWidth() * displayMetrics.density);
            int heightBackground = (int) (imgBackgroundBitmap.getHeight() * displayMetrics.density);

            // Change size ImageView.
            RelativeLayout.LayoutParams paramsMiniature = new RelativeLayout.LayoutParams(widthBackground, heightBackground);
            float ratioX = (float) displayWidthPx / (float) widthBackground;
            float ratioY = (float) displayHeightPx / (float) heightBackground;
            if (ratioX < ratioY && ratioX < 1) {
                paramsMiniature.width = (int) displayWidthPx;
                paramsMiniature.height = (int) (ratioX * heightBackground);
            } else if (ratioX >= ratioY && ratioY < 1) {
                paramsMiniature.width = (int) (ratioY * widthBackground);
                paramsMiniature.height = (int) displayHeightPx;
            }

            // set image at the view.
            ImageView imageView = (ImageView) findViewById(R.id.background);
            imageView.setImageBitmap(imgBackgroundBitmap);

            paramsMiniature.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            imageView.setLayoutParams(paramsMiniature);
        }
    }
}
