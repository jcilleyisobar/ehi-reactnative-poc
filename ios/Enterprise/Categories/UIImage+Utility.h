//
//  UIImage+Utility.h
//  Enterprise
//
//  Created by Alex Koller on 1/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

@interface UIImage (Utility)

/** The appropriately sized launch image for the app */
+ (UIImage *)launchImage;

- (void)saveImageToPhotoLibraryWithHandler:(void (^)(BOOL success, NSError *error))handler;

@end
