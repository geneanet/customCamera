# org.geneanet.customcamera

This cordova plugin is an alternative to the official cordova plugin (camera). It starts a custom camera: image overlay with an opacity slider, user-defined color of the buttons, activating/deactivating functions.


This plugin defines a global variable `navigator.GeneanetCustomCamera`.

## Installation

    cordova plugin add https://github.com/geneanet/customCamera.git
    cordova build `platform`

## Supported Platforms

+ Android

## Utilisation

### Command

``` js
navigator.GeneanetCustomCamera.startCamera(options, onSuccess, onFail);
```

### Parameters

#### *{Object}* options

An `options` object containing the parameters of the camera.

+ **imgBackgroundBase64 :** Overlay image. Should be in base64 format.
    - **Type :** `string`
    - **Default :** `null`

+ **imgBackgroundBase64OtherOrientation :** Alternate overlay image (if device's orientation has changed since app started). Should be in base64 format. If `null`, use `imgBackgroundBase64` and resize image.
    - **Type :** `string`
    - **Default :** `null`

+ **miniature :** Activate/deactivate the thumbnail option. `true` : Activate option. `false` : Deactivate option.
    - **Type :** `boolean`
    - **Default :** `true`

+ **saveInGallery :** Save picture to the camera gallery. `true` : Activate option. `false` : Deactivate option.
    - **Type :** `boolean`
    - **Default :** `false`

+ **cameraBackgroundColor :** Color of the camera button.
    - **Type :** `string`
    - **Default :** `"#e26760"`
    - **Notes :**
        + An incorrect value or a `null` value means a transparency effect.
        + See the [`parseColor()`](http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String)) method for the format of colors.

+ **cameraBackgroundColorPressed :** Color of the camera button when it is pressed.
    - **Type :** `string`
    - **Default :** `"#dc453d"`
    - **Notes :**
        + An incorrect value or a `null` value means a transparency effect.
        + See the [`parseColor()`](http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String)) method for the format of colors.

+ **quality :** Quality of the picture.
    - **Type :** `integer`
    - **Default :** `100`
    - **Notes :**
        + A value between 0 and 100. An incorrect value means the default value.
        + See the [`compress()`](http://developer.android.com/reference/android/graphics/Bitmap.html) method for more informations.

+ **opacity :** Activate/deactivate the opacity option for the overlay image. `true` : Activate option. `false` : Deactivate option.
    - **Type :** `boolean`
    - **Default :** `true`

+ **defaultFlash :** Default mode flash to use. See `CustomCamera.FlashModes` for corrects values.
    - **Type :** `integer`
    - **Default :** `0`

+ **switchFlash :** Activate/deactivate the flash mode button. `true` : Activate button. `false` : Deactivate button.
    - **Type :** `boolean`
    - **Default :** `true`

+ **defaultCamera :** Default camera used. See `CustomCamera.CameraFacings` for corrects values.
    - **Type :** `integer`
    - **Default :** `0`

+ **switchCamera :** Activate/deactivate the button to switch camera. `true` : Activate option. `false` : Deactivate option.
    - **Type :** `boolean`
    - **Default :** `true`

#### *{Function}* onSuccess

`onSuccess` is called when the shooting has succeed.

+ **Parameters :**
    - **result :**
        + **Type :** `string`
        + **Note :** Contains the picture in base64 format.

#### *{Function}* onFail

`onFail`is  called when the shooting has failed.
+ **Parameters :**
    - **code :**
        + **Type :** `integer`
        + **Note :** Contains the error code.
            - **Code "2" :** Error while taking a picture.
            - **Code "3" :** Camera closed before takin a picture.
    - **message :**
        + **Type :** `string`
        + **Note :** A error message.

## Constants

+ **CustomCamera.FlashModes.DISABLE :**
    - **Type :** `integer`
    - **Value :** `0`
+ **CustomCamera.FlashModes.ACTIVE :**
    - **Type :** `integer`
    - **Value :** `1`
+ **CustomCamera.FlashModes.AUTO :**
    - **Type :** `integer`
    - **Value :** `2`

+ **CustomCamera.CameraFacings.BACK :**
    - **Type :** `integer`
    - **Value :** `0`
+ **CustomCamera.CameraFacings.FRONT :**
    - **Type :** `integer`
    - **Value :** `1`


## Implementation

### Example

``` js
var base64 = "...";
navigator.GeneanetCustomCamera.startCamera(
    {
        imgBackgroundBase64: base64,
        saveInGallery: true,
        miniature: false,
        quality: 70,
        cameraBackgroundColor: "#ffffff",
        cameraBackgroundColorPressed: null
    },
    function(result) {
        window.console.log("success");
        $("#imgTaken").attr("src", "data:image/jpeg;base64,"+result);
    },
    function(code, message) {
        window.console.log("fail");
        window.console.log(code);
        window.console.log(message);
    }
);
```

### Barcode

[See the code](https://github.com/geneanet/customCamera/tree/master/examples/barcode)

![Barcode](https://raw.githubusercontent.com/geneanet/customCamera/master/examples/barcode/screenshot.png)

### Grid

[See the code](https://github.com/geneanet/customCamera/tree/master/examples/grid)

![Grid](https://raw.githubusercontent.com/geneanet/customCamera/master/examples/grid/screenshot.png)

### AngularJS

An implementation in AngularJS has been made for ease of use : [$geneanetCustomCamera](https://github.com/geneanet/customCameraAngular.git).

## Contribute

To contribute to this project, please read the following :
+ **Bugs, suggestion, etc. :** Must be declared in Github. Please search the threads before starting a new topic.
+ **Développement Javascript :** Must compiles with JSHint coding rules.
