//
//  EHILayoutMetrics.m
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "Reactor.h"
#import "EHILayoutMetrics.h"
#import "EHILayoutable.h"
#import "EHIUpdatable.h"

@implementation EHILayoutMetrics

+ (NSMutableDictionary *)metricsMap
{
    static NSMutableDictionary *metricsMap;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        metricsMap = [NSMutableDictionary new];
    });
    
    return metricsMap;
}

- (instancetype)init
{
    if(self = [super init]) {
        // by default, use relative layout with insets
        _fixedSize = EHILayoutSizeNil;
        _centerOffset = EHILayoutOffsetNil;
        _isAutomaticallyRegisterable = YES;
    }
    
    return self;
}

# pragma mark - Factory

+ (void)prepareToLaunch
{
    [NSObject implementClassMethodNamed:@selector(metrics) imp:(IMP)ehi_metrics types:"@@:" forClassesConformingToProtocol:@protocol(EHILayoutable)];
}

+ (instancetype)metricsForClass:(Class<EHILayoutable>)klass
{
    NSString *key = NSStringFromClass(klass);
    EHILayoutMetrics *metrics = self.metricsMap[key];
    
    if(!metrics) {
        metrics = [klass defaultMetrics];
        NSAssert(metrics, @"Classes conforming to EHILayoutable may not return nil from +defaultMetrics");
        self.metricsMap[key] = metrics;
    }
    
    return metrics;
}

# pragma mark - Calculation

- (CGSize)sizeForContainerSize:(CGSize)size
{
    CGSize result = self.fixedSize;
    
    // if we don't have a fixed width, calculate from the parent and insets
    if(result.width == EHILayoutValueNil) {
        result.width = size.width - self.insets.left - self.insets.right;
    }
  
    // if we don't have a fixed height, calculate from the parent and insets
    if(result.height == EHILayoutValueNil) {
        result.height = size.height - self.insets.top - self.insets.bottom;
    }
    
    return result;
}

- (CGSize)dynamicSizeForView:(UIView<EHIUpdatable> *)view containerSize:(CGSize)size model:(id)model
{
    // check that we actually have a view (maybe assert?)
    if(!view) {
        return CGSizeZero;
    }
   
    // if the view is dynamic, it's fixed size should start unset until we
    // calculate it
    self.fixedSize = EHILayoutSizeNil;
    
    // give the label its default size
    view.frame = (CGRect){
        .size = [self sizeForContainerSize:size]
    };
    
    // populate the reference cell with model
    [view updateWithModel:model metrics:self];
    
    // force a flush so that the cell will be updated properly
    [[MTRReactor reactor] flush];
    
    // trigger layout
    [view setNeedsUpdateConstraints];
    [view setNeedsLayout];
    [view layoutIfNeeded];
    
    // update metrics with intrinsic content size
    self.fixedSize = [view intrinsicContentSize];
    
    return [self sizeForContainerSize:size];
}

# pragma mark - Debugging

- (NSString *)description
{
    return [[NSString alloc] initWithFormat:@"<%@: %p; insets = %@; size = %@>",
        self, self, NSStringFromUIEdgeInsets(self.insets), NSStringFromCGSize(self.fixedSize)];
}

# pragma mark - NSCopying

- (id)copyWithZone:(NSZone *)zone
{
    EHILayoutMetrics *metrics = [self.class new];
    
    metrics.identifier = self.identifier;
    metrics.primaryFont = self.primaryFont;
    metrics.secondaryFont = self.secondaryFont;
    metrics.primaryColor = self.primaryColor;
    metrics.insets = self.insets;
    metrics.centerOffset = self.centerOffset;
    metrics.fixedSize = self.fixedSize;
    metrics.isReplaceable = self.isReplaceable;
    metrics.isDeviceSpecific = self.isDeviceSpecific;
    metrics.isAutomaticallyRegisterable = self.isAutomaticallyRegisterable;
    
    return metrics;
}

# pragma mark - Swizzling

EHILayoutMetrics * ehi_metrics(id self, SEL _cmd)
{
    return [EHILayoutMetrics metricsForClass:self];
}

@end

@implementation MASConstraintMaker (Metrics)

- (void (^)(EHILayoutMetrics *))metrics
{
    return ^(EHILayoutMetrics *metrics) {
        UIView *superview = [[self performSelector:@selector(view)] superview];
       
        // add explicit width metric for fixed width
        if(metrics.fixedSize.width != EHILayoutValueNil) {
            self.width.equalTo(@(metrics.fixedSize.width));
        }
        
        // center horizontally if specified
        if(metrics.centerOffset.horizontal != EHILayoutValueNil) {
            self.centerX.equalTo(superview.mas_centerX).offset(metrics.centerOffset.horizontal);
        }
        // otherwise add inset metrics
        else {
            if(metrics.insets.left != EHILayoutValueNil) {
                self.left.equalTo(superview.mas_left).offset(metrics.insets.left);
            }
            if(metrics.insets.right != EHILayoutValueNil) {
                self.right.equalTo(superview.mas_right).offset(-metrics.insets.right);
            }
        }
       
        // add explicit height metric for fixed height
        if(metrics.fixedSize.height != EHILayoutValueNil) {
            self.height.equalTo(@(metrics.fixedSize.height));
        }
        
        // center vertically if specified
        if(metrics.centerOffset.vertical != EHILayoutValueNil) {
            self.centerY.equalTo(superview.mas_centerY).offset(metrics.centerOffset.vertical);
        }
        // otherwise add inset metrics
        else {
            if(metrics.insets.top != EHILayoutValueNil) {
                self.top.equalTo(superview.mas_top).offset(metrics.insets.top);
            }
            if(metrics.insets.bottom != EHILayoutValueNil) {
                self.bottom.equalTo(superview.mas_bottom).offset(metrics.insets.bottom);
            }
        }
    };
}

@end
