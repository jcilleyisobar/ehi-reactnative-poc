//
//  EHIReviewAdditionalInfoAddCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewAdditionalInfoAddCell.h"
#import "EHIReviewAdditionalInfoAddViewModel.h"
#import "EHIButton.h"

@interface EHIReviewAdditionalInfoAddCell ()
@property (strong, nonatomic) EHIReviewAdditionalInfoAddViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *addInfoButton;
@end

@implementation EHIReviewAdditionalInfoAddCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewAdditionalInfoAddViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewAdditionalInfoAddViewModel *)model
{
    model.bind.map(@{
        source(model.details)      : dest(self, .detailsLabel.text),
        source(model.addInfoTitle) : dest(self, .addInfoButton.ehi_title)
    });
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
