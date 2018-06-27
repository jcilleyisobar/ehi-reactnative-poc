//
//  EHILocationDetailsPickupCell.m
//  Enterprise
//
//  Created by Ty Cobb on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDetailsPickupCell.h"
#import "EHILocationDetailsPickupViewModel.h"

@interface EHILocationDetailsPickupCell ()
@property (strong, nonatomic) EHILocationDetailsPickupViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *detailsLabel;
@end

@implementation EHILocationDetailsPickupCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHILocationDetailsPickupViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationDetailsPickupViewModel *)model
{
    [super registerReactions:model];
  
    [MTRReactor autorun:self action:@selector(updateDetailsText:)];
   
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text)
    });
}

- (void)updateDetailsText:(MTRComputation *)computation
{
    self.detailsLabel.attributedText = self.detailsLabel.attributedText.rebuild
        .text(self.viewModel.details)
        .range(self.viewModel.highlightRange).color([UIColor ehi_greenColor]).string;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    const CGFloat bottomPadding = 25.0f;
    
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.detailsLabel.frame) + bottomPadding
    };
}

@end
