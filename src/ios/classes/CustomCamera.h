#import <Cordova/CDV.h>

@interface CustomCamera : CDVPlugin<UIImagePickerControllerDelegate, UINavigationControllerDelegate>
{
    CDVInvokedUrlCommand *lastCommand;
    
    NSString *filename;
    CGFloat quality;
    CGFloat targetWidth;
    CGFloat targetHeight;
    
    int nDestType;
    int nSourceType;
    NSString* strPhotoName;
    
}
- (void)startCamera:(CDVInvokedUrlCommand*)command;

@end
