#import <UIKit/UIKit.h>
#import "CameraParameter.h"

@interface AVCamViewController : UIViewController
{
    NSData *_imageData;
    void (^_callback)(UIImage *, NSString *, NSString *);

    UIPinchGestureRecognizer *twoFingerPinch;

    CGRect frameBtnThumb;

    UIImage *capturedImage;
    NSData *capturedImageData;

    BOOL isRotated;

    CGFloat fDist;
}

@property (weak, nonatomic) IBOutlet UIView *saveBgPanel;
@property (weak, nonatomic) IBOutlet UIView *topBgPanel;
@property (weak, nonatomic) IBOutlet UIImageView *capturedImageView;

@property (weak, nonatomic) IBOutlet UIButton *btnBigDeletePicture;
@property (weak, nonatomic) IBOutlet UIButton *btnDeletePicture;
@property (weak, nonatomic) IBOutlet UIButton *btnSaveImage;
@property (weak, nonatomic) IBOutlet UIButton *btnBigSaveImage;
@property (weak, nonatomic) IBOutlet UISlider *opacitySlider;

@property (nonatomic, retain) CameraParameter *params;

- (id)initWithParams:(CameraParameter *)parameter WithCallback:(void (^)(UIImage *, NSString *, NSString *))callback;

@end
