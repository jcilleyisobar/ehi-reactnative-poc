//
//  EHIDashboardUpcomingRentalCell.m
//  Enterprise
//
//  Created by mplace on 5/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardUpcomingRentalCell.h"
#import "EHIDashboardUpcomingRentalViewModel.h"
#import "EHIButton.h"
#import "EHINetworkImageView.h"
#import "EHIRestorableConstraint.h"

@interface EHIDashboardUpcomingRentalCell ()
@property (strong, nonatomic) EHIDashboardUpcomingRentalViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;
@property (weak, nonatomic) IBOutlet UILabel *upcomingRentalTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *confirmationNumberTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *confirmationNumberLabel;
@property (weak, nonatomic) IBOutlet UILabel *relativePickupDateLabel;
@property (weak, nonatomic) IBOutlet UILabel *pickupDateLabel;
@property (weak, nonatomic) IBOutlet EHIButton *pickupLocationButton;
@property (weak, nonatomic) IBOutlet EHIButton *directionsButton;
@property (weak, nonatomic) IBOutlet EHIButton *directionsFromTerminalButton;
@property (weak, nonatomic) IBOutlet EHIButton *detailsButton;
@property (weak, nonatomic) IBOutlet EHINetworkImageView *vehicleImageView;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *airportContainerWidthConstraint;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *terminalDirectionsHeightConstraint;
@end

@implementation EHIDashboardUpcomingRentalCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardUpcomingRentalViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.detailsButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
    self.directionsButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    self.directionsButton.titleLabel.textAlignment = NSTextAlignmentCenter;

    self.directionsFromTerminalButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    self.directionsFromTerminalButton.titleLabel.textAlignment = NSTextAlignmentCenter;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardUpcomingRentalViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.upcomingRentalTitle)     : dest(self, .upcomingRentalTitleLabel.attributedText),
        source(model.confirmationNumberTitle) : dest(self, .confirmationNumberTitleLabel.text),
        source(model.confirmationNumber)      : dest(self, .confirmationNumberLabel.text),
        source(model.relativePickupDateTitle) : dest(self, .relativePickupDateLabel.text),
        source(model.pickupLocation)          : dest(self, .pickupLocationButton.ehi_attributedTitle),
        source(model.pickupDateTime)          : dest(self, .pickupDateLabel.text),
        source(model.directionsButtonTitle)   : dest(self, .directionsButton.ehi_title),
        source(model.detailsButtonTitle)      : dest(self, .detailsButton.ehi_title),
        source(model.vehicleImage)            : dest(self, .vehicleImageView.imageModel),
        source(model.shouldHideAirport)       : dest(self, .airportContainerWidthConstraint.isDisabled),
        source(model.shouldHideDirectionsFromTerminal): dest(self, .terminalDirectionsHeightConstraint.isDisabled),
        source(model.directionsFromTerminalText)      : dest(self, .directionsFromTerminalButton.ehi_title)
    });
}

# pragma mark - Actions

- (IBAction)didTapDirectionsButton:(id)sender
{
    [self.viewModel showDirections];
}

- (IBAction)didTapDetailsButton:(id)sender
{
    [self.viewModel showDetails];
}

- (IBAction)didTapLocationDetailsButton:(id)sender
{
    [self.viewModel showLocationDetails];
}

- (IBAction)didTapTerminalDirectionsButton:(id)sender
{
    [self.viewModel showDirectionsFromTerminal];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = self.contentContainer.bounds.size.height
    };
}

@end
