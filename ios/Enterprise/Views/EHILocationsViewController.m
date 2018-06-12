//
//  EHILocationsViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 1/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationsViewController.h"
#import "EHILocationsMapViewController.h"
#import "EHILocationsViewModel.h"
#import "EHISectionHeader.h"
#import "EHILocationNearbyCell.h"
#import "EHILocationEmptyQueryCell.h"
#import "EHILocationCell.h"
#import "EHILocationManager.h"
#import "EHIDashboardViewController.h"
#import "EHIListCollectionView.h"
#import "EHIActivityIndicator.h"
#import "EHITextField.h"
#import "EHIBarButtonItem.h"
#import "EHIReservationRouter.h"

@interface EHILocationsViewController () <EHIListCollectionViewDelegate, EHISectionHeaderActions, UITextFieldDelegate>
@property (strong, nonatomic) EHILocationsViewModel *viewModel;
@property (strong, nonatomic) EHITextField *searchField;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
// Contains all of the loading interface elements
@property (weak  , nonatomic) IBOutlet UIView *loadingContainer;
// Loading indicator that animates while query is loading
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
// Image that shows up when a query is loading
@property (weak  , nonatomic) IBOutlet UIImageView *loadingImageView;
// Constraint from view container to bottom of view (Aids in keyboard handling)
@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *containerToViewBottomConstraint;
@end

@implementation EHILocationsViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHILocationsViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self configureCollectionView];
    
    dispatch_after_seconds(0.7, ^{
        [self.searchField becomeFirstResponder];
    });
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
   
    // transition should be complete, so we can show the search field
    [self.searchField setHidden:NO];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
  
    // clean up the nav bar search field before animating away
    [self.searchField resignFirstResponder];
}

//
// Helpers
//

- (void)configureCollectionView
{
    // configure nearby section
    EHIListDataSourceSection *nearby = self.collectionView.sections[EHILocationSectionNearby];
    nearby.klass = EHILocationNearbyCell.class;
    
    // configure empty query section
    EHIListDataSourceSection *empty = self.collectionView.sections[EHILocationSectionEmptyQuery];
    empty.klass = EHILocationEmptyQueryCell.class;
    empty.isDynamicallySized = YES;
    
    // configure all the location cell sections
    for(EHILocationSection index=EHILocationSectionFavorites ; index<=EHILocationSectionCity ; index++) {
        EHIListDataSourceSection *section = self.collectionView.sections[index];
        section.klass = EHILocationCell.class;
        section.isDynamicallySized = YES;
    }
    
    // update the headers for each section
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        EHISectionHeaderModel *header = [self.viewModel headerForSection:section.index];
        // only configure the header if we have a model
        if(header) {
            section.header.klass = EHISectionHeader.class;
            section.header.model = header;
        }
    }
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.searchField.accessibilityIdentifier = EHILocationsSearchInputKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationsViewModel *)model
{
    [super registerReactions:model];
  
    // bind the models for each section
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        [MTRReactor autorun:^(MTRComputation *computation) {
            section.models = [model modelsForSection:section.index];
        }];
    }
  
    // bind the remaining ui elements
    model.bind.map(@{
        source(model.query) : dest(self, .searchField.text),
        source(model.searchPlaceholder) : dest(self, .searchField.placeholder),
        source(model.isLoading) : ^(NSNumber *isLoading){
            self.loadingContainer.alpha = isLoading.boolValue;
            (isLoading.boolValue) ? [self.loadingIndicator startAnimating] : [self.loadingIndicator stopAnimating];
        },
    });
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    // notify the view model of this selection
    [self.viewModel selectIndexPath:indexPath];
}

# pragma mark - Search Field

- (EHITextField *)searchField
{
    if(_searchField) {
        return _searchField;
    }
   
    CGRect searchFrame = CGRectInsetWithOffset(self.navigationController.navigationBar.bounds, EHISearchFieldInsets);
    
    _searchField = [[EHITextField alloc] initWithFrame:searchFrame];
    _searchField.borderType = EHITextFieldBorderNone;
    _searchField.backgroundColor = [UIColor whiteColor];
    _searchField.clearButtonMode = UITextFieldViewModeWhileEditing;
    _searchField.returnKeyType = UIReturnKeyDone;
    _searchField.delegate = self;
    
    // default to hidden to accomodate transition; show the search field when the view finishes
    // appearing in -viewDidAppear
    _searchField.hidden = YES;
    
    return _searchField;
}

//
// UITextFieldDelegate
//

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    // update the view model with new query
    self.viewModel.query = [textField.text stringByReplacingCharactersInRange:range withString:string];
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    // hijack text field's normal updating cycle, the text is driven by the vm
    return NO;
}

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    // clear results
    self.viewModel.query = nil;
    
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    return NO;
}

# pragma mark - EHISectionHeaderActions

- (void)sectionHeaderDidTapActionButton:(EHISectionHeader *)sectionHeader
{
    [self.viewModel clearRecentActivity];
}

# pragma mark - Keyboard Events

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

# pragma mark - Transitions

- (BOOL)executesCustomAnimationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    return [controller isKindOfClass:[EHIDashboardViewController class]];
}

- (NSArray *)animationsForTransitionToViewController:(EHIViewController *)controller isEntering:(BOOL)isEntering
{
    // hide the search field before transitioning
    self.searchField.hidden = YES;
    
    NSArray *animations = [super animationsForTransitionToViewController:controller isEntering:isEntering];
    
    animations = animations.concat(@[
        EHINavigationAnimation.target(self.view)
            .alpha(1.0)
            .delay(0.7).duration(0.35),
    ]);
   
    if(!isEntering) {
        animations = animations.concat(@[
            EHINavigationAnimation.target(self.navigationController.navigationBar)
                .autoreversingNavigationBar
                .delay(0.1)
        ]);
    }
    
    return animations;
}

# pragma mark - EHIViewController

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.titleView = self.searchField;
    item.rightBarButtonItems = @[
        [EHIBarButtonItem flexibleSpace]
    ];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenLocations state:EHIScreenLocations];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventLocations;
    }];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenLocations;
}

@end
