//
//  EHIRedemptionLineItemsView.m
//  Enterprise
//
//  Created by fhu on 8/12/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionLineItemsView.h"
#import "EHIRedemptionLineItemsViewModel.h"

@interface EHIRedemptionLineItemsView()
@property (weak, nonatomic) IBOutlet UILabel *redeemingTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *redeemingLabel;
@property (weak, nonatomic) IBOutlet UILabel *pointsSpentTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *pointsSpentLabel;
@property (weak, nonatomic) IBOutlet UILabel *creditTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *creditLabel;
@end

@implementation EHIRedemptionLineItemsView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRedemptionLineItemsViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRedemptionLineItemsViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.redeemingTitle) : dest(self, .redeemingTitleLabel.text),
        source(model.redeemingDays)  : dest(self, .redeemingLabel.text),
        source(model.pointsTitle)    : dest(self, .pointsSpentTitleLabel.text),
        source(model.points)         : dest(self, .pointsSpentLabel.text),
        source(model.creditTitle)    : dest(self, .creditTitleLabel.text),
        source(model.credits)        : dest(self, .creditLabel.attributedText)
    });
}

@end
