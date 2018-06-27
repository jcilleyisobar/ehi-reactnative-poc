
//
//  EHIReviewAdditionalInfoItemCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReviewAdditionalInfoItemCell.h"
#import "EHIReviewAdditionalInfoItemViewModel.h"

@interface EHIReviewAdditionalInfoItemCell ()
@property (strong, nonatomic) EHIReviewAdditionalInfoItemViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *valueLabel;
@end

@implementation EHIReviewAdditionalInfoItemCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewAdditionalInfoItemViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewAdditionalInfoItemViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.attributedText),
        source(model.value) : dest(self, .valueLabel.attributedText)
    });
}

# pragma mark - Actions

- (IBAction)didTapCell:(id)sender
{
    [self ehi_performAction:@selector(didTapAdditionalInfoCell) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.valueLabel.frame)
    };
}

@end
