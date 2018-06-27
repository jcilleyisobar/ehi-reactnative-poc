//
//  UIImage+Utility.m
//  Enterprise
//
//  Created by Alex Koller on 1/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "UIImage+Utility.h"
@import PhotosUI;

@implementation UIImage (Utility)

+ (UIImage *)launchImage
{
    NSString *path = [[NSBundle mainBundle] pathForResource:[self launchImageName] ofType:@"png"];
    return [UIImage imageWithContentsOfFile:path];
}

+ (NSString *)launchImageName
{
    switch((NSInteger)[UIScreen mainScreen].bounds.size.height) {
        case 480:
            return @"LaunchImage-700@2x";
        case 568:
            return @"LaunchImage-700-568h@2x";
        case 667:
            return @"LaunchImage-800-667h@2x";
         case 812:
            return @"LaunchImage-1100-2436h@3x";
        default:
            return @"LaunchImage-800-Portrait-736h@3x";
    }
}

- (void)saveImageToPhotoLibraryWithHandler:(void (^)(BOOL success, NSError *error))handler
{
    [self checkForPermissionWithHandler:^(PHAuthorizationStatus status) {
        if (status == PHAuthorizationStatusAuthorized) {
            [[PHPhotoLibrary sharedPhotoLibrary] performChanges:^{
                [PHAssetChangeRequest creationRequestForAssetFromImage:self];
            } completionHandler:handler];
        }
        else
        {
            //TODO: message that access is denied
        }
    }];
}

- (void)checkForPermissionWithHandler:(void (^)(PHAuthorizationStatus status))handler
{
    if ([PHPhotoLibrary authorizationStatus] == PHAuthorizationStatusAuthorized) {
        ehi_call(handler)(PHAuthorizationStatusAuthorized);
    }
    else {
        [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {
            ehi_call(handler)(status);
        }];
    }
}

@end
