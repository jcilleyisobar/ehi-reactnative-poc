//
//  EHIEnrollmentStepHeaderCell.m
//  Enterprise
//
//  Created by Rafael Machado on 8/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepHeaderCell.h"
#import "EHIEnrollmentStepHeaderViewModel.h"
#import "EHIButton.h"

@interface EHIEnrollmentStepHeaderCell ()
@property (strong, nonatomic) EHIEnrollmentStepHeaderViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *stepTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *stepDetailLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *scanButton;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UIView *stepTitleContainer;
@property (weak  , nonatomic) IBOutlet UIView *scanButtonContainer;
@end

@implementation EHIEnrollmentStepHeaderCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIEnrollmentStepHeaderViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIEnrollmentStepHeaderViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateScanContainer:)];
    [MTRReactor autorun:self action:@selector(invalidateStepTitleContainer:)];
    
    model.bind.map(@{
        source(model.stepTitle)       : dest(self, .stepTitleLabel.text),
        source(model.stepDetail)      : dest(self, .stepDetailLabel.text),
        source(model.scanButtonTitle) : dest(self, .scanButton.ehi_title)
    });
}

- (void)invalidateScanContainer:(MTRComputation *)computation
{
    BOOL hideScan = self.viewModel.hideScan;
    
    MASLayoutPriority priority = hideScan ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.scanButtonContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
}

- (void)invalidateStepTitleContainer:(MTRComputation *)computation
{
    BOOL hideTitle = self.viewModel.stepTitle.length == 0;
    
    MASLayoutPriority priority = hideTitle ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.stepTitleContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame)
    };
}

@end
