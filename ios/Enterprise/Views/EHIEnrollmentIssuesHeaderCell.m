//
//  EHIEnrollmentIssuesHeaderCell.m
//  Enterprise
//
//  Created by Rafael Machado on 8/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentIssuesHeaderCell.h"

@interface EHIEnrollmentIssuesHeaderCell ()
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@end

@implementation EHIEnrollmentIssuesHeaderCell

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[NSString class]]) {
        self.titleLabel.text = (NSString *)model;
    }
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + EHIHeavyPadding
    };
}

@end
