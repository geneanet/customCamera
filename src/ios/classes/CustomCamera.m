#import "CustomCamera.h"
#import "AVCamViewController.h"
#import "CameraParameter.h"

@implementation CustomCamera

- (void)startCamera:(CDVInvokedUrlCommand *)command {
    lastCommand = command;

    NSString *guid = [[NSUUID new] UUIDString];
    NSString *uniqueFileName = [NSString stringWithFormat:@"%@.jpg", guid];

    filename = uniqueFileName;
    nSourceType = 1;
    nDestType = 0;

    CameraParameter *param = [[CameraParameter alloc] initWithCommand:lastCommand];

    if (nSourceType == 0) {
        UIImagePickerController *imagePickerController = [[UIImagePickerController alloc] init];
        imagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        imagePickerController.delegate = self;
        [self.viewController presentViewController:imagePickerController animated:YES completion:nil];
    }
    else {
        if (![UIImagePickerController isCameraDeviceAvailable:UIImagePickerControllerCameraDeviceRear]) {
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No rear camera detected"];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }
        else if (![UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Camera is not accessible"];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }
        else {
            AVCamViewController *cameraViewController = [[AVCamViewController alloc] initWithParams:param WithCallback: ^(UIImage *image, NSString *errorCode, NSString *message) {
                @autoreleasepool {
                    if (image) {
                        if (nDestType == 0) {
                            NSData *imageData = UIImageJPEGRepresentation(image, quality / 100);

                            NSString *strEncodeData = [imageData base64EncodedStringWithOptions:0];
                            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                                        messageAsString:strEncodeData];
                            [self.viewController dismissViewControllerAnimated:YES completion:nil];
                            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                        }
                        else {
                            NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
                            NSString *imagePath = [documentsDirectory stringByAppendingPathComponent:filename];
                            NSData *imageData = UIImageJPEGRepresentation(image, quality / 100);
                            [imageData writeToFile:imagePath atomically:YES];
                            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                                        messageAsString:[[NSURL fileURLWithPath:imagePath] absoluteString]];
                            [self.viewController dismissViewControllerAnimated:YES completion:nil];
                            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                        }
                    }
                    else {
                        //error
                        NSDictionary *error = @{ @"code":errorCode, @"message":message };
                        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:error];
                        [self.viewController dismissViewControllerAnimated:YES completion:nil];
                        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                    }
                }
            }];
            [self.viewController presentViewController:cameraViewController animated:YES completion:nil];
        }
    }
}

// This method is called when an image has been chosen from the library or taken from the camera.
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    //You can retrieve the actual UIImage
    UIImage *image = [info valueForKey:UIImagePickerControllerOriginalImage];
    //Or you can get the image url from AssetsLibrary
    //    NSURL *path = [info valueForKey:UIImagePickerControllerReferenceURL];

    [picker dismissViewControllerAnimated:YES completion: ^{
        @autoreleasepool {
            if (nDestType == 0) {
                NSData *imageData = UIImageJPEGRepresentation(image, quality / 100);

                NSString *strEncodeData = [imageData base64EncodedStringWithOptions:0];
                CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                            messageAsString:strEncodeData];
                [self.commandDelegate sendPluginResult:result callbackId:lastCommand.callbackId];
            }
            else {
                NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
                NSString *imagePath = [documentsDirectory stringByAppendingPathComponent:filename];
                NSData *imageData = UIImageJPEGRepresentation(image, quality / 100);
                //[self deleteFileWithName:imagePath];
                [imageData writeToFile:imagePath atomically:YES];
                CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                            messageAsString:[[NSURL fileURLWithPath:imagePath] absoluteString]];
                [self.commandDelegate sendPluginResult:result callbackId:lastCommand.callbackId];
            }
        }
    }];
}

- (void)deleteFileWithName:(NSString *)fileName {
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
    }
    else {
        NSLog(@"File %@ doesn't exists", fileName);
    }
}

@end
