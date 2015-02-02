package org.geneanet.customcamera;

/**
 * Use to transfer big data between activities.
 */
public class TransferBigData {
  protected static byte[] imgBackgroundBase64 = null;
  protected static byte[] imgBackgroundBase64OtherOrientation = null;
  protected static byte[] imgTaken = null;

  /**
   * Get bytes to represent background picture.
   * 
   * @return byte[]
   */
  public static byte[] getImgBackgroundBase64() {
    return TransferBigData.imgBackgroundBase64;
  }

  /**
   * Set bytes to represent background picture.
   * 
   * @param byte[] imgBackgroundBase64
   */
  public static void setImgBackgroundBase64(byte[] imgBackgroundBase64) {
    TransferBigData.imgBackgroundBase64 = imgBackgroundBase64;
  }

  /**
   * Get bytes to represent background picture for OtherOrientation.
   * 
   * @return byte[]
   */
  public static byte[] getImgBackgroundBase64OtherOrientation() {
    return TransferBigData.imgBackgroundBase64OtherOrientation;
  }

  /**
   * Set bytes to represent background picture for OtherOrientation.
   * 
   * @param byte[] imgBackgroundBase64OtherOrientation
   */
  public static void setImgBackgroundBase64OtherOrientation(byte[] imgBackgroundBase64OtherOrientation) {
    TransferBigData.imgBackgroundBase64OtherOrientation = imgBackgroundBase64OtherOrientation;
  }

  /**
   * Get bytes to represent picture taken.
   * 
   * @return byte[]
   */
  public static byte[] getImgTaken() {
    return TransferBigData.imgTaken;
  }

  /**
   * Set bytes to represent picture taken.
   * 
   * @param byte[] imgTaken
   */
  public static void setImgTaken(byte[] imgTaken) {
    TransferBigData.imgTaken = imgTaken;
  }
}
