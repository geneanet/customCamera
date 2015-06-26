package org.geneanet.customcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.DisplayMetrics;
import android.view.Display;

public class BitmapUtils {
  /**
   * Determine the original size of picture.
   * 
   * @param bytes[] imgBackgroundBase64
   * 
   * @return Options Return the options object with sizes set.
   */
  public static Options determineOriginalSizePicture(byte[] imgBackgroundBase64) {
    Options options = new Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeByteArray(imgBackgroundBase64, 0, imgBackgroundBase64.length, options);
    
    return options;
  }
  
  /**
   * Generate a bitmap optimized from the screen sizes.
   * 
   * @param activity Current activity.
   * @param data Bytes represent the picture. 
   * 
   * @return Bitmap
   */
  public static Bitmap generateOptimizeBitmap(Activity activity, byte[] data) {
    // Get sizes screen.
    Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    defaultDisplay.getMetrics(displayMetrics);
    int displayWidthPx = (int) displayMetrics.widthPixels;
    int displayHeightPx = (int) displayMetrics.heightPixels;
    // Get picture.
    Options options = BitmapUtils.determineOriginalSizePicture(data);
    int widthResize = 0;
    int heightResize = 0;
    int widthBackground = (int) (options.outWidth * displayMetrics.density);
    int heightBackground= (int) (options.outHeight * displayMetrics.density);
    float ratioX = (float) widthBackground / (float) displayWidthPx;
    float ratioY = (float) heightBackground / (float) displayHeightPx;
    int inSampleSize = 1;
    if (ratioX > ratioY && ratioX > 1) {
      widthResize = (int) displayWidthPx;
      heightResize = (int) (heightBackground / ratioX);
      inSampleSize = (int) ratioX;
    } else if (ratioX <= ratioY && ratioY > 1) {
      widthResize = (int) (widthBackground / ratioY);
      heightResize = (int) displayHeightPx;
      inSampleSize = (int) ratioY;
    }
    options.inSampleSize = inSampleSize;
    options.inJustDecodeBounds = false;
    
    Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length, options);
    data = null;
    if (widthResize > 0 && heightResize > 0) {
      picture = Bitmap.createScaledBitmap(picture, widthResize, heightResize, true);
    }
    
    return picture;
  }
}
