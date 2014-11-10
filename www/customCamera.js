/**
 * Permet de faire l'interface entre phonegap et l'application customcamera en java.
 */

'use strict';

var exec = require('cordova/exec');

// constructor.
var customCameraExport = function() {
};

// add method.
customCameraExport.prototype.getPicture = function() {
    alert("Oh yeah !");
};

customCameraExport.prototype.startCamera = function() {
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
        "customCamera",
        []
    );
};

module.exports = new customCameraExport();