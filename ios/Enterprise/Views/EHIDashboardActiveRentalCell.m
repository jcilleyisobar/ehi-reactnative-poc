//
//  EHIDashboardActiveRentalCell.m
//  Enterprise
//
//  Created by ; on 5/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardActiveRentalCell.h"
#import "EHIDashboardActiveRentalViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHIButton.h"

@interface EHIDashboardActiveRentalCell ()
@property (strong, nonatomic) EHIDashboardActiveRentalViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UIView *locationButtonContainer;
@property (weak  , nonatomic) IBOutlet UIView *vehicleContainer;
@property (weak  , nonatomic) IBOutlet UILabel *vehicleStatTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *vehicleTypeTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *vehicleColorTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *vehiclePlateTitleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *rateMyRideButton;

@property (weak  , nonatomic) IBOutlet UIView *vehicleTypeSectionContainer;
@property (weak  , nonatomic) IBOutlet UIView *vehicleColorSectionContainer;
@property (weak  , nonatomic) IBOutlet UIView *vehiclePlateSectionContainer;

@property (weak  , nonatomic) IBOutlet UILabel *vehicleTypeSubtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *vehicleColorSubtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *vehiclePlateSubtitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *rateMyRideContainer;

@property (weak  , nonatomic) IBOutlet UILabel *rentalInfoTitleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *returnLocationTitleButton;
@property (weak  , nonatomic) IBOutlet UILabel *returnDateTimeTitleLabel;

@property (weak  , nonatomic) IBOutlet EHIButton *returnInstructionsButton;
@property (weak  , nonatomic) IBOutlet EHIButton *getDirectionsButton;
@property (weak  , nonatomic) IBOutlet EHIButton *extendRentalButton;
@property (weak  , nonatomic) IBOutlet EHIButton *findGasButton;

@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *afterHoursHeightConstraint;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *getDirectionsHeightConstraint;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *findGasHeightConstraint;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *airportContainerWidthConstraint;
@end

@implementation EHIDashboardActiveRentalCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardActiveRentalViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.getDirectionsButton.type = EHIButtonTypeDirections;
    self.getDirectionsButton.titleLabel.textAlignment = NSTextAlignmentCenter;
    self.getDirectionsButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    
    self.findGasButton.titleLabel.textAlignment       = NSTextAlignmentCenter;
    self.findGasButton.imageHorizontalAlignment       = UIControlContentHorizontalAlignmentLeft;
    
    self.extendRentalButton.titleLabel.textAlignment = NSTextAlignmentCenter;
    self.extendRentalButton.imageHorizontalAlignment  = UIControlContentHorizontalAlignmentLeft;
    
    self.rateMyRideButton.titleLabel.numberOfLines = 0;
    self.rateMyRideButton.titleLabel.textAlignment = NSTextAlignmentCenter;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardActiveRentalViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.vehicleStatTitle)              : dest(self, .vehicleStatTitleLabel.attributedText),
        source(model.vehicleTypeTitle)              : dest(self, .vehicleTypeTitleLabel.text),
        source(model.vehicleColorTitle)             : dest(self, .vehicleColorTitleLabel.text),
        source(model.vehiclePlateTitle)             : dest(self, .vehiclePlateTitleLabel.text),
        source(model.vehicleTypeSubtitle)           : dest(self, .vehicleTypeSubtitleLabel.text),
        source(model.vehicleColorSubtitle)          : dest(self, .vehicleColorSubtitleLabel.text),
        source(model.vehiclePlateSubtitle)          : dest(self, .vehiclePlateSubtitleLabel.text),
        source(model.rentalInfoTitle)               : dest(self, .rentalInfoTitleLabel.text),
        source(model.returnDateTimeTitle)           : dest(self, .returnDateTimeTitleLabel.text),
        source(model.returnLocationTitle)           : dest(self, .returnLocationTitleButton.ehi_attributedTitle),
        source(model.returnInstructionsButtonTitle) : dest(self, .returnInstructionsButton.ehi_title),
        source(model.getDirectionsButtonTitle)      : dest(self, .getDirectionsButton.ehi_title),
        source(model.extendRentalButtonTitle)       : dest(self, .extendRentalButton.ehi_title),
        source(model.findGasTitle)                  : dest(self, .findGasButton.ehi_title),
        source(model.shouldHideReturnLocation)      : dest(self, .afterHoursHeightConstraint.isDisabled),
        source(model.shouldHideAirport)             : dest(self, .airportContainerWidthConstraint.isDisabled),
        source(model.rateMyRideTitle)               : dest(self, .rateMyRideButton.ehi_title),
    });
    
    [MTRReactor autorun:self action:@selector(invalidateVehicleTypeSection:)];
    [MTRReactor autorun:self action:@selector(invalidateVehicleColorSection:)];
    [MTRReactor autorun:self action:@selector(invalidateVehiclePlateSection:)];
    [MTRReactor autorun:self action:@selector(invalidateVehicleSection:)];
    [MTRReactor autorun:self action:@selector(invalidateRateMyRide:)];
    [MTRReactor autorun:self action:@selector(invalidateDirectionsButtons:)];
}

- (void)invalidateVehicleTypeSection:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.shouldHideVehicleName ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.vehicleTypeSectionContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

- (void)invalidateVehicleColorSection:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.shouldHideVehicleColor ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.vehicleColorSectionContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

- (void)invalidateVehiclePlateSection:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.shouldHideVehiclePlate ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.vehiclePlateSectionContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

- (void)invalidateVehicleSection:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.shouldHideCurrentRentalSection ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.vehicleContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

- (void)invalidateRateMyRide:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.shouldHideRateMyRide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.rateMyRideContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

- (void)invalidateDirectionsButtons:(MTRComputation *)computation
{
    BOOL shouldHide = self.viewModel.shouldHideGetDirections;
    
    self.getDirectionsHeightConstraint.isDisabled = shouldHide;
    self.findGasHeightConstraint.isDisabled = shouldHide;
}

# pragma mark - Actions

- (IBAction)didTapReturnInstructionsButton:(id)sender
{
    [self.viewModel showReturnInstructions];
}

- (IBAction)didTapGetDirectionsButton:(id)sender
{
    [self.viewModel showDirections];
}

- (IBAction)didTapExtendRentalButton:(id)sender
{
    [self.viewModel showExtendRentalDialogue];
}

- (IBAction)didTapFindGasButton:(id)sender
{
    [self.viewModel showGasStations];
}

- (IBAction)didTapLocationDetailsButton:(id)sender
{
    [self.viewModel showLocationDetails];
}

- (IBAction)didTapRateMyRideButton:(id)sender
{
    [self.viewModel showRateMyRide];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGRect frame = [self.contentContainer convertRect:self.contentContainer.bounds toView:self.contentView];
    
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(frame)
    };
}

@end
