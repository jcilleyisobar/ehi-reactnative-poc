//
//  EHIGaugeViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/6/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIGaugeViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIUserLoyalty.h"

@interface EHIGaugeViewModel ()
@property (strong, nonatomic) EHIUserLoyalty *loyalty;
@property (assign, nonatomic) EHIUserLoyaltyTier tier;
@property (assign, nonatomic) CGFloat fill;
@property (assign, nonatomic) CGFloat total;
@property (assign, nonatomic) BOOL didAnimate;
@end

@implementation EHIGaugeViewModel

- (instancetype)initWithLoyalty:(EHIUserLoyalty *)loyalty fill:(CGFloat)fill
{
    if(self = [super init]) {
        self.loyalty = loyalty;
        self.total   = fill;
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [self runFillIfNeeded];
}

- (void)runFillIfNeeded
{
    if(self.didAnimate) {
        return;
    }
    NSTimeInterval fps = 1.0/30.0;
    NSTimer *timer = [NSTimer timerWithTimeInterval:fps target:self selector:@selector(calculateFill:) userInfo:[NSDate date] repeats:YES];
    [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
}

- (void)calculateFill:(NSTimer *)timer
{
    NSTimeInterval difference = [[NSDate date] timeIntervalSinceDate:timer.userInfo];
    CGFloat progress = difference / EHIMeterLayerAnimationDuration;
    CGFloat fill = _total * progress;
    
    if(fill >= _total) {
        fill = _total;
        [timer invalidate];
        self.didAnimate = YES;
    }
    
    self.fill = fill;
}

# pragma mark - Arc data

- (EHIArcSegmentData)arcData
{
    switch(self.tier) {
        case EHIUserLoyaltyTierSilver:
        case EHIUserLoyaltyTierPlus:
            return (EHIArcSegmentData) {
                .segments     = self.loyalty.goal.nextTierRentals ?: 0,
                .lineWidth    = 1.0f,
                // the meter line width with a plus (to be drawn outside of the bounds)
                .offset       = 28.0f,
                .segmentColor = self.segmentColor
            };
        case EHIUserLoyaltyTierGold:
        case EHIUserLoyaltyTierPlatinum:
        case EHIUserLoyaltyTierUnknown:
            return EHIArcSegmentDataNull;
    }
}

# pragma mark - Meter Data

- (EHIMeterData)meterData
{
    return (EHIMeterData) {
        .backgroundColor = self.meterBackgroundColor,
        .fillColor       = [UIColor clearColor].CGColor,
        .outlineColor    = self.outlineColor
    };
}

- (EHIMeterData)innerMeterData
{
    EHIMeterFillStrategyType fillStrategy = EHIMeterFillStrategyTypePercent;
    NSInteger segments = 0;
    switch(self.tier) {
        case EHIUserLoyaltyTierSilver:
        case EHIUserLoyaltyTierPlus:{
            fillStrategy = EHIMeterFillStrategyTypeStep;
            segments     = self.arcData.segments;
            break;
        }
        default:
            break;
    }
    
    return (EHIMeterData) {
        .fillStrategy    = fillStrategy,
        .segments        = segments,
        .backgroundColor = self.innerMeterBackgroundColor,
        .fillColor       = self.innerMeterFillColor,
    };
}

# pragma mark - Colors

- (CGColorRef)segmentColor
{
    return [UIColor ehi_grayColor1].CGColor;
}

- (CGColorRef)outlineColor
{
    return [UIColor ehi_graySpecialColor].CGColor;
}

- (CGColorRef)innerMeterBackgroundColor
{
    return [UIColor ehi_graySpecialColor].CGColor;
}

- (CGColorRef)meterBackgroundColor
{
    return [UIColor ehi_grayColor0].CGColor;
}

- (CGColorRef)innerMeterFillColor
{
    return [EHILoyaltyTierColorForTier(self.tier) CGColor];
}

//
// Helpers
//

- (EHIUserLoyaltyTier)tier
{
    return self.loyalty.goal.tier;
}

@end
