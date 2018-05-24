//
//  EHIConfirmationActionsCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/21/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationActionsCell.h"
#import "EHIConfirmationActionsViewModel.h"
#import "EHIActivityButton.h"
#import "EHIActionButton.h"

@interface EHIConfirmationActionsCell ()
@property (strong, nonatomic) EHIConfirmationActionsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *topDividerLineView;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UIView *defaultContainer;
@property (weak  , nonatomic) IBOutlet EHIActivityButton *modifyButton;
@property (weak  , nonatomic) IBOutlet EHIActivityButton *cancelButton;
@property (weak  , nonatomic) IBOutlet EHIActionButton *returnToDashboardButton;

@end

@implementation EHIConfirmationActionsCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIConfirmationActionsViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
   
    @[self.modifyButton, self.cancelButton].flatten.each(^(EHIActivityButton *button) {
        button.isDisabledWhileLoading = YES;
        button.indicatorType = EHIActivityIndicatorTypeGreen;
        
        button.showsBorder = YES;
        button.borderColor = [UIColor ehi_greenColor];
        
        // update the state-based background colors
        [button setBackgroundColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [button setBackgroundColor:[UIColor ehi_grayColor1] forState:UIControlStateDisabled];
    });
    self.returnToDashboardButton.ehi_title = self.viewModel.returnHomeTitle;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.modifyButton.ehi_title = self.viewModel.modifyTitle;
    self.cancelButton.ehi_title = self.viewModel.cancelReservationTitle;
    self.returnToDashboardButton.accessibilityIdentifier = EHIConfirmationReturnDashboardKey;
    self.modifyButton.accessibilityIdentifier = EHIConfirmationModifyKey;
    self.cancelButton.accessibilityIdentifier = EHIConfirmationCancelKey; 
}

- (void)registerReactions:(EHIConfirmationActionsViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
                     source(model.modifyTitle)            : dest(self, .modifyButton.ehi_title),
                     source(model.cancelReservationTitle) : dest(self, .cancelButton.ehi_title),
                     source(model.returnHomeTitle)        : dest(self, .returnToDashboardButton.ehi_title)
                     });
    
    [MTRReactor autorun:self action:@selector(invalidateButtonsState:)];
    [MTRReactor autorun:self action:@selector(invalidateIsLoading:)];
}

- (void)invalidateButtonsState:(MTRComputation *)computation
{
    BOOL disableModify = self.viewModel.disableModify;
    self.modifyButton.isFauxDisabled = disableModify;

    BOOL disableCancel = self.viewModel.disableCancel;
    self.cancelButton.isFauxDisabled = disableCancel;
}

- (void)invalidateIsLoading:(MTRComputation *)computation
{
    BOOL isLoadingModify = self.viewModel.isModifyLoading;
    BOOL isLoadingCancel = self.viewModel.isCancelationLoading;
    BOOL isLoading       = isLoadingModify || isLoadingCancel;
    
    self.modifyButton.isLoading = isLoadingModify;
    self.modifyButton.enabled   = !isLoading;
    
    self.cancelButton.isLoading = isLoadingCancel;
    self.cancelButton.enabled   = !isLoading;
    
    self.returnToDashboardButton.enabled   = !isLoading;
}

# pragma mark - Actions

- (IBAction)didTapReturnHomeButton:(EHIButton *)sender
{
    [self ehi_performAction:@selector(confirmationCellDidTapReturnToDashboard:) withSender:self];
    
    [self.viewModel returnToDashboard];
}

- (IBAction)didTapModifyButton:(EHIButton *)button
{
    [self.viewModel modifyReservation];
}

- (IBAction)didTapCancelReservationButton:(EHIButton *)button
{
    [self.viewModel cancelReservation];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = self.containerView.bounds.size.height + 2 * EHIMediumPadding
    };
}

@end
