package org.geneanet.customcamera;

import XXX_NAME_CURRENT_PACKAGE_XXX.CameraView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;

public class CameraLauncher extends CordovaPlugin {
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Intent intent = new Intent(this.cordova.getActivity(), CameraView.class);
        cordova.getActivity().startActivity(intent);

        return true;
    }
}
