"use strict";

(function(require, module) {
    // Get cordova plugin.
    var exec = require("cordova/exec");

    var cameraIsStarted = false;

    // constructor.
    function CustomCameraExport() {}

    CustomCameraExport.prototype.FlashModes = {DISABLE: 0, ACTIVE: 1, AUTO: 2};
    CustomCameraExport.prototype.CameraFacings = {BACK: 0, FRONT: 1};

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
            imgBackgroundBase64OtherOrientation: null, // background picture in base64 for second orientation. If it's not defined, imgBackgroundBase64 is used.
            miniature: true, // active or disable the miniature function.
            saveInGallery: false, // save or not the picture in gallery.
            cameraBackgroundColor: "#e26760", // color of the camera button.
            cameraBackgroundColorPressed: "#dc453d", // color of the pressed camera button.
            // To get supported color formats, go to see method parseColor : http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String)
            quality: 100, // picture's quality : range 0 - 100 : http://developer.android.com/reference/android/graphics/Bitmap.html#compress(android.graphics.Bitmap.CompressFormat, int, java.io.OutputStream) (parameter "quality")
            opacity: true, // active or disable the opacity function.
            defaultFlash: this.FlashModes.DISABLE, // default state for flash. See CustomCamera.FlashModes for corrects values.
            switchFlash: true, // active or disable the switch flash button.
            defaultCamera: this.CameraFacings.BACK, // default camera used. See CustomCamera.CameraFacings for corrects values.
            switchCamera: true // active or disable the switch camera button.
        };

        for (var nameOption in defaultOptions) {
            if (options[nameOption] === undefined) {
                options[nameOption] = defaultOptions[nameOption];
            }
        }

        function successFctCallback(data) {
            cameraIsStarted = false;
            if (successFct instanceof Function) {
                successFct(data);
            }
        }

        function failFctCallback(data) {
            cameraIsStarted = false;
            if (failFct instanceof Function) {
                failFct(data.code, data.message);
            }
        }

        cameraIsStarted = true;
        exec(
            successFctCallback,
            failFctCallback,
            "CustomCamera",
            "startCamera",
            [
                options.imgBackgroundBase64,
                options.imgBackgroundBase64OtherOrientation,
                options.miniature,
                options.saveInGallery,
                options.cameraBackgroundColor,
                options.cameraBackgroundColorPressed,
                options.quality,
                options.opacity,
                options.defaultFlash,
                options.switchFlash,
                options.defaultCamera,
                options.switchCamera
            ]
        );
    };

    /**
     * Check if the camera is started or not.
     * 
     * @return {boolean} True: It's started, else false.
     */
    CustomCameraExport.prototype.cameraIsStarted = function() {
        return cameraIsStarted;
    };

    module.exports = new CustomCameraExport();
})(require, module);