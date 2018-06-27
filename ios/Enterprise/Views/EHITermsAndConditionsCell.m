//
//  EHITermsAndConditionsCell.m
//  Enterprise
//
//  Created by frhoads on 10/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHITermsAndConditionsCell.h"
#import "EHITermsAndConditionsCellViewModel.h"
#import "EHILabel.h"

@interface EHITermsAndConditionsCell ()
@property (strong, nonatomic) EHITermsAndConditionsCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@end

@implementation EHITermsAndConditionsCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel  = [EHITermsAndConditionsCellViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHITermsAndConditionsCellViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateLayout:)];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text)
    });
}

- (void)invalidateLayout:(MTRComputation *)computation
{
    depend(self.viewModel.layout);
    
    self.contentView.backgroundColor = self.viewModel.layout == EHITermsAndConditionsLayoutReview
        ? [UIColor ehi_grayColor0]
        : [UIColor whiteColor];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.titleLabel.frame) + EHIMediumPadding
    };
}

@end
