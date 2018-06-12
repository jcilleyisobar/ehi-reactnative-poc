//
//  EHIItineraryReturnLocationView.m
//  Enterprise
//
//  Created by mplace on 3/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIItineraryReturnLocationView.h"
#import "EHIItineraryReturnLocationViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHIReservationRouter.h"
#import "EHIButton.h"
#import "EHILabel.h"

@interface EHIItineraryReturnLocationView ()
@property (strong, nonatomic) EHIItineraryReturnLocationViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UIView *containerView;

@property (weak  , nonatomic) IBOutlet EHIButton *alternateReturnLocationButton;
@property (weak  , nonatomic) IBOutlet EHILabel *returnLocationTitleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *returnLocationRemovalButton;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *iconContainerWidthConstraint;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *lockIconContainerWidthConstraint;
@end

@implementation EHIItineraryReturnLocationView

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        // initialize view model
        self.viewModel = [EHIItineraryReturnLocationViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.iconImageView.tintColor = [UIColor blackColor];
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.accessibilityIdentifier = EHIItinerarySelectReturnDateKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIItineraryReturnLocationViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(updateReturnLocation:)];
    [MTRReactor autorun:self action:@selector(invalidateIconImage:)];
    
    model.bind.map(@{
        source(model.alternateReturnLocationButtonTitle) : dest(self, .alternateReturnLocationButton.ehi_title),
        source(model.returnLocationTitle)                : dest(self, .returnLocationTitleLabel.text)
    });
}

- (void)updateReturnLocation:(MTRComputation *)computation
{
    // if its a one way reservation we should hide the reservation title and remove buttons
    CGFloat locationAlpha = self.viewModel.showsReturnLocation ? 1.0f : 0.0f;
    
    [UIView animateWithDuration:.25 animations:^{
        self.returnLocationRemovalButton.alpha = locationAlpha;
        self.returnLocationTitleLabel.alpha = locationAlpha;
        self.alternateReturnLocationButton.alpha = 1.0f - locationAlpha;
        self.iconImageView.alpha = locationAlpha;
    } completion:^(BOOL finished) {
        self.iconContainerWidthConstraint.isDisabled = self.viewModel.shouldHideIcon;
        self.lockIconContainerWidthConstraint.isDisabled = self.viewModel.shouldHideLock;
    }];
}

- (void)invalidateIconImage:(MTRComputation *)computation
{
    // render as template so we can tint it
    UIImage *iconImage = [[UIImage imageNamed:self.viewModel.iconImageName] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    self.iconImageView.image = iconImage;
}

# pragma mark - View Actions

- (IBAction)didTapAlternateReturnLocationButton:(id)sender
{
    [self.viewModel findReturnLocation];
}

- (IBAction)didTapReturnLocationRemovalButton:(id)sender
{
    [self.viewModel clearReturnLocation];
}

@end
