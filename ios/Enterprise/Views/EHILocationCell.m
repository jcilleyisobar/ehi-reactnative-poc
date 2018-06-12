//
//  EHILocationCell.m
//  Enterprise
//
//  Created by mplace on 2/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationCell.h"
#import "EHILocationViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHIButton.h"

@interface EHILocationCell ()
@property (strong, nonatomic) EHILocationViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *tagsLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak  , nonatomic) IBOutlet EHIButton *selectButton;
@property (weak  , nonatomic) IBOutlet EHIButton *detailsButton;
@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *iconWidth;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *iconLeading;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *subtitleSpacing;
@property (nonatomic, readonly) UIView *bottomView;
@end

@implementation EHILocationCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        // Initialize view model
        self.viewModel = [EHILocationViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // Style location details button
    self.selectButton.type = EHIButtonTypeLocation;
    self.selectButton.showsBorder = NO;
}

- (void)prepareForReuse
{
    [super prepareForReuse];
    
    self.titleLabel.attributedText = nil;
    self.subtitleLabel.text = nil;
}

- (void)updateConstraints
{
    [super updateConstraints];
   
    self.iconWidth.constant   = self.iconImageView.image.size.width;
    self.iconLeading.constant = self.viewModel.hidesIcon ? -self.iconWidth.constant : EHIRestorableConstant;
    
    self.subtitleSpacing.isDisabled = self.viewModel.hidesSubtitle;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.selectButton.accessibilityIdentifier = EHILocationsSelectLocationKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateButtonVisibility:)];

    model.bind.map(@{
        source(model.title)             : dest(self, .titleLabel.attributedText),
        source(model.subtitle)          : dest(self, .subtitleLabel.text),
        source(model.tagsText)          : dest(self, .tagsLabel.attributedText),
        source(model.selectButtonTitle) : dest(self, .selectButton.ehi_title),
        source(model.iconImageName)     : dest(self, .iconImageView.ehi_imageName),
    });
}

- (void)invalidateButtonVisibility:(MTRComputation *)computation
{
    BOOL hidesSelectButton  = self.viewModel.hidesSelectButton;
    
    self.selectButton.hidden = hidesSelectButton;
    self.detailsButton.userInteractionEnabled = !hidesSelectButton;
}

# pragma mark - Interface Actions

- (IBAction)didTapLocationDetailsButton:(UIButton *)sender
{
    // push the location details screen with the selected location
    [self.viewModel showLocationDetails];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGRect bottomFrame = [self.bottomView convertRect:self.bottomView.bounds toView:self];
    
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomFrame) + EHILightPadding
    };
}

//
// Helpers
//

- (UIView *)bottomView
{
    if(self.viewModel.tagsText) {
        return self.tagsLabel;
    } else if(self.viewModel.subtitle) {
        return self.subtitleLabel;
    }
    
    return self.titleLabel;
}

@end
