package org.geneanet.customcamera;

import XXX_NAME_CURRENT_PACKAGE_XXX.CameraView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;

public class CameraLauncher extends CordovaPlugin {

    protected CallbackContext callbackContext;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startCamera")) {
            this.callbackContext = callbackContext;

            Intent intent = new Intent(this.cordova.getActivity(), CameraView.class);

            Bundle imgBackgroundBase64 = new Bundle();
            imgBackgroundBase64.putString("imgBackgroundBase64", args.getString(0));
            intent.putExtras(imgBackgroundBase64);

            cordova.startActivityForResult((CordovaPlugin) this, intent, 123456789);

            return true;
        }

        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 123456789 && resultCode == 1) {
            String pathPicture = intent.getStringExtra("pathPicture");
            // Log.d("customCamera", pathPicture);
            try {
                File fl = new File(pathPicture);
                byte[] ret = loadFile(fl);

                byte[] output = Base64.encode(ret, Base64.NO_WRAP);
                String js_out = new String(output);
                
                this.callbackContext.success(js_out);
            } catch (Exception e) {
                this.callbackContext.error("Error to get content file.");
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
}
