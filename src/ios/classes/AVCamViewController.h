#import <UIKit/UIKit.h>

@interface AVCamViewController : UIViewController
{
        NSData *_imageData;
        void(^_callback)(UIImage*);
    
        UIPinchGestureRecognizer *twoFingerPinch;
    
        CGRect frameBtnThumb;

    UIImage *capturedImage;
    NSData *capturedImageData;
    
}

@property (weak, nonatomic) IBOutlet UIView *saveBgPanel;
@property (weak, nonatomic) IBOutlet UIView *topBgPanel;
@property (weak, nonatomic) IBOutlet UIImageView *capturedImageView;

@property (weak, nonatomic) IBOutlet UIButton *btnDeletePicture;
@property (weak, nonatomic) IBOutlet UIButton *btnSaveImage;
@property (weak, nonatomic) IBOutlet UISlider *opacitySlider;




- (id) initWithPhoto:(NSString *)sttPhoto WithCallback:(void(^)(UIImage*))callback;




@end
