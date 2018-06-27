//
//  EHINetworkImageView.h
//  EHIius
//
//  Created by Ty Cobb on 8/13/14.
//  Copyright (c) 2014 Isobar. All rights reserved.
//

#import "EHIImage.h"

typedef NS_ENUM(NSInteger, EHIImageTransition) {
    EHIImageTransitionFadeNetwork,
    EHIImageTransitionFadeAlways,
    EHIImageTransitionNone,
};

@interface EHINetworkImageView : UIImageView

@property (copy  , nonatomic) NSString *path;
@property (strong, nonatomic) EHIImage *imageModel;
@property (assign, nonatomic) EHIImageTransition transition;

- (void)fetchImage:(EHIImage *)thumbnail;
- (void)fetchImage:(EHIImage *)thumbnail handler:(void(^)(UIImage *image))handler;

- (void)fetchImageForPath:(NSString *)path;
- (void)fetchImageForPath:(NSString *)path handler:(void(^)(UIImage *image))handler;

- (void)prepareForReuse;

@end
