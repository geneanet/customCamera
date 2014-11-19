package org.geneanet.customcamera;

import XXX_NAME_CURRENT_PACKAGE_XXX.CameraView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;

public class CameraLauncher extends CordovaPlugin {
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startCamera")) {
            Intent intent = new Intent(this.cordova.getActivity(), CameraView.class);

            Bundle imgBackgroundBase64 = new Bundle();
            imgBackgroundBase64.putString("imgBackgroundBase64", args.getString(0));
            intent.putExtras(imgBackgroundBase64);

            cordova.getActivity().startActivity(intent);

            PluginResult r = new PluginResult(PluginResult.Status.OK, "base64retour");
            callbackContext.sendPluginResult(r);

            return true;
        }

        return false;
    }
}
