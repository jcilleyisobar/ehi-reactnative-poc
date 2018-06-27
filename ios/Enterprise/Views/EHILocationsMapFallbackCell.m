//
//  EHILocationFallbackCell.m
//  Enterprise
//
//  Created by Ty Cobb on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationsMapFallbackCell.h"

@interface EHILocationsMapFallbackCell ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHILocationsMapFallbackCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.titleLabel.text = EHILocalizedString(@"locations_map_fallback_title", @"Sorry, there are no locations near your search. Try another location", @"Title for the locations map fallback");
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 70.0f };
    metrics.isAutomaticallyRegisterable = NO;
    return metrics;
}

@end
