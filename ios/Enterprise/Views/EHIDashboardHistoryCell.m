//
//  EHIDashboardHistoryCell.m
//  Enterprise
//
//  Created by mplace on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardHistoryCell.h"
#import "EHIDashboardHistoryViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHIButton.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIDashboardHistoryCell ()
@property (strong, nonatomic) EHIDashboardHistoryViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *titleTopSpacing;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *trackButton;
@end

@implementation EHIDashboardHistoryCell

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardHistoryViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.trackButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardHistoryViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.iconImageName)     : dest(self, .iconImageView.ehi_imageName),
        source(model.title)             : dest(self, .titleLabel.text),
        source(model.details)           : dest(self, .detailsLabel.attributedText),
        source(model.hidesTitle)        : dest(self, .titleTopSpacing.isDisabled),
        source(model.trackButtonTitle)  : dest(self, .trackButton.ehi_title),
        source(model.hidesTrackButton)  : dest(self, .trackButton.hidden),
    });
}

# pragma mark - Actions

- (IBAction)didTapTrackButton:(id)sender
{
    [self.viewModel enableTracking];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *bottomView = self.viewModel.hidesTrackButton ? self.detailsLabel : self.trackButton;
    CGRect frame = [self convertRect:bottomView.bounds fromView:bottomView];
    
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(frame) + EHIHeaviestPadding
    };
}

@end

NS_ASSUME_NONNULL_END