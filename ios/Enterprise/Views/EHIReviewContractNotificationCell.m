//
//  EHIReviewContractNotificationCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewContractNotificationCell.h"
#import "EHIContractNotificationView.h"

@interface EHIReviewContractNotificationCell ()
@property (weak, nonatomic) IBOutlet EHIContractNotificationView *contractView;
@end

@implementation EHIReviewContractNotificationCell

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
	[super updateWithModel:model metrics:metrics];

    [self.contractView updateWithModel:model metrics:metrics];
}

# pragma mark - Layout

-(NSArray *)customSubviews
{
    return @[self.contractView];
}

- (CGSize)intrinsicContentSize
{
    CGFloat margin = 10.f;
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.contractView.frame) + margin
    };
}

@end
