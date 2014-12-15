package org.geneanet.customcamera;

/**
 * Use to transfer big data between activities.
 */
public class TransferBigData {
    protected static byte[] imgBackgroundBase64 = null;

    /**
     * Get bytes to represent background picture.
     * @return byte[]
     */
    public static byte[] getImgBackgroundBase64() {
        return TransferBigData.imgBackgroundBase64;
    }

    /**
     * Set bytes to represent background picture.
     * @param byte[] imgBackgroundBase64
     */
    public static void setImgBackgroundBase64(byte[] imgBackgroundBase64) {
        TransferBigData.imgBackgroundBase64 = imgBackgroundBase64;
    }
}
