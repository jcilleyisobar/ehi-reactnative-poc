//
//  EHIEnrollmentWarningCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentWarningCell.h"

@interface EHIEnrollmentWarningCell ()
@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet UILabel *messageLabel;
@end

@implementation EHIEnrollmentWarningCell


- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
	[super updateWithModel:model metrics:metrics];

    if([model isKindOfClass:[NSString class]]) {
        self.messageLabel.text = (NSString *)model;
    }
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.containerView.frame)
    };
}

@end
