//
//  EHIFormFieldButtonCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIFormFieldButtonCell.h"
#import "EHIFormFieldButtonViewModel.h"
#import "EHIButton.h"

@interface EHIFormFieldButtonCell ()
@property (strong, nonatomic) EHIFormFieldButtonViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *button;
@property (weak  , nonatomic) IBOutlet UIView *containterView;
@end

@implementation EHIFormFieldButtonCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIFormFieldButtonViewModel new];
    }

    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.button.titleLabel.numberOfLines = 0;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldButtonViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title) : dest(self, .button.ehi_title)
    });
}

# pragma mark - Actions

- (IBAction)didTap:(id)sender
{
    [self.viewModel performButtonAction];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containterView.frame)
    };
}

@end
