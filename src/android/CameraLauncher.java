package org.geneanet.customcamera;

import XXX_NAME_CURRENT_PACKAGE_XXX.CameraActivity;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import android.content.Intent;
import android.util.Base64;

public class CameraLauncher extends CordovaPlugin {

    protected CallbackContext callbackContext;

    protected static final int RESULT_SUCCESS = 1;
    protected static final int RESULT_ERROR = 2;
    protected static final int RESULT_BACK = 3;

    protected static final int REQUEST_CODE = 88224646;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startCamera")) {
            this.callbackContext = callbackContext;

            Intent intent = new Intent(this.cordova.getActivity(), CameraActivity.class);

            byte[] imgBackgroundBase64;
            try {
                imgBackgroundBase64 = Base64.decode(args.getString(0), Base64.NO_WRAP);
            } catch (IllegalArgumentException e) {
                this.callbackContext.error(
                    generateError(
                        CameraLauncher.RESULT_ERROR,
                        "Error decode base64 picture."
                    )
                );

                return false;
            }
            TransferBigData.setImgBackgroundBase64(imgBackgroundBase64);

            cordova.startActivityForResult((CordovaPlugin) this, intent, CameraLauncher.REQUEST_CODE);

            return true;
        }

        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CameraLauncher.REQUEST_CODE) {
            switch (resultCode)
            {
                case CameraLauncher.RESULT_ERROR:
                    this.callbackContext.error(
                        generateError(
                            CameraLauncher.RESULT_ERROR,
                            intent.getStringExtra("errorMessage")
                        )
                    );
                    break;
                case CameraLauncher.RESULT_BACK:
                    this.callbackContext.error(
                        generateError(
                            CameraLauncher.RESULT_BACK,
                            "Error because back camera."
                        )
                    );
                    break;
                case CameraLauncher.RESULT_SUCCESS:
                    try {
                        byte[] output = Base64.encode(TransferBigData.getImgTaken(), Base64.NO_WRAP);
                        String js_out = new String(output);
                        
                        this.callbackContext.success(js_out);
                    } catch (Exception e) {
                        this.callbackContext.error("Error to get content file.");
                    }    
                    break;
                default:
                    this.callbackContext.error("Camera has crashed.");
            }
        }
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
 
        long length = file.length();
        byte[] bytes = new byte[(int)length];
        
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
 
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
 
        is.close();
        return bytes;
    }

    protected JSONObject generateError(int code, String message) {
        JSONObject resultForPlugin = new JSONObject();
        try {
            resultForPlugin.put("code", code);
            resultForPlugin.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultForPlugin;
    }
}
