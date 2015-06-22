package org.geneanet.customcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

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
   * Determine the best value of inSampleSize in the option object to adapt the picture at the screen size.
   * 
   * @param Options options Option object to set inSampleSize.
   * @param int destWidth Width destination.
   * @param int destHeight Height destination.
   */
  public static void determineInSampleSize(Options options, int destWidth, int destHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > destHeight || width > destWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        // More informations: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#load-bitmap
        while ((halfHeight / inSampleSize) > destHeight
                && (halfWidth / inSampleSize) > destWidth) {
            inSampleSize *= 2;
        }
    }
    
    options.inSampleSize = inSampleSize;
  }
  
  /**
   * Decode a byte array to generate a base64 adapted at the destination's size.
   * 
   * @param bytes[] imgBackgroundBase64
   * @param Options options
   * 
   * @return Bitmap
   */
  public static Bitmap decodeOptimalPictureFromByteArray(byte[] imgBackgroundBase64, Options options) {
    options.inJustDecodeBounds = false;
    
    return BitmapFactory.decodeByteArray(imgBackgroundBase64, 0, imgBackgroundBase64.length, options);
  }
}
