//
//  EHIRentalsFallbackCell.m
//  Enterprise
//
//  Created by fhu on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsFallbackCell.h"
#import "EHIRentalsFallbackViewModel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIRentalsFallbackCell()
@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (strong, nonatomic) EHIRentalsFallbackViewModel *viewModel;
@end

@implementation EHIRentalsFallbackCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRentalsFallbackViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRentalsFallbackViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.headerText) : dest(self, .headerLabel.text),
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame) + EHIHeaviestPadding,
    };
}

@end
