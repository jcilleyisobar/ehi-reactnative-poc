//
//  EHIPromotionDetailsPolicyCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsPolicyCell.h"
#import "EHIPromotionDetailsPolicyCellViewModel.h"
#import "EHIButton.h"

@interface EHIPromotionDetailsPolicyCell ()
@property (strong, nonatomic) EHIPromotionDetailsPolicyCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *policiesButton;
@end

@implementation EHIPromotionDetailsPolicyCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPromotionDetailsPolicyCellViewModel new];
    }

    return self;
}

- (IBAction)didTapPolicies:(id)sender
{
    [self.viewModel didTapPolicies];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPromotionDetailsPolicyCellViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.policiesButtonTitle) : dest(self, .policiesButton.ehi_title)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.policiesButton.frame) + EHIMediumPadding
    };
}

@end
