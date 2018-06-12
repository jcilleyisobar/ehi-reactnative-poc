//
//  EHIExtrasTermsCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 16/04/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIExtrasTermsCell.h"
#import "EHIExtrasTermsViewModel.h"
#import "EHIButton.h"

@interface EHIExtrasTermsCell ()
@property (strong, nonatomic) EHIExtrasTermsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *termsButton;
@end

@implementation EHIExtrasTermsCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIExtrasTermsViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.termsButton.titleLabel.textAlignment = NSTextAlignmentLeft;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIExtrasTermsViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .termsButton.ehi_title)
    });
}

# pragma mark - Action

- (IBAction)didTapTerms:(UIButton *)sender
{
    [self.viewModel showTerms];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.termsButton.frame) + EHIMediumPadding
    };
}

@end
