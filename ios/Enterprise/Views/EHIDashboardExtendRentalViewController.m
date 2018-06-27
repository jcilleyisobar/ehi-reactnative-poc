//
//  EHIDashboardExtendRentalViewController.m
//  Enterprise
//
//  Created by mplace on 6/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardExtendRentalViewController.h"
#import "EHIDashboardExtendRentalViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHIButton.h"

@interface EHIDashboardExtendRentalViewController ()
@property (strong, nonatomic) EHIDashboardExtendRentalViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak, nonatomic) IBOutlet UILabel *reservationTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *reservationLabel;
@property (weak, nonatomic) IBOutlet UIButton *dismissButton;
@property (weak, nonatomic) IBOutlet EHIButton *actionButton;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *ticketNumberHeightConstraint;

@end

@implementation EHIDashboardExtendRentalViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardExtendRentalViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardExtendRentalViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)                  : dest(self, .titleLabel.text),
        source(model.detailsTitle)           : dest(self, .detailsLabel.text),
        source(model.reservationTitle)       : dest(self, .reservationTitleLabel.text),
        source(model.ticketNumber)           : dest(self, .reservationLabel.text),
        source(model.actionButtonTitle)      : dest(self, .actionButton.ehi_title),
        source(model.shouldHideTicketNumber) : dest(self, .ticketNumberHeightConstraint.isDisabled)
    });
}

# pragma mark - Actions

- (IBAction)didTapActionButton:(id)sender
{
    [self.viewModel performAction];
}

- (IBAction)didTapCancelButton:(id)sender
{
    [self.viewModel dismiss];
}

# pragma mark - EHIViewController

- (EHIModalTransitionStyle)customModalTransitionStyle
{
    return EHIModalTransitionStyleOverlay;
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenExtendRental;
}

@end
