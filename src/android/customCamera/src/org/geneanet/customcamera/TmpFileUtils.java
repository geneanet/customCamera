package org.geneanet.customcamera;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.ExifInterface;
import android.util.Log;

public class TmpFileUtils {
  /**
   * Create a tmp file in app directory.
   * 
   * @param activity To storage in tmp directory of the activity.
   * @param fileName File name.
   * @param img      Image in bytes.
   */
  public static void createTmpFile(Activity activity, String fileName, byte[] img) {
    FileOutputStream fos = null;
    try {
      fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
      try {
        fos.write(img);
        fos.close();
      } catch (IOException e1) {
        Log.e("customCamera", "Can't write or close the file: "+fileName);
      }
    } catch (FileNotFoundException e1) {
      Log.e("customCamera", "Can't open the file: "+fileName);
    }
  }
  
  /**
   * Get the file content from this name. 
   * 
   * @param activity To getin tmp directory of the activity.
   * @param fileName
   * 
   * @return byte[]
   */
  public static byte[] getTmpFileContent(Activity activity, String fileName) {
    FileInputStream fis = null;
    ByteArrayOutputStream ous = null;
    byte[] returnValue = null;

    try {
      byte[] buffer = new byte[4096];
      ous = new ByteArrayOutputStream();
      fis = activity.openFileInput(fileName);
      int read = 0;
      while ( (read = fis.read(buffer)) != -1 ) {
          ous.write(buffer, 0, read);
      }
      fis.close();
      returnValue = ous.toByteArray();
      ous.close();
    } catch (FileNotFoundException e) {
      Log.e("customCamera", "Can't open the file: "+fileName);
    } catch (IOException e1) {
      Log.e("customCamera", "Can't read or close the file: "+fileName);
    }
    
    return returnValue;
  }
  
  /**
   * determine the rotate for a picture based on exif data.
   * 
   * @param filePath
   * 
   * @return int
   */
  public static int determineRotateBasedOnExifFromFilePath(String filePath) {
    ExifInterface exif;
    int rotate = 0;
    try {
      exif = new ExifInterface(filePath);
      int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

      switch (orientation) {
      case ExifInterface.ORIENTATION_ROTATE_270:
          rotate = 270;
          break;
      case ExifInterface.ORIENTATION_ROTATE_180:
          rotate = 180;
          break;
      case ExifInterface.ORIENTATION_ROTATE_90:
          rotate = 90;
          break;
      }
    } catch (IOException e) {
      Log.e("customCamera", "Can't determine EXIF orientation of :"+filePath+". Error message: "+e.getMessage());
    }
    
    return rotate;
  }
}
