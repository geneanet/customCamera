#import "CustomCamera.h"
#import "AVCamViewController.h"

@implementation CustomCamera

- (void)startCamera:(CDVInvokedUrlCommand*)command {
    lastCommand = command;
    
    NSString *guid = [[NSUUID new] UUIDString];
    NSString *uniqueFileName = [NSString stringWithFormat:@"%@.jpg", guid];

    filename = uniqueFileName; //[command argumentAtIndex:0];
    quality = [[command argumentAtIndex:1] floatValue];
    targetWidth = [[command argumentAtIndex:2] floatValue];
    targetHeight = [[command argumentAtIndex:3] floatValue];
    nDestType = [[command argumentAtIndex:4] intValue];
    nSourceType = [[command argumentAtIndex:5] intValue];
    
    strPhotoName = [command argumentAtIndex:0];
    
    if(nSourceType == 0)
    {
        UIImagePickerController *imagePickerController = [[UIImagePickerController alloc] init];
        imagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        imagePickerController.delegate = self;
        [self.viewController presentViewController:imagePickerController animated:YES completion:nil];
    } else {
        if (![UIImagePickerController isCameraDeviceAvailable:UIImagePickerControllerCameraDeviceRear]) {
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No rear camera detected"];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        } else if (![UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Camera is not accessible"];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        } else {
            AVCamViewController *cameraViewController = [[AVCamViewController alloc] initWithPhoto:strPhotoName WithCallback:^(UIImage *image)   {
                @autoreleasepool {
                if(nDestType == 0)
                {
                        UIImage *scaledImage = [self scaleImage:image toSize:CGSizeMake(targetWidth, targetHeight)];
                        NSData *scaledImageData = UIImageJPEGRepresentation(scaledImage, quality / 100);
                        NSString* strEncodeData = [scaledImageData base64EncodedStringWithOptions:0];
                        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                                    messageAsString:strEncodeData];
                        [self.viewController dismissViewControllerAnimated:YES completion:nil];
                        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                } else {
                    NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
                    NSString *imagePath = [documentsDirectory stringByAppendingPathComponent:filename];
                    UIImage *scaledImage = [self scaleImage:image toSize:CGSizeMake(targetWidth, targetHeight)];
                    NSData *scaledImageData = UIImageJPEGRepresentation(scaledImage, quality / 100);
                    //[self deleteFileWithName:imagePath];
                    [scaledImageData writeToFile:imagePath atomically:YES];
                    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                                messageAsString:[[NSURL fileURLWithPath:imagePath] absoluteString]];
                    [self.viewController dismissViewControllerAnimated:YES completion:nil];
                    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                }
                }
            } ];
            [self.viewController presentViewController:cameraViewController animated:YES completion:nil];
        }
        
    }
    
}

// This method is called when an image has been chosen from the library or taken from the camera.
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    //You can retrieve the actual UIImage
    UIImage *image = [info valueForKey:UIImagePickerControllerOriginalImage];
    //Or you can get the image url from AssetsLibrary
//    NSURL *path = [info valueForKey:UIImagePickerControllerReferenceURL];
    
    [picker dismissViewControllerAnimated:YES completion:^{
        @autoreleasepool {
        if(nDestType == 0)
        {
            UIImage *scaledImage = [self scaleImage:image toSize:CGSizeMake(targetWidth, targetHeight)];
            NSData *scaledImageData = UIImageJPEGRepresentation(scaledImage, quality / 100);
            
            NSString* strEncodeData = [scaledImageData base64EncodedStringWithOptions:0];
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                        messageAsString:strEncodeData];
            [self.commandDelegate sendPluginResult:result callbackId:lastCommand.callbackId];
            
        } else {
            NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
            NSString *imagePath = [documentsDirectory stringByAppendingPathComponent:filename];
            UIImage *scaledImage = [self scaleImage:image toSize:CGSizeMake(targetWidth, targetHeight)];
            NSData *scaledImageData = UIImageJPEGRepresentation(scaledImage, quality / 100);
            //[self deleteFileWithName:imagePath];
            [scaledImageData writeToFile:imagePath atomically:YES];
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                        messageAsString:[[NSURL fileURLWithPath:imagePath] absoluteString]];
            [self.commandDelegate sendPluginResult:result callbackId:lastCommand.callbackId];
        }
        }
    }];
}

- (void)deleteFileWithName:(NSString *)fileName
{
    NSFileManager *manager = [NSFileManager defaultManager];
    // Need to check if the to be deleted file exists.
    if ([manager fileExistsAtPath:fileName]) {
        NSError *error = nil;
        // This function also returnsYES if the item was removed successfully or if path was nil.
        // Returns NO if an error occurred.
        [manager removeItemAtPath:fileName error:&error];
        if (error) {
            NSLog(@"There is an Error: %@", error);
        }
    } else {
        NSLog(@"File %@ doesn't exists", fileName);
    }
}

- (UIImage*)scaleImage:(UIImage*)image toSize:(CGSize)targetSize {
    if (targetSize.width <= 0 && targetSize.height <= 0) {
        return image;
    }
    
    CGFloat aspectRatio = image.size.height / image.size.width;
    CGSize scaledSize;
    if (targetSize.width > 0 && targetSize.height <= 0) {
        scaledSize = CGSizeMake(targetSize.width, targetSize.width * aspectRatio);
    } else if (targetSize.width <= 0 && targetSize.height > 0) {
        scaledSize = CGSizeMake(targetSize.height / aspectRatio, targetSize.height);
    } else {
        scaledSize = CGSizeMake(targetSize.width, targetSize.height);
    }
    
    UIGraphicsBeginImageContext(scaledSize);
    [image drawInRect:CGRectMake(0, 0, scaledSize.width, scaledSize.height)];
    UIImage *scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return scaledImage;
}

@end