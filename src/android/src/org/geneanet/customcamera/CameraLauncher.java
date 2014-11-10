package org.geneanet.customcamera;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;

public class CameraLauncher extends CordovaPlugin {
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		Intent intent = new Intent("org.geneanet.customcamera.CameraView");
		cordova.startActivityForResult((CordovaPlugin) this, intent, 1111111);
		
        return true;
    }
}
