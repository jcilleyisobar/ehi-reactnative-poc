//
//  EHIReservationReviewLoadingIndicator.m
//  Enterprise
//
//  Created by fhu on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReviewBookingLoadingIndicator.h"
#import "EHICircleView.h"
#import "EHILabel.h"
#import "EHIRestorableConstraint.h"

#define EHIReservationBookingAnimationFrames 87
#define EHIReservationBookingAnimationGreenFrames 140
#define EHIReservationSuccessFrames 12

typedef NS_ENUM(NSInteger, EHIIndicatorType) {
    EHIReservationReviewIndicatorTypeLoading,
    EHIReservationReviewIndicatorTypeLoadingGreen,
    EHIReservationReviewIndicatorTypeSuccess
};

@interface EHIReviewBookingLoadingIndicator()
@property (assign, nonatomic) EHIIndicatorType type;
@property (strong, nonatomic) EHICircleView *expandingCircle;
@property (assign, nonatomic) BOOL successAnimationStarted;
@property (weak  , nonatomic) IBOutlet UIImageView *successIndicator;
@property (weak  , nonatomic) IBOutlet EHILabel *successLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *successLabelConstraint;
@property (weak  , nonatomic) IBOutlet UIImageView *loadingIndicator;
@end

@implementation EHIReviewBookingLoadingIndicator

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self setupView];
}

- (void)finishLoadingWithSuccess:(BOOL)didSucceed completion:(void(^)(BOOL completed))handler;
{
    if(self.successAnimationStarted) {
        return;
    }
    
    if(!didSucceed) {
        [self resetView];
        return;
    }
    
    self.successAnimationStarted = YES;
    self.loadingIndicator.hidden = YES;
    
    self.expandingCircle.hidden = NO;
    UIView.animate(YES).duration(0.4f).transform(^{
        CGFloat scale = [self calculateExpandingCircleScaleToFill];
        
        [self.expandingCircle setTransform:CGAffineTransformMakeScale(scale, scale)];
    }).start(nil);
    
    dispatch_after_seconds(0.15f, ^{
        self.successIndicator.hidden = NO;
        [self.successIndicator startAnimating];
        self.successLabel.hidden = NO;
    });
    
    [self.successLabelConstraint setOffset:25.0f];
    UIView.animate(YES).delay(0.23f).duration(0.13f).option(UIViewAnimationOptionCurveEaseIn).transform(^{
        [self layoutIfNeeded];
    }).start(nil);
    
    dispatch_after_seconds(1.5f, ^{
        UIView.animate(YES).duration(0.15f).transform(^{
            self.isAnimating = NO;
        }).start(^(BOOL finished) {
            [self resetView];
            ehi_call(handler)(YES);
        });
    });
}

//
// Helpers
//

- (CGFloat)calculateExpandingCircleScaleToFill
{
    // determine circle radius and required radius to completely fill view
    CGFloat circleRadius  = self.expandingCircle.bounds.size.width / 2;
    CGFloat minRadius     = sqrtf(powf(self.bounds.size.width, 2) + powf(self.bounds.size.height, 2)) / 2;
    
    // determine scaling required to appropriately fill screen
    CGFloat minScale      = minRadius / circleRadius;
    CGFloat bufferedScale = minScale * 1.2;
    
    return bufferedScale;
}

# pragma mark - Setup

- (void)resetView
{
    if(self.expandingCircle) {
        [self.expandingCircle removeFromSuperview];
    }
   
    self.successAnimationStarted = NO;
    self.successLabelConstraint.constant = self.successLabelConstraint.restorableValue;
   
    [self setupView];
}

- (void)setupView
{
    self.backgroundColor = self.type == EHIReservationReviewIndicatorTypeLoadingGreen ? [UIColor whiteColor] : [UIColor ehi_greenColor];
    
    self.expandingCircle = [EHICircleView new];
    self.expandingCircle.backgroundColor = [UIColor whiteColor];
    
    [self addSubview:self.expandingCircle];
    [self.expandingCircle mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.mas_centerX);
        make.centerY.equalTo(self.mas_centerY);
        make.height.equalTo(@40.0f);
        make.width.equalTo(@40.0f);
    }];
    
    self.expandingCircle.hidden = YES;
    [self sendSubviewToBack:self.expandingCircle];
    
    self.successIndicator.hidden = YES;
    self.successIndicator.animationRepeatCount = 1;
    self.successIndicator.animationDuration = (float)EHIReservationSuccessFrames / 15.0f;
    
    self.loadingIndicator.hidden = NO;
    self.loadingIndicator.animationDuration = (float)[self frameCountForType:self.type] / 15.0f;
	
	self.successLabel.text = EHILocalizedString(@"reservation_booking_success_title", @"Success!", @"");
    self.successLabel.textColor = [UIColor ehi_greenColor];
    self.successLabel.hidden = YES;
    
    [self loadFramesForType:self.type withHandler:^(NSArray *frames) {
        self.loadingIndicator.animationImages = frames;
        [self invalidateView];
    }];
    
    [self loadFramesForType:EHIReservationReviewIndicatorTypeSuccess withHandler:^(NSArray *frames) {
        self.successIndicator.animationImages = frames;
        [self invalidateView];
    }];
}

//
// Helpers
//

- (void)invalidateView
{
    self.alpha = !self.isAnimating ? 0.0f : 1.0f;
    
    if(self.isAnimating) {
        [self.loadingIndicator startAnimating];
    } else {
        [self.loadingIndicator stopAnimating];
    }
}

# pragma mark - Setters

- (void)setIsAnimating:(BOOL)isAnimating
{
    _isAnimating = isAnimating;
    
    [self invalidateView];
}

- (void)setIsGreen:(BOOL)isGreen
{
    self.type = isGreen ? EHIReservationReviewIndicatorTypeLoadingGreen : EHIReservationReviewIndicatorTypeLoading;
    
    [self resetView];
}

# pragma mark - Animation Frames

- (void)loadFramesForType:(EHIIndicatorType)type withHandler:(void(^)(NSArray *images))handler
{
    int capacity = [self frameCountForType:type];
    NSMutableArray *frames = [[NSMutableArray alloc] initWithCapacity:capacity+1];
    for (int i = 0; i < capacity; i++) {
        [frames insertObject:[NSNull null] atIndex:i];
    }
    
    __block int count = 0;
    [self loadImagesForType:type withHandler:^(UIImage *image, NSInteger index) {
        [frames setObject:image atIndexedSubscript:index];
        count++;
        
        if ((count == EHIReservationBookingAnimationFrames && type == EHIReservationReviewIndicatorTypeLoading) ||
            (count == EHIReservationSuccessFrames && type == EHIReservationReviewIndicatorTypeSuccess) ||
            (count == EHIReservationBookingAnimationGreenFrames && type == EHIReservationReviewIndicatorTypeLoadingGreen)) {
            handler(frames.copy);
        }
    }];
}

- (void)loadImagesForType:(EHIIndicatorType)type withHandler:(void(^)(UIImage *image, NSInteger index))handler
{
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0);
    
    int capacity = [self frameCountForType:type];
    for (int index = 0; index < capacity; index++) {
        dispatch_async(queue, ^{
            NSString *path = [self pathForImageWithType:type index:index];
            UIImage *image = [UIImage imageWithContentsOfFile:path];
            dispatch_async(dispatch_get_main_queue(), ^{
                handler(image, index);
            });
        });
    }
}

- (int)frameCountForType:(EHIIndicatorType)type
{
    switch (type) {
        case EHIReservationReviewIndicatorTypeLoading:
            return EHIReservationBookingAnimationFrames;
        case EHIReservationReviewIndicatorTypeLoadingGreen:
            return EHIReservationBookingAnimationGreenFrames;
        case EHIReservationReviewIndicatorTypeSuccess:
            return EHIReservationSuccessFrames;
    }
}

# pragma mark - Helpers

- (NSString *)pathForImageWithType:(EHIIndicatorType)type index:(NSInteger)index
{
    NSString *imageName = [self imageNameFormatForType:type];
    NSString *name = [NSString stringWithFormat:imageName, index + 1];
    return [[NSBundle mainBundle] pathForResource:[self appendDeviceModifierToString:name] ofType:@"png" inDirectory:nil];
}

- (NSString *)appendDeviceModifierToString:(NSString *)string
{
    return [NSString stringWithFormat:@"%@@%ix", string, (int)[UIScreen mainScreen].scale];
}

- (NSString *)imageNameFormatForType:(EHIIndicatorType)type
{
    switch (type) {
        case EHIReservationReviewIndicatorTypeLoading:
            return @"loader_book_icon_%i";
        case EHIReservationReviewIndicatorTypeLoadingGreen:
            return @"loader_book_icon_green_%i";
        case EHIReservationReviewIndicatorTypeSuccess:
            return @"check_mark_icon_%i";
    }
}

# pragma mark - EHIView

+ (BOOL)isReplaceable
{
    return YES;
}

@end
