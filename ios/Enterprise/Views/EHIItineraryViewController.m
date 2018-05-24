//
//  EHIItineraryViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIItineraryViewController.h"
#import "EHIItineraryViewModel.h"
#import "EHIItineraryPickupLocationView.h"
#import "EHIItineraryReturnLocationView.h"
#import "EHIItineraryUserInfoView.h"
#import "EHIReservationEditableScheduleView.h"
#import "EHILabel.h"
#import "EHIRestorableConstraint.h"
#import "EHISectionHeader.h"
#import "EHIActionButton.h"
#import "NSNotificationCenter+Utility.h"
#import "EHIActivityIndicator.h"

typedef NS_ENUM(NSInteger, EHIReservationSection) {
    EHIReservationSectionLocation,
    EHIReservationSectionSchedule,
    EHIReservationSectionAge,
    EHIReservationSectionDetails
};

@interface EHIItineraryViewController ()
@property (strong, nonatomic) EHIItineraryViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet UIView *returnSectionHeaderContainer;
@property (weak, nonatomic) IBOutlet UILabel *pickupHeaderTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *returnHeaderTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pickupSectionHeaderLabel;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *returnHeaderConstraint;
@property (weak, nonatomic) IBOutlet EHIItineraryPickupLocationView *pickupLocationSelectionView;
@property (weak, nonatomic) IBOutlet EHIItineraryReturnLocationView *returnLocationSelectionView;
@property (weak, nonatomic) IBOutlet EHIReservationEditableScheduleView *pickupDateSelectionView;
@property (weak, nonatomic) IBOutlet EHIReservationEditableScheduleView *returnDateSelectionView;
@property (weak, nonatomic) IBOutlet EHIItineraryUserInfoView *userInformationView;
@property (weak, nonatomic) IBOutlet EHIActionButton *continueButton;
@property (weak, nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak, nonatomic) IBOutlet EHIActivityIndicator *circleLoadingIndicator;
@end

@implementation EHIItineraryViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIItineraryViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // use the enterprise logo loader
    self.loadingIndicator.type = EHIActivityIndicatorTypeELoader;
    self.circleLoadingIndicator.type = EHIActivityIndicatorTypeGreen;
    
    // distinguish between the pickup and return date/time selection views
    self.pickupDateSelectionView.type   = EHIReservationScheduleViewTypePickup;
    self.pickupDateSelectionView.layout = EHIReservationScheduleViewLayoutItinerary;
    self.returnDateSelectionView.type   = EHIReservationScheduleViewTypeReturn;
    self.returnDateSelectionView.layout = EHIReservationScheduleViewLayoutItinerary;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.continueButton.accessibilityIdentifier = EHIItineraryContinueKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIItineraryViewModel *)model
{
    [super registerReactions:model];
  
    [MTRReactor autorun:self action:@selector(invalidateIsLoading:)];
    [MTRReactor autorun:self action:@selector(invalidateSectionHeaders:)];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .title),
        source(model.isReadyToContinue) : dest(self, .continueButton.enabled),
        source(model.returnHeaderTitle) : dest(self, .returnHeaderTitleLabel.text),
        source(model.actionButtonTitle) : dest(self, .continueButton.ehi_title),
        source(model.pickupHeaderTitle) : dest(self, .pickupHeaderTitleLabel.text)
    });
}

- (void)invalidateSectionHeaders:(MTRComputation *)computation
{
    BOOL isOneWay = self.viewModel.isOneWay;
    
    self.returnHeaderConstraint.constant = isOneWay ? self.returnHeaderConstraint.restorableValue : 0.0f;
    
    UIView.animate(!computation.isFirstRun).duration(0.15f).transform(^{
        [self.view layoutIfNeeded];
        self.returnSectionHeaderContainer.alpha = isOneWay ? 1.0f : 0.0f;
    }).start(nil);
}

- (void)invalidateIsLoading:(MTRComputation *)computation
{
    BOOL isLoading = self.viewModel.isLoading;
    BOOL isInitiating = self.viewModel.isInitiating;
    
    UIView.animate(YES).duration(0.15).transform(^{
        self.circleLoadingIndicator.isAnimating = isLoading;
        self.loadingIndicator.isAnimating = isInitiating;
        self.continueButton.enabled = !isInitiating && !isLoading;
    }).start(nil);
}

# pragma mark - View Actions

- (IBAction)didTapContinueButton:(id)sender
{
    [self.userInformationView resignFirstResponder];
    [self.viewModel commitItinerary];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationItinerary state:EHIScreenReservationItinerary];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationItinerary;
}

@end
