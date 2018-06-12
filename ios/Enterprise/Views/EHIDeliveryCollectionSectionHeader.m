//
//  EHIDeliveryCollectionHeader.m
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDeliveryCollectionSectionHeader.h"

@interface EHIDeliveryCollectionSectionHeader ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHIDeliveryCollectionSectionHeader

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    self.titleLabel.text = model;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = self.titleLabel.bounds.size.height + 2 * EHIMediumPadding
    };
}

@end
