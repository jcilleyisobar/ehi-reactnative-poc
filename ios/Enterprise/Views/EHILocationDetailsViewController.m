//
//  EHILocationDetailsViewController.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 12.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDetailsViewController.h"
#import "EHILocationDetailsViewModel.h"
#import "EHILocationDetailsMapCell.h"
#import "EHILocationDetailsInfoCell.h"
#import "EHILocationDetailsHoursCell.h"
#import "EHILocationDetailsPickupCell.h"
#import "EHILocationDetailsPolicyCell.h"
#import "EHILocationDetailsConflictCell.h"
#import "EHISectionHeader.h"
#import "EHIListCollectionView.h"
#import "EHIActionButton.h"
#import "EHIReservationRouter.h"
#import "EHIActivityIndicator.h"
#import "EHIRestorableConstraint.h"

@interface EHILocationDetailsViewController () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHILocationDetailsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet EHIActionButton *actionButton;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *collectionBottomConstraint;
@end

@implementation EHILocationDetailsViewController

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
 
    [self.collectionView.sections construct:@{
        @(EHILocationDetailsSectionMap)       : [EHILocationDetailsMapCell class],
        @(EHILocationDetailsSectionConflicts) : [EHILocationDetailsConflictCell class],
        @(EHILocationDetailsSectionHours)     : [EHILocationDetailsHoursCell class],
        @(EHILocationDetailsSectionPolicies)  : [EHILocationDetailsPolicyCell class],
        @(EHILocationDetailsSectionInfo)      : [EHILocationDetailsInfoCell class],
        @(EHILocationDetailsSectionPickup)    : [EHILocationDetailsPickupCell class],
    }];

    self.collectionView.sections[EHILocationDetailsSectionConflicts].isDynamicallySized = YES;
    self.collectionView.sections[EHILocationDetailsSectionInfo].isDynamicallySized = YES;
    self.collectionView.sections[EHILocationDetailsSectionPickup].isDynamicallySized = YES;

    // apply the shared header to every section
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.header.klass = [EHISectionHeader class];
        section.header.model = [self.viewModel headerForSection:section.index];
    }
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationDetailsViewModel *)model
{
    [super registerReactions:model];

    // batch bind the location to a bunch of sections
    [MTRReactor autorun:^(MTRComputation *computation) {
        EHILocation *location = model.location;
  
        if (location.policies.count > 0) {
            self.collectionView.sections[EHILocationDetailsSectionMap].model  = location;
            self.collectionView.sections[EHILocationDetailsSectionInfo].model = location;
        }
    }];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        self.collectionView.sections[EHILocationDetailsSectionConflicts].model = model.conflictsModel;
    }];
   
    [MTRReactor autorun:^(MTRComputation *computation) {
        NSArray *hours = model.hours;
        NSArray *policies = model.policies;
        
        if(hours && policies) {
            [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
                self.collectionView.sections[EHILocationDetailsSectionHours].models    = hours;
                self.collectionView.sections[EHILocationDetailsSectionPolicies].models = policies;
                self.collectionView.sections[EHILocationDetailsSectionPickup].model    = model.pickupLocation;
            } completion:nil];
        }
    }];

    [MTRReactor autorun:^(MTRComputation *computation) {
        BOOL hidesActionButton = self.viewModel.disablesSelection;
        
        self.actionButton.hidden = hidesActionButton;
        self.collectionBottomConstraint.isDisabled = hidesActionButton;
    }];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .title),
        source(model.actionTitle) : dest(self, .actionButton.ehi_title),
        source(model.isLoading)   : dest(self, .loadingIndicator.isAnimating)
    });
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel showPolicyAtIndexPath:indexPath];
}

//
// UICollectionViewDelegateFlowLayout
//

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout insetForSectionAtIndex:(NSInteger)section
{
    if(section == EHILocationDetailsSectionHours) {
        return (UIEdgeInsets){ .top = 15.0f, .bottom = 15.0f };
    }
    
    return UIEdgeInsetsZero;
}

# pragma mark - User interaction

- (IBAction)didTapSelectLocation:(id)sender
{
    // push the reservation flow with our location
    [self.viewModel selectLocation];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenLocations state:EHIScreenLocationDetails];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenLocationDetails;
}

@end
