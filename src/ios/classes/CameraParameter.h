#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>

@interface CameraParameter : NSObject
{

}

@property(nonatomic, retain) NSData *bgImageData;
@property(nonatomic, retain) NSData *bgImageData1;
@property(nonatomic, assign) BOOL bMiniature;
@property(nonatomic, assign) BOOL bSaveInGallery;
@property(nonatomic, assign) int nCameraFlashMode;


@property(nonatomic, retain) NSString* strCameraBGColor;
@property(nonatomic, retain) NSString* strCameraPressedBG;
@property(nonatomic, assign) CGFloat fQuality;
@property(nonatomic, assign) BOOL bOpacity;

@property(nonatomic, assign) int nDefaultFlash;
@property(nonatomic, assign) BOOL bSwitchFlash;

@property(nonatomic, assign) int nDefaultCamera;
@property(nonatomic, assign) BOOL bSwitchCamera;

-(id) initWithCommand :(CDVInvokedUrlCommand *)command;

@end
