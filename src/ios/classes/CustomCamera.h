#import <Cordova/CDV.h>

@interface CustomCamera : CDVPlugin<UIImagePickerControllerDelegate, UINavigationControllerDelegate>
{
    CDVInvokedUrlCommand *lastCommand;
    
    int nSourceType;
    int nDestType;

    NSData *bgImageData;
    NSData *bgImageData1;
    BOOL miniature;
    BOOL saveInGallery;
    int nCameraFlashMode;

    NSString* clrCameraBG;
    NSString* clrCameraPressedBG;
    CGFloat quality;
    BOOL opacity;

    int defaultFlash;
    BOOL switchFlash;

    int defaultCamera;
    BOOL switchCamera;

    NSString *filename;
}
- (void)startCamera:(CDVInvokedUrlCommand*)command;

@end
