"use strict";

(function(require, module) {
    // Get cordova plugin.
    var exec = require("cordova/exec");

    // constructor.
    function CustomCameraExport() {}

    /**
     * Start custom camera.
     *
     * @param {object}   options    Options to plugin.
     * @param {function} successFct Callback function to success action.
     * @param {function} failFct    Callback function to fail action.
     */
    CustomCameraExport.prototype.startCamera = function(options, successFct, failFct) {
        var defaultOptions = {
            imgBackgroundBase64: null, // background picture in base64.
            miniature: true, // active or disable the miniature function.
            cameraBackgroundColor: "#e26760", // color of the camera button.
            cameraBackgroundColorPressed: "#dc453d" // color of the pressed camera button.
            // To get supported color formats, go to see method parseColor : http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String)
        };

        for (var nameOption in defaultOptions) {
            if (options[nameOption] === undefined) {
                options[nameOption] = defaultOptions[nameOption];
            }
        }

        function successFctCallback(data) {
            successFct(data);
        }

        function failFctCallback(data) {
            failFct(data.code, data.message);
        }

        exec(
            successFctCallback,
            failFctCallback,
            "CustomCamera",
            "startCamera",
            [
                options.imgBackgroundBase64,
                options.miniature,
                options.cameraBackgroundColor,
                options.cameraBackgroundColorPressed
            ]
        );
    };

    module.exports = new CustomCameraExport();
})(require, module);