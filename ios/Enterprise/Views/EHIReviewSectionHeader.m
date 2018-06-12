//
//  EHIReviewSectionHeader.m
//  Enterprise
//
//  Created by Rafael Machado on 8/1/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewSectionHeader.h"
#import "EHIReviewSectionHeaderViewModel.h"
#import "EHIRestorableConstraint.h"

@interface EHIReviewSectionHeader ()
@property (strong, nonatomic) EHIReviewSectionHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIView *sectionDivider;
@property (weak  , nonatomic) IBOutlet UIView *titleContainer;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *dividerHeight;
@end

@implementation EHIReviewSectionHeader

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewSectionHeaderViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewSectionHeaderViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title)       : dest(self, .titleLabel.text),
        source(model.hideDivider) : dest(self, .dividerHeight.isDisabled)
    });
}

- (CGSize)intrinsicContentSize
{
    CGFloat height = CGRectGetHeight(self.sectionDivider.frame) + CGRectGetHeight(self.titleContainer.frame);
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = height + 10.0f
    };
}

@end
