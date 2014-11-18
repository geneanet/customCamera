package org.geneanet.customcamera;

import XXX_NAME_CURRENT_PACKAGE_XXX.CameraView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;

public class CameraLauncher extends CordovaPlugin {
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Intent intent = new Intent(this.cordova.getActivity(), CameraView.class);

        Bundle imgBase64 = new Bundle();
        imgBase64.putString("imgBase64", "mon base 64");
        intent.putExtras(imgBase64);

        cordova.getActivity().startActivity(intent);

        return true;
    }
}
