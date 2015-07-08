#import "CameraParameter.h"

@implementation CameraParameter
{
    
}

@synthesize bgImageData;
@synthesize bgImageData1;
@synthesize bMiniature;
@synthesize bSaveInGallery;
@synthesize nCameraFlashMode;
@synthesize strCameraBGColor;
@synthesize strCameraPressedBG;
@synthesize fQuality;
@synthesize bOpacity;
@synthesize nDefaultFlash;
@synthesize bSwitchFlash;
@synthesize nDefaultCamera;
@synthesize bSwitchCamera;

-(id) initWithCommand :(CDVInvokedUrlCommand *)command
{
    if(self = [super init])
    {
        
//    imgBackgroundBase64: null, // background picture in base64.
//    imgBackgroundBase64OtherOrientation: null, // background picture in base64 for second orientation. If it's not defined, imgBackgroundBase64 is used.
//    miniature: true, // active or disable the miniature function.
//    saveInGallery: false, // save or not the picture in gallery.
//    cameraBackgroundColor: "#e26760", // color of the camera button.
//    cameraBackgroundColorPressed: "#dc453d", // color of the pressed camera button.
//        // To get supported color formats, go to see method parseColor : http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String)
//    quality: 100, // picture's quality : range 0 - 100 : http://developer.android.com/reference/android/graphics/Bitmap.html#compress(android.graphics.Bitmap.CompressFormat, int, java.io.OutputStream) (parameter "quality")
//    opacity: true, // active or disable the opacity function.
//    defaultFlash: this.FlashModes.DISABLE, // default state for flash. See CustomCamera.FlashModes for corrects values.
//    switchFlash: true, // active or disable the switch flash button.
//    defaultCamera: this.CameraFacings.BACK, // default camera used. See CustomCamera.CameraFacings for corrects values.
//    switchCamera: true // active or disable the switch camera button.

        
        NSString *strData = [command argumentAtIndex:0];
        if(strData)
        {
            bgImageData = [[NSData alloc] initWithBase64Encoding:strData];
        }
        else{
            bgImageData = nil;
        }
        
        
        NSString *strData1 = [command argumentAtIndex:1];
        if(strData1)
        {
            bgImageData1 = [[NSData alloc] initWithBase64Encoding:strData1];
        }
        else{
            bgImageData1 = nil;
        }
        
        
        bMiniature = [[command argumentAtIndex:2] boolValue];
        bSaveInGallery = [[command argumentAtIndex:3] boolValue];
        strCameraBGColor = [command argumentAtIndex:4];
        strCameraPressedBG = [command argumentAtIndex:5];
                              
        fQuality = [[command argumentAtIndex:6] intValue];
        bOpacity = [[command argumentAtIndex:7] boolValue];
        nDefaultFlash = [[command argumentAtIndex:8] intValue];
        bSwitchFlash = [[command argumentAtIndex:9] boolValue];
        nDefaultCamera = [[command argumentAtIndex:10] intValue];
        bSwitchCamera = [[command argumentAtIndex:11] boolValue];
    }
    return self;
}

@end
