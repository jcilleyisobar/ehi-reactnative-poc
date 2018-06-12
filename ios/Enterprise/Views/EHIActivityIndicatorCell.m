//
//  EHIActivityIndicatorCell.m
//  Enterprise
//
//  Created by mplace on 5/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIActivityIndicatorCell.h"
#import "EHIActivityIndicator.h"

@interface EHIActivityIndicatorCell ()
@property (weak, nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;
@end

@implementation EHIActivityIndicatorCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    /*
     When this cell appears when a view controller initially appears, this animation
     gets caught as part of NAVNavigationControllerUpdater's
     
     - (void)performUpdate:(NAVUpdate *)update withTransaction:(void(^)(void))transaction completion:(void(^)(BOOL finished))completion {
        [CATransation begin];
        transaction();
        [CATransaction commit];
     }
     
     The activity indicator animates infinitely so the transition never ends, which
     blocks all future transitions. Must run asynchronously to avoid being added to 
     the CATransaction.
     */
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.activityIndicator startAnimating];
    });
}

# pragma mark - Layout

+ (EHILayoutMetrics *)metrics
{
    EHILayoutMetrics *metrics = [self.defaultMetrics copy];
    metrics.fixedSize = (CGSize) {.width = EHILayoutValueNil, .height = 200};
    return metrics;
}

@end
