'use strict';
/**
 * Permet de faire l'interface entre phonegap et l'application customcamera en java.
 */


var exec = require('cordova/exec');

// constructor.
var customCameraExport = function() {
};

customCameraExport.prototype.startCamera = function(imgBackgroundBase64, successFct, failFct) {
    exec(
        successFct,
        failFct,
        "CustomCamera",
        "startCamera",
        [imgBackgroundBase64]
    );
};

module.exports = new customCameraExport();