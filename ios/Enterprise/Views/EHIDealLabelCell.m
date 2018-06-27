//
//  EHIDealLabelCell.m
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealLabelCell.h"
#import "EHIDealLabelViewModel.h"

@interface EHIDealLabelCell ()
@property (strong, nonatomic) EHIDealLabelViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *dealNameLabel;
@property (weak  , nonatomic) IBOutlet UILabel *termsLabel;
@end

@implementation EHIDealLabelCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDealLabelViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDealLabelViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.dealName) : dest(self, .dealNameLabel.text),
        source(model.terms)    : dest(self, .termsLabel.text),
    });
}

# pragma mark - Actions

- (IBAction)didTap:(UIControl *)sender
{
    [self.viewModel tapDeal];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.termsLabel.frame) + EHIMediumPadding
    };
}

@end
