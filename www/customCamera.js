'use strict';
/**
 * Permet de faire l'interface entre phonegap et l'application customcamera en java.
 */


var exec = require('cordova/exec');

// constructor.
var customCameraExport = function() {
};

customCameraExport.prototype.startCamera = function(imgBackgroundBase64) {
    exec(
        function(result) {
            console.log("success");
            console.log(result);
        },
        function(result) {
            console.log("fail");
            console.log(result);
        },
        "CustomCamera",
        "startCamera",
        [imgBackgroundBase64]
    );
};

module.exports = new customCameraExport();