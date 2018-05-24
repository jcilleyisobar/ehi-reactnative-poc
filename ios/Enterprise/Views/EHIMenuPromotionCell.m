//
//  EHIMenuPromotionCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/29/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIMenuPromotionCell.h"
#import "EHIPromotionViewModel.h"
#import "EHIButton.h"

@interface EHIMenuPromotionCell()
@property (strong, nonatomic) EHIPromotionViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIImageView *promotionImageView;
@property (weak  , nonatomic) IBOutlet UILabel *promotionLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *learnMoreButton;
@end

@implementation EHIMenuPromotionCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPromotionViewModel new];
    }
    
    return self;
}

# pragma mark - View Lifecycle

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.learnMoreButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPromotionViewModel *)model
{
    model.bind.map(@{
        source(model.promotionName)        : dest(self, .promotionLabel.text),
        source(model.promotionButtonTitle) : dest(self, .learnMoreButton.ehi_title)
    });
}

# pragma mark - Actions

- (IBAction)didTapLearnMore:(EHIButton *)sender
{
    [self ehi_performAction:@selector(didTapMenuPromotionCell:) withSender:self];
}

#pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.promotionImageView.frame)
    };
}

@end
