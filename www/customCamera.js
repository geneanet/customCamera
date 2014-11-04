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

module.exports = new customCameraExport();