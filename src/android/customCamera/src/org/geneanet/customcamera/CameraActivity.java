package org.geneanet.customcamera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import org.geneanet.customcamera.CameraPreview;
import org.geneanet.customcamera.ManagerCamera;
import org.geneanet.customcamera.TransferBigData;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Activity to use customCamera.
 */
public class CameraActivity extends Activity {
  /**
   * Camera resource.
   */
  private Camera mCamera = null;

  /**
   * Distance between fingers for the zoom
   */
  private static float distanceBetweenFingers;

  /**
   * Enable miniature mode.
   */
  private boolean modeMiniature = false;

  /**
   * Enable when a photo is taken
   */
  private boolean photoTaken = false;

  /**
   * The image in Bitmap format of the preview photo
   */
  private Bitmap storedBitmap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    /* Remove title bar */
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    /* Remove notification bar */
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_camera_view);

    setBackground();

    // The opacity bar
    SeekBar switchOpacity = (SeekBar) findViewById(R.id.switchOpacity);

    // Event on change opacity.
    switchOpacity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      int progress = 0;

      @Override
      public void onProgressChanged(SeekBar seekBar, int progresValue,
          boolean fromUser) {
        progress = progresValue;
        ImageView background = (ImageView) findViewById(R.id.background);
        float newOpacity = (float) (0.2 + progress * 0.1);
        background.setAlpha(newOpacity);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
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

    // Change camera orientation function of the device's default
    // orientation.
    if (defaultOrientation == 1 || defaultOrientation == 2) {
      int orientation;
      switch (defaultDisplay.getRotation()) {
      case 0:
        orientation = (defaultOrientation == 1) ? 90 : 0;
        mCamera.setDisplayOrientation(orientation);
        break;
      case 1:
        orientation = (defaultOrientation == 1) ? 0 : 270;
        mCamera.setDisplayOrientation(orientation);
        break;
      case 2:
        orientation = (defaultOrientation == 1) ? 270 : 180;
        mCamera.setDisplayOrientation(orientation);
        break;
      case 3:
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
    final SeekBar zoomLevel = (SeekBar) findViewById(R.id.zoomLevel);
    final Camera.Parameters paramsCamera = mCamera.getParameters();
    final int zoom = paramsCamera.getZoom();
    int maxZoom = paramsCamera.getMaxZoom();

    zoomLevel.setMax(maxZoom);
    zoomLevel.setProgress(zoom);
    zoomLevel.setVisibility(View.VISIBLE);

    // Event on change zoom with the bar.
    zoomLevel.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      int progress = 0;

      @Override
      public void onProgressChanged(SeekBar seekBar, int progresValue,
          boolean fromUser) {
        progress = progresValue;
        int newZoom = (int) (zoom + progress);
        zoomLevel.setProgress(newZoom);
        paramsCamera.setZoom(newZoom);
        mCamera.setParameters(paramsCamera);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
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
      Camera.Parameters paramsCamera = mCamera.getParameters();
      int action = event.getAction();

      if (event.getPointerCount() > 1) {
        // If we touch with more than one finger
        if (action == MotionEvent.ACTION_POINTER_2_DOWN) {
          distanceBetweenFingers = getFingerSpacing(event);
        } else if (action == MotionEvent.ACTION_MOVE
            && paramsCamera.isZoomSupported()) {
          mCamera.cancelAutoFocus();
          handleZoom(event, paramsCamera, distanceBetweenFingers);
        }
      } else {
        // If we touch with one finger -> auto-focus
        if (action == MotionEvent.ACTION_UP) {
          handleFocus(event, paramsCamera);
        }
      }
    }
    return true;
  }

  /**
   * Determine the space between the first two fingers.
   * 
   * @param MotionEvent
   *          event Current event which start this calculation.
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
   * @param MotionEvent
   *          event Current event which start this action.
   * @param Parameters
   *          paramsCamera Camera's parameter.
   * @param float distanceBetweenFingers Distance between two fingers.
   */
  private void handleZoom(MotionEvent event, Camera.Parameters paramsCamera,
      float distanceBetweenFingers) {
    // take zoom max for the camera hardware.
    int maxZoom = paramsCamera.getMaxZoom();
    // current value for the zoom.
    int zoom = paramsCamera.getZoom();
    setZoomProgress(maxZoom, zoom);
    // new distance between fingers.
    float newDist = getFingerSpacing(event);

    if (newDist > distanceBetweenFingers) {
      // zoom in
      if (zoom < maxZoom) {
        zoom++;
      }
    } else if (newDist < distanceBetweenFingers) {
      // zoom out
      if (zoom > 0) {
        zoom--;
      }
    }
    distanceBetweenFingers = newDist;
    paramsCamera.setZoom(zoom);
    mCamera.setParameters(paramsCamera);
  }

  /**
   * To set the seekBar zoom with the pinchZoom
   * 
   * @param maxZoom
   *          int the max zoom of the device
   * @param zoom
   *          int the current zoom
   */
  private void setZoomProgress(int maxZoom, int zoom) {
    SeekBar zoomLevel = (SeekBar) findViewById(R.id.zoomLevel);
    zoomLevel.setMax(maxZoom);
    zoomLevel.setProgress(zoom * 2);
    zoomLevel.setVisibility(View.VISIBLE);
  }

  /**
   * Manage the focus.
   * 
   * @param MotionEvent
   *          event Current event which start this action.
   * @param Parameters
   *          paramsCamera Camera's parameter.
   */
  public void handleFocus(MotionEvent event, Camera.Parameters paramsCamera) {
    if (photoTaken == false) {
      List<String> supportedFocusModes = paramsCamera.getSupportedFocusModes();
      if (supportedFocusModes != null
          && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
          @Override
          public void onAutoFocus(boolean b, Camera camera) {
          }
        });
      }
    }
  }

  /**
   * Display the miniature.
   * 
   * @param View
   *          view Current view.
   */
  public void showMiniature(View view) {
    ImageView background = (ImageView) findViewById(R.id.background);
    final Button miniature = (Button) view;

    // if it's not miniature mode.
    if (!modeMiniature) {
      modeMiniature = true;
      // Set new size for miniature layout.
      setParamsMiniature(background, true);

      // Hide the miniature button.
      miniature.setVisibility(View.INVISIBLE);
      // Add event on click action for the miniature picture.
      background.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          modeMiniature = false;
          ImageView background = (ImageView) findViewById(R.id.background);
          // resize miniature.
          background.setClickable(false);
          setBackground();

          miniature.setVisibility(View.VISIBLE);
        }
      });
    }
  }

  /**
   * Set the size and the gravity of the miniature function of photo is taken or
   * not.
   * 
   * @param ImageView
   *          imageView Reference to the background image.
   * @param Boolean
   *          Resize Should we resize or not ? Only when click on "miniature"
   */
  public void setParamsMiniature(ImageView imageView, boolean resize) {
    RelativeLayout.LayoutParams paramsMiniature = new RelativeLayout.LayoutParams(
        imageView.getWidth(), imageView.getHeight());
    if (resize == true) {
      paramsMiniature.width = imageView.getWidth() / 4;
      paramsMiniature.height = imageView.getHeight() / 4;
    }
    if (!photoTaken) {
      paramsMiniature.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
          RelativeLayout.TRUE);
    } else {
      paramsMiniature.addRule(RelativeLayout.ALIGN_PARENT_TOP,
          RelativeLayout.TRUE);
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

    if ((config.orientation == Configuration.ORIENTATION_LANDSCAPE && (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180))
        || (config.orientation == Configuration.ORIENTATION_PORTRAIT && (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270))) {
      return Configuration.ORIENTATION_LANDSCAPE;
    } else {
      return Configuration.ORIENTATION_PORTRAIT;
    }
  }

  /**
   * Method to take picture.
   * 
   * @param view
   *          Current view.
   */
  public void takePhoto(View view) {

    // Handles the moment where picture is taken
    ShutterCallback shutterCallback = new ShutterCallback() {
      public void onShutter() {
      }
    };

    // Handles data for raw picture
    PictureCallback rawCallback = new PictureCallback() {
      public void onPictureTaken(byte[] data, Camera camera) {
      }
    };

    // Handles data for jpeg picture
    PictureCallback jpegCallback = new PictureCallback() {

      /**
       * Event when picture is taken.
       * 
       * @param byte[] data Picture with byte format.
       * @param Camera
       *          camera Current resource camera.
       */
      public void onPictureTaken(final byte[] data, Camera camera) {
        // Show buttons to accept or decline the picture.
        LinearLayout keepPhoto = (LinearLayout) findViewById(R.id.keepPhoto);
        keepPhoto.setVisibility(View.VISIBLE);

        // Hide the capture button.
        Button photo = (Button) findViewById(R.id.capture);
        photo.setVisibility(View.INVISIBLE);

        // Hide the zoom progressBar
        SeekBar zoomLevel = (SeekBar) findViewById(R.id.zoomLevel);
        zoomLevel.setVisibility(View.INVISIBLE);

        // Put button miniature at the top of the page
        Button miniature = (Button) findViewById(R.id.miniature);
        LayoutParams paramsLayoutAcceptDecline = (LinearLayout.LayoutParams) miniature
            .getLayoutParams();
        ((LinearLayout.LayoutParams) paramsLayoutAcceptDecline).gravity = Gravity.TOP;
        miniature.setLayoutParams(paramsLayoutAcceptDecline);

        photoTaken = true;

        // If miniature mode when photo is taken, the miniature goes to
        // the top
        if (modeMiniature) {
          ImageView background = (ImageView) findViewById(R.id.background);
          setParamsMiniature(background, false);
        }

        // Stop link between view and camera to start the preview
        // picture.
        mCamera.stopPreview();
        BitmapFactory.Options opt;
        opt = new BitmapFactory.Options();

        // Temp storage to use for decoding
        opt.inTempStorage = new byte[16 * 1024];
        Camera.Parameters paramsCamera = mCamera.getParameters();
        Size size = paramsCamera.getPictureSize();

        int height = size.height;
        int width = size.width;
        float res = (width * height) / 1024000;

        // Return a smaller image for now
        if (res > 4f)
          opt.inSampleSize = 4;
        else if (res > 3f)
          opt.inSampleSize = 2;

        // Preview from camera
        storedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);

        // Lock the screen until the accept/decline button is clicked
        // ---> OPTION
        // lockScreen(true);

        assignFunctionButtonAcceptAndDecline();

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
  public float redirectPhotoInGallery() {
    WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    int defaultOrientation = getDeviceDefaultOrientation();
    int redirect = 0;
    int orientationCamera = getOrientationOfCamera();

    if (defaultOrientation == 1 || defaultOrientation == 2) {
      switch (windowManager.getDefaultDisplay().getRotation()) {
      case 0:
        redirect = (defaultOrientation == 1) ? 90 : 0;
        // If the device is in front camera by default
        if (orientationCamera == 1 && defaultOrientation == 1) {
          redirect = 270;
        }
        break;
      case 1:
        redirect = (defaultOrientation == 1) ? 0 : 270;
        break;
      case 2:
        redirect = (defaultOrientation == 1) ? 270 : 180;
        // If the device is in front camera by default
        if (orientationCamera == 1 && defaultOrientation == 1) {
          redirect = 90;
        }
        break;
      case 3:
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
    CameraInfo info = new Camera.CameraInfo();
    // Get info of the default camera (which is called by default)
    Camera.getCameraInfo(0, info);

    return info.facing;
  }

  /**
   * To get camera resource or stop this activity.
   * 
   * @return boolean
   */
  protected boolean initCameraResource() {
    mCamera = ManagerCamera.getCameraInstance();

    if (mCamera == null) {
      this.setResult(2,
          new Intent().putExtra("errorMessage", "Camera is unavailable."));
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
      Bitmap imgBackgroundBitmap = BitmapFactory.decodeByteArray(
          imgBackgroundBase64, 0, imgBackgroundBase64.length);

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
      RelativeLayout.LayoutParams paramsMiniature = new RelativeLayout.LayoutParams(
          widthBackground, heightBackground);
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
      ImageView background = (ImageView) findViewById(R.id.background);
      background.setImageBitmap(imgBackgroundBitmap);

      paramsMiniature.addRule(RelativeLayout.CENTER_IN_PARENT,
          RelativeLayout.TRUE);
      background.setLayoutParams(paramsMiniature);
    }
  }

  /**
   * To save some contains of the activity
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putBoolean("photoTaken", photoTaken);
    outState.putBoolean("modeMiniature", modeMiniature);
    outState.putParcelable("storedBitmap", storedBitmap);
    super.onSaveInstanceState(outState);
  }

  /**
   * To restore the contains saved on the method onSaveInstanceState()
   */
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
    LinearLayout keepPhoto = (LinearLayout) findViewById(R.id.keepPhoto);
    LinearLayout buttonMiniatureAndPhoto = (LinearLayout) findViewById(R.id.buttonMiniatureAndPhoto);
    photoTaken = savedInstanceState.getBoolean("photoTaken");
    modeMiniature = savedInstanceState.getBoolean("modeMiniature");
    storedBitmap = savedInstanceState.getParcelable("storedBitmap");

    // If the photo is taken when we orient the device
    if (photoTaken) {

      // Matrix to perform rotation
      Matrix mat = new Matrix();
      mat.postRotate(90);

      // Creation of the bitmap
      storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0,
          storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
      Bitmap newBitmap = resizeAfterRotate(storedBitmap);
      ImageView photoResized = (ImageView) findViewById(R.id.photoResized);
      photoResized.setImageBitmap(newBitmap);
      preview.setVisibility(View.INVISIBLE);

      keepPhoto.setVisibility(View.VISIBLE);
      buttonMiniatureAndPhoto.setVisibility(View.INVISIBLE);
      assignFunctionButtonAcceptAndDecline();
    }

    super.onRestoreInstanceState(savedInstanceState);
  }

  /**
   * Resize the bitmap saved when you rotate the device.
   * 
   * @param Bitmap
   *          bitmap The original bitmap
   * 
   * @return the new bitmap.
   */
  protected Bitmap resizeAfterRotate(Bitmap bitmap) {
    // Initialize the new bitmap resized
    Bitmap newBitmap = null;

    // Get sizes screen.
    Display defaultDisplay = getWindowManager().getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    defaultDisplay.getMetrics(displayMetrics);
    int displayWidthPx = (int) displayMetrics.widthPixels;
    int displayHeightPx = (int) displayMetrics.heightPixels;

    // Get sizes picture.
    int widthBackground = (int) (bitmap.getWidth() * displayMetrics.density);
    int heightBackground = (int) (bitmap.getHeight() * displayMetrics.density);

    // Change size ImageView.
    float ratioX = (float) displayWidthPx / (float) widthBackground;
    float ratioY = (float) displayHeightPx / (float) heightBackground;
    if (ratioX < ratioY && ratioX < 1) {
      newBitmap = Bitmap.createScaledBitmap(bitmap, (int) displayWidthPx,
          (int) (ratioX * heightBackground), false);
    } else if (ratioX >= ratioY && ratioY < 1) {
      newBitmap = Bitmap.createScaledBitmap(bitmap,
          (int) (ratioY * widthBackground), (int) displayHeightPx, false);
    }

    return newBitmap;
  }

  /**
   * Allow to lock the screen or not
   * 
   * @param Do
   *          we have to lock or not ?
   */
  protected void lockScreen(boolean lock) {
    if (lock == false) {
      this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    } else {
      Display defaultDisplay = getWindowManager().getDefaultDisplay();
      int defaultOrientation = this.getDeviceDefaultOrientation();
      if (defaultOrientation == Configuration.ORIENTATION_LANDSCAPE) {
        switch (defaultDisplay.getRotation()) {
        case 0:
          this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
          break;
        case 1:
          this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
          break;
        case 2:
          this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
          break;
        case 3:
          this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
          break;
        }

      } else if (defaultOrientation == Configuration.ORIENTATION_PORTRAIT) {
        switch (defaultDisplay.getRotation()) {
        case 0:
          this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
          break;
        case 1:
          this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
          break;
        case 2:
          this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
          break;
        case 3:
          this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
          break;
        }
      }

    }
  }

  /**
   * Assign actions on button "accept" and "decline"
   */
  protected void assignFunctionButtonAcceptAndDecline() {
    final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
    final LinearLayout keepPhoto = (LinearLayout) findViewById(R.id.keepPhoto);
    final LinearLayout buttonMiniatureAndPhoto = (LinearLayout) findViewById(R.id.buttonMiniatureAndPhoto);
    final Button miniature = (Button) findViewById(R.id.miniature);
    final Button accept = (Button) findViewById(R.id.accept);
    final Button decline = (Button) findViewById(R.id.decline);
    final Button photo = (Button) findViewById(R.id.capture);
    final CameraActivity cameraActivityCurrent = this;

    // Event started after accept picture.
    accept.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          // Matrix to perform rotation
          Matrix mat = new Matrix();
          float redirect = redirectPhotoInGallery();
          mat.postRotate(redirect);

          // Creation of the bitmap
          storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0,
              storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          storedBitmap.compress(CompressFormat.JPEG, 70, stream);

          photoTaken = false;
          // Get path picture to storage.
          String pathPicture = Environment.getExternalStorageDirectory()
              .getPath() + "/" + Environment.DIRECTORY_DCIM + "/Camera/";
          pathPicture = pathPicture
              + String.format("%d.jpeg", System.currentTimeMillis());

          // Write data in file.
          FileOutputStream outStream = new FileOutputStream(pathPicture);
          outStream.write(stream.toByteArray());
          outStream.close();

          // Return to success & finish current activity.
          cameraActivityCurrent.setResult(1,
              new Intent().putExtra("pathPicture", pathPicture));
          cameraActivityCurrent.finish();

          // Unlock the screen ---> OPTION
          // lockScreen(false);
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
        LayoutParams paramsLayoutAcceptDecline = (LinearLayout.LayoutParams) miniature
            .getLayoutParams();
        ((LinearLayout.LayoutParams) paramsLayoutAcceptDecline).gravity = Gravity.BOTTOM;
        miniature.setLayoutParams(paramsLayoutAcceptDecline);

        // If mode miniature and photo is declined, the miniature goes
        // back to the bottom
        if (modeMiniature) {
          ImageView background = (ImageView) findViewById(R.id.background);
          setParamsMiniature(background, false);
        }
        keepPhoto.setVisibility(View.INVISIBLE);
        photo.setVisibility(View.VISIBLE);
        SeekBar zoomLevel = (SeekBar) findViewById(R.id.zoomLevel);
        zoomLevel.setVisibility(View.VISIBLE);
        ImageView photoResized = (ImageView) findViewById(R.id.photoResized);
        photoResized.setVisibility(View.INVISIBLE);
        mCamera.startPreview();
        preview.setVisibility(View.VISIBLE);
        buttonMiniatureAndPhoto.setVisibility(View.VISIBLE);

        // Unlock the screen ---> OPTION
        // lockScreen(false);
      }
    });
  }
}
