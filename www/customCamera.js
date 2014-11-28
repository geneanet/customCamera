"use strict";

// Get cordova plugin.
var exec = require("cordova/exec");

// constructor.
var customCameraExport = function() {
};

/**
 * Start custom camera.
 * 
 * @param {string}   imgBackgroundBase64 Base64 picture for the background.
 * @param {function} successFct          Callback function to success action.
 * @param {function} failFct             Callback function to fail action.
 */
customCameraExport.prototype.startCamera = function(imgBackgroundBase64, successFct, failFct) {
    var successFctCallback = function(data) {
        successFct(data);
    };

    var failFctCallback = function(message) {
        failFct(message);
    };

    exec(
        successFctCallback,
        failFctCallback,
        "CustomCamera",
        "startCamera",
        [imgBackgroundBase64]
    );
};

module.exports = new customCameraExport();