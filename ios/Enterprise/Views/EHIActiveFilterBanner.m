//
//  EHIActiveFilterBanner.m
//  Enterprise
//
//  Created by Michael Place on 4/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIActiveFilterBanner.h"
#import "EHILabel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"
#import "EHIActiveFilterBannerViewModel.h"

@interface EHIActiveFilterBanner ()
@property (strong, nonatomic) EHIActiveFilterBannerViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *clearButton;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@end

@implementation EHIActiveFilterBanner

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIActiveFilterBannerViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // style the clear button
    self.clearButton.ehi_titleColor  = [UIColor ehi_greenColor];
    self.clearButton.showsBorder     = YES;
    self.clearButton.borderColor     = [UIColor ehi_greenColor];
    self.clearButton.backgroundColor = [UIColor whiteColor];
    
    // tint the filter icon
    self.iconImageView.tintColor = [UIColor blackColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIActiveFilterBannerViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.clearButtonTitle) : dest(self, .clearButton.ehi_title),
        source(model.attributedTitle)  : dest(self, .titleLabel.attributedText),
    });
}

# pragma mark - Actions

- (IBAction)didTapClearFiltersButton:(UIButton *)button
{
    [self ehi_performAction:@selector(didTapClearButtonForFilterBanner:) withSender:self];
}

# pragma mark - Replaceability

+ (BOOL)isReplaceable
{
    return YES;
}

@end
