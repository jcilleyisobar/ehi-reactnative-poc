//
//  EHITierItemView.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/3/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITierItemView.h"
#import "EHITierItemViewModel.h"

@interface EHITierItemView ()
@property (strong, nonatomic) EHITierItemViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UIView *subContainerView;
@property (weak  , nonatomic) IBOutlet UILabel *firstInfoLabel;
@property (weak  , nonatomic) IBOutlet UILabel *firstInfoTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *orLabel;
@property (weak  , nonatomic) IBOutlet UILabel *secondInfoLabel;
@property (weak  , nonatomic) IBOutlet UILabel *secondInfoTitleLabel;
@end

@implementation EHITierItemView

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // hacky way to force the view to properly layout itself
    [self forceLayout];
}

# pragma mark - Reactions

- (void)registerReactions:(EHITierItemViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSubContainer:)];
    
    model.bind.map(@{
        source(model.firstInfo)       : dest(self, .firstInfoLabel.text),
        source(model.firstInfoTitle)  : dest(self, .firstInfoTitleLabel.text),
        source(model.orTitle)         : dest(self, .orLabel.text),
        source(model.secondInfo)      : dest(self, .secondInfoLabel.text),
        source(model.secondInfoTitle) : dest(self, .secondInfoTitleLabel.text)
    });
}

- (void)invalidateSubContainer:(MTRComputation *)computation
{
    BOOL hasSecondInfo = self.viewModel.secondInfo != nil;
    MASLayoutPriority priority = hasSecondInfo ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.secondInfoLabel mas_remakeConstraints:^(MASConstraintMaker *make) {
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

# pragma mark - Replaceability

+ (BOOL)isReplaceable
{
    return YES;
}

@end
