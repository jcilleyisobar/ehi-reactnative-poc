//
//  EHICollectionTitleView.m
//  Enterprise
//
//  Created by Ty Cobb on 1/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionTitleView.h"

@interface EHICollectionTitleView ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHICollectionTitleView

- (void)updateWithModel:(NSString *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
   
    // update the data
    self.titleLabel.text = model;
   
    // update layout based on metrics
    if(metrics.primaryFont) {
        self.titleLabel.font = metrics.primaryFont;
    }
    
    if(metrics.primaryColor) {
        self.titleLabel.textColor = metrics.primaryColor;
    }
    
    if(metrics.backgroundColor) {
        self.backgroundColor = metrics.backgroundColor;
    }
}

# pragma mark - EHILayoutable

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 44.0f };
    return metrics;
}

@end
