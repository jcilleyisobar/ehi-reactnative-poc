//
//  EHIReservationEditableScheduleView.m
//  Enterprise
//
//  Created by Michael Place on 3/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationEditableScheduleView.h"
#import "EHIReservationEditableScheduleViewModel.h"
#import "EHIReservationRouter.h"
#import "EHILabel.h"
#import "EHICaptionedButton.h"

@interface EHIReservationEditableScheduleView ()
@property (strong, nonatomic) EHIReservationEditableScheduleViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHICaptionedButton *dateSelectionButton;
@property (weak  , nonatomic) IBOutlet EHIButton *timeSelectionButton;
@property (weak  , nonatomic) IBOutlet UIView *divider;
@end

@implementation EHIReservationEditableScheduleView

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        // initialize view model
        self.viewModel = [EHIReservationEditableScheduleViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // style the date and time selection buttons
    for(EHIButton *button in @[ self.dateSelectionButton, self.timeSelectionButton ]) {
        button.tintColor      = [UIColor whiteColor];
        button.ehi_titleColor = [UIColor whiteColor];
    }
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationEditableScheduleViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(updateDateSelectionState:)];
    
    model.bind.map(@{
        source(model.shouldEnableDateSelection) : dest(self, .dateSelectionButton.enabled),
        source(model.shouldEnableTimeSelection) : dest(self, .timeSelectionButton.enabled),
        source(model.dateSelectionButtonTitle)  : dest(self, .dateSelectionButton.ehi_title),
        source(model.timeSelectionButtonTitle)  : dest(self, .timeSelectionButton.ehi_title),
    });
}

- (void)updateDateSelectionState:(MTRComputation *)computation
{
    EHIReservationScheduleSelectionState state = self.viewModel.state;
  
    self.dateSelectionButton.hidden = [self dateSelectionButtonHiddenForState:state];
    self.timeSelectionButton.hidden = [self timeSelectionButtonHiddenForState:state];
}

- (BOOL)dateSelectionButtonHiddenForState:(EHIReservationScheduleSelectionState)state
{
    return state == EHIReservationScheduleSelectionStateDateSelected
        || state == EHIReservationScheduleSelectionStateTimeSelected;
}

- (BOOL)timeSelectionButtonHiddenForState:(EHIReservationScheduleSelectionState)state
{
    return state == EHIReservationScheduleSelectionStateTimeSelected;
}

# pragma mark - Interface Actions

- (IBAction)didTapDateView:(id)sender
{
    // prevents the tap gesture from being triggered while the button overtop it is disabled
    if(!self.dateSelectionButton.enabled) {
        return;
    }
    
    // determine current step based on our type (pickup or return)
    EHIReservationSchedulingStep currentStep = (self.type == EHIReservationScheduleViewTypePickup)
        ? EHIReservationSchedulingStepPickupDate
        : EHIReservationSchedulingStepReturnDate;
    
    // let the view model navigate
    [self.viewModel startSelectionWithStep:currentStep];
}

- (IBAction)didTapTimeView:(id)sender
{
    // prevents the tap gesture from being triggered while the button overtop it is disabled
    if(!self.timeSelectionButton.enabled) {
        return;
    }
    
    // determine current step based on our type (pickup or return)
    EHIReservationSchedulingStep currentStep = (self.type == EHIReservationScheduleViewTypePickup)
        ? EHIReservationSchedulingStepPickupTime
        : EHIReservationSchedulingStepReturnTime;
    
    // let the view model navigate
    [self.viewModel startSelectionWithStep:currentStep];
}

@end
