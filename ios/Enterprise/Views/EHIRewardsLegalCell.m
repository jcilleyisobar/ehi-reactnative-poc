//
//  EHIRewardsLegalCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/23/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsLegalCell.h"
#import "EHIRewardsLegalViewModel.h"
#import "EHILabel.h"

@interface EHIRewardsLegalCell ()
@property (strong, nonatomic) EHIRewardsLegalViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet EHILabel *legalLabel;
@end

@implementation EHIRewardsLegalCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRewardsLegalViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRewardsLegalViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.legal) : dest(self, .legalLabel.attributedText)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + EHILightPadding
    };
}

@end
