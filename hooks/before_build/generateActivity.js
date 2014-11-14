#!/usr/bin/env node

// Define required.
var fs = require("fs");
var xml2js = require('xml2js');
var parseString = xml2js.parseString;
var builder = new xml2js.Builder();

// Define differents paths.
var pathConfigXml = "config.xml";
var pathAndroidCordova = "platforms/android/";
var pathResAndroidCordova = "platforms/android/res/";
var pathAndroidPlugin = __dirname+"/../../src/android/";
var pathResPlugin = __dirname+"/../../res/";

/**
 * Generate a path based on a package name.
 * 
 * @param {string} packageName Package name, eg: com.example.
 * 
 * @return {string} Return path generate.
 */
var generatePathFrompackageName = function(packageName) {
    return packageName.split(".").join("/");
}

/**
 * Create different java classes.
 * 
 * @param {string} packageName Package name of the current application.
 */
var createClasses = function(packageName) {
    var pathCameraView = pathAndroidPlugin+"customCamera/src/org/geneanet/customcamera/CameraView.java";
    var pathCameraLauncher = pathAndroidPlugin+"CameraLauncher.java";

    if (fs.existsSync(pathCameraView) && fs.existsSync(pathCameraLauncher)) {
        // Rewrite package of CameraView class.
        var contentCameraView = fs.readFileSync(pathCameraView, {encoding: "utf8"});
        contentCameraView = contentCameraView.replace(/^package\s[^;]*/,"package "+packageName);
        fs.writeFileSync(pathAndroidCordova+"src/"+generatePathFrompackageName(packageName)+"/CameraView.java", contentCameraView);

        // Rewrite import of CameraLauncher class.
        var contentCameraLauncher = fs.readFileSync(pathCameraLauncher, {encoding: "utf8"});
        contentCameraLauncher = contentCameraLauncher.replace("XXX_NAME_CURRENT_PACKAGE_XXX", packageName);
        fs.writeFileSync(
            pathAndroidCordova+"src/"+generatePathFrompackageName(contentCameraLauncher.match(/package\s([^;]*)/)[1])+"/CameraLauncher.java",
            contentCameraLauncher
        );
    } else {
        console.error("File CameraView.java or/and CameraLauncher.java not found.");
        process.exit(1);
    }
}

/**
 * Update AndroidManifest.xml of the current application.
 * 
 * @param {string} packageName Package name of the current application.
 */
var updateAndroidManifest = function(packageName) {
    var pathAndroidManifestCordova = pathAndroidCordova+"AndroidManifest.xml";
    if (fs.existsSync(pathAndroidManifestCordova)) {
        // get content AndroidManifest.
        var contentAndroidManifest = fs.readFileSync(pathAndroidManifestCordova, {encoding: "utf8"});
        parseString(contentAndroidManifest, function (err, result) {
            contentAndroidManifest = result;
        });

        // add activity if needed.
        var needAddActivity = true;
        var currentActivities = contentAndroidManifest["manifest"]["application"][0]["activity"];
        for (var i = currentActivities.length - 1; i >= 0; i--) {
            if (currentActivities[i]["$"]["android:name"] == "CameraView") {
                needAddActivity = false;
            }
        };
        if (needAddActivity) {
            contentAndroidManifest["manifest"]["application"][0]["activity"].push({
                $: {
                    "android:name": "CameraView",
                    "android:label": "CameraView",
                }
            })
            var newXmlAndroidManifest = builder.buildObject(contentAndroidManifest);
            fs.writeFileSync(
                pathAndroidManifestCordova,
                newXmlAndroidManifest
            );
        }
    } else {
        console.error("File AndroidManifest.xml for cordova not found.");
        process.exit(1);
    }
}

/**
 * Update differents config file (translate, res/layout, etc).
 */
var updateConfig = function() {
    var pathLayoutCordova = pathResAndroidCordova+"layout/";
    var pathLayoutPlugin = pathAndroidPlugin+"customCamera/res/layout/";
    var pathLayoutCameraView = pathLayoutPlugin+"activity_camera_view.xml";

    // create directory layout in cordova if it doesn't exist.
    if (!fs.existsSync(pathLayoutCordova)) {
        fs.mkdirSync(pathLayoutCordova);
    }

    // "copy" layout for camera.
    if (fs.existsSync(pathLayoutCameraView)) {
        var layoutCameraViewContent = fs.readFileSync(pathLayoutCameraView, {encoding: "utf8"});

        fs.writeFileSync(pathLayoutCordova+"activity_camera_view.xml", layoutCameraViewContent);
    } else {
        console.error("File activity_camera_view.xml in plugin not found.");
        process.exit(1);
    }

    // add translations.
    var pathTranslations = pathResPlugin+"translations.json";
    if (fs.existsSync(pathTranslations)) {
        // get translations.
        var translationsForApplications = fs.readFileSync(pathTranslations, {encoding: "utf8"});
        translationsForApplications = JSON.parse(translationsForApplications);
        for (lang in translationsForApplications) {
            var pathFileTranslate = pathResAndroidCordova+"values-"+lang+"/";

            var objToXml;
            // already exist, get data.
            if (fs.existsSync(pathFileTranslate+"strings.xml")) {
                var objToXml = fs.readFileSync(pathFileTranslate+"strings.xml", {encoding: "utf8"});
                parseString(objToXml, function (err, result) {
                    objToXml = result;
                });
            } else {
                // generate minimal object.
                objToXml = {
                    resources: {
                        string: []
                    }
                };
            }

            // add message.
            for (tag in translationsForApplications[lang]) {
                objToXml["resources"]["string"].push({
                    _: translationsForApplications[lang][tag],
                    $: {
                        name: tag
                    }
                });
            }

            var xmlBuild = builder.buildObject(objToXml);
            if (!fs.existsSync(pathFileTranslate)) {
                fs.mkdirSync(pathFileTranslate);
            }
            fs.writeFileSync(pathFileTranslate+"strings.xml", xmlBuild);
        }
    } else {
        console.error("File translations.json in plugin not found.");
        process.exit(1);
    }
}

// Check if files required exist.
if (fs.existsSync(pathConfigXml)) {
    // Get the name package of the current application.
    var configContent = fs.readFileSync(pathConfigXml, {encoding: "utf8"});
    parseString(configContent, function (err, result) {
        configContent = result;
    });
    var packageName = configContent["widget"]["$"]["id"];

    createClasses(packageName);
    updateAndroidManifest(packageName);
    updateConfig();
} else {
    console.error("File config.xml for cordova not found.");
    process.exit(1);
}