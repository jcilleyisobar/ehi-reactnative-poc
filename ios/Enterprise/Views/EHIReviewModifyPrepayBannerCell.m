//
//  EHIReviewModifyPrepayCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewModifyPrepayBannerCell.h"
#import "EHIReviewModifyPrepayBannerViewModel.h"

@interface EHIReviewModifyPrepayBannerCell ()
@property (strong, nonatomic) EHIReviewModifyPrepayBannerViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *totalAmountLabel;

@property (weak  , nonatomic) IBOutlet UIView *priceContainerView;
@end

@implementation EHIReviewModifyPrepayBannerCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewModifyPrepayBannerViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewModifyPrepayBannerViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidatePriceContainer:)];
    
    model.bind.map(@{
        source(model.title)          : dest(self, .titleLabel.text),
        source(model.subtitle)       : dest(self, .subtitleLabel.text),
        source(model.totalAmount)    : dest(self, .totalAmountLabel.text)
    });
}

- (void)invalidatePriceContainer:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.totalAmount == nil;
    
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.priceContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.containerView.frame) + EHIHeavyPadding
    };
}

@end
