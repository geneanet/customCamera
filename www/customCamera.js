"use strict";

// Get cordova plugin.
var exec = require("cordova/exec");

// constructor.
var customCameraExport = function() {
};

/**
 * Start custom camera.
 *
 * @param {object}   options    Options to plugin.
 * @param {function} successFct Callback function to success action.
 * @param {function} failFct    Callback function to fail action.
 */
customCameraExport.prototype.startCamera = function(options, successFct, failFct) {
    var defaultOptions = {
        imgBackgroundBase64: null, // background picture in base64.
        miniature: true // active or disable the miniature function.
    };

    for (var nameOption in defaultOptions) {
        if (options[nameOption] === undefined) {
            options[nameOption] = defaultOptions[nameOption];
        }
    }

    var successFctCallback = function(data) {
        successFct(data);
    };

    var failFctCallback = function(data) {
        failFct(data.code, data.message);
    };

    exec(
        successFctCallback,
        failFctCallback,
        "CustomCamera",
        "startCamera",
        [
            options.imgBackgroundBase64,
            options.miniature
        ]
    );
};

module.exports = new customCameraExport();