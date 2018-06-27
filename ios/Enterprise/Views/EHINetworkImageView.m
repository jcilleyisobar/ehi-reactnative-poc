//
//  EHINetworkImageView.m
//  EHIius
//
//  Created by Ty Cobb on 8/13/14.
//  Copyright (c) 2014 Isobar. All rights reserved.
//

#import <AFNetworking/AFNetworking.h>
#import <AFNetworking/UIImageView+AFNetworking.h>
#import "EHINetworkImageView.h"
#import "EHIServices.h"

@interface EHINetworkImageView ()
@property (strong, nonatomic) id identifyingModel;
@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *widthConstraint;
#if NOT_RELEASE
@property (assign, nonatomic) BOOL isDebugImage;
#endif
@end

@implementation EHINetworkImageView

- (void)prepareForReuse
{
    self.imageModel = nil;
    self.identifyingModel = nil;
    
    if(self.image) {
        self.image = nil;
    }
    
    [self cancelImageDownloadTask];
}

- (void)updateConstraints
{
    [super updateConstraints];
    
    // seems like this shouldn't be necessary, but I can't get contentMode alone to do the job
    if(self.contentMode == UIViewContentModeScaleAspectFit && self.image.size.height != 0) {
        self.widthConstraint.constant = self.image.size.width / self.image.size.height * self.bounds.size.height;
    }
}

#if NOT_RELEASE
- (void)setBounds:(CGRect)bounds
{
    CGRect previousBounds = self.bounds;
    
    [super setBounds:bounds];
    
    if(!CGRectEqualToRect(previousBounds, self.bounds) && self.isDebugImage) {
        [self fetchImageForPath:nil];
    }
}
#endif

# pragma mark - Image fetching

- (void)setPath:(NSString *)path
{
    [self fetchImageForPath:path];
}

- (void)setImageModel:(EHIImage *)imageModel
{
    [self fetchImage:imageModel];
}

- (void)fetchImage:(EHIImage *)imageModel
{
    [self fetchImage:imageModel handler:nil];
}

- (void)fetchImage:(EHIImage *)imageModel handler:(void (^)(UIImage *))handler
{
    _imageModel = imageModel;
  
    // we need to make sure our bounds are correct before fetching the image
    [self layoutIfNeeded];
    
    NSString *path = [imageModel finalPathForWidth:self.bounds.size.width quality:EHIImageQualityHigh];
    [self fetchImageForPath:path handler:handler];
}

- (void)fetchImageForPath:(NSString *)path
{
    [self fetchImageForPath:path handler:nil];
}

- (void)fetchImageForPath:(NSString *)path handler:(void(^)(UIImage *image))handler
{    
    _path = path;
    
    // we don't want to accidentally fetch the image multiple times, because the completion blocks get batched
    id identifyingModel   = @(CACurrentMediaTime());
    self.identifyingModel = identifyingModel;
    
    if(path) {
        // cancel any outstanding request
        [self cancelImageDownloadTask];
        
        __weak typeof(self) weakSelf = self;

        NSURLRequest *imageRequest = [EHIServices URLRequestForPath:path];
        
        // and kick if off
        [self setImageWithURLRequest:imageRequest placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            if(weakSelf.identifyingModel != identifyingModel) {
                return;
            }
            
            if([weakSelf shouldApplyTransitionForResponse:response]) {
                [weakSelf.layer addAnimation:[weakSelf transitionInstance] forKey:nil];
            }
            
            weakSelf.image = image;
            ehi_call(handler)(image);
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            if(error) {
                EHIDomainError(EHILogDomainNetwork, @"image: %@ error: %@", path.lastPathComponent, error.localizedDescription);
            }
        }];
    }
}

// apply transition if this wasn't a cache hit, or we're set to always transition
- (BOOL)shouldApplyTransitionForResponse:(NSHTTPURLResponse *)response
{
    return (self.transition == EHIImageTransitionFadeAlways)
        || (self.transition == EHIImageTransitionFadeNetwork && response);
}

- (CATransition *)transitionInstance
{
    CATransition *backgroundTransition = [CATransition animation];
    backgroundTransition.duration = 0.15f;
    backgroundTransition.type     = kCATransitionFade;
    backgroundTransition.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
    return backgroundTransition;
}

@end
