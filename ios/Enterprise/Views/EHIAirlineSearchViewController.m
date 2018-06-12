//
//  EHIAirlineSearchViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 6/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAirlineSearchViewController.h"
#import "EHIAirlineSearchViewModel.h"
#import "EHITextField.h"
#import "EHIListCollectionView.h"
#import "EHIFlightDetailsViewController.h"
#import "EHIAirlineSearchResultCell.h"

@interface EHIAirlineSearchViewController () <EHIListCollectionViewDelegate, UITextFieldDelegate>
@property (strong, nonatomic) EHIAirlineSearchViewModel *viewModel;
@property (strong, nonatomic) EHITextField *searchField;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIAirlineSearchViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIAirlineSearchViewModel new];
    }

    return self;
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    self.viewModel.handler = attributes.handler;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self configureCollectionView];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    // transition should be complete, so we can show the search field
    [self.searchField setHidden:NO];

    dispatch_after_seconds(0.7, ^{
        [self.searchField becomeFirstResponder];
    });
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    // clean up the nav bar search field before animating away
    [self.searchField resignFirstResponder];
}

- (void)configureCollectionView
{
    self.collectionView.section.klass = EHIAirlineSearchResultCell.class;
    self.collectionView.section.isDynamicallySized = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIAirlineSearchViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSearchModels:)];
}

- (void)invalidateSearchModels:(MTRComputation *)computation
{
    NSArray *models = self.viewModel.resultModels;
    
    [self.collectionView performBatchUpdates:^{
        self.collectionView.section.models = models;
    } completion:nil];
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectAirlineAtIndexPath:indexPath];
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
    NSString *query = [textField.text stringByReplacingCharactersInRange:range withString:string];
    [self.viewModel filterAirlineWithQuery:query];
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    // hijack text field's normal updating cycle, the text is driven by the vm
    return YES;
}

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    // clear results
    [self.viewModel filterAirlineWithQuery:nil];
    
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    return NO;
}

# pragma mark - Keyboard Events

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

# pragma mark - EHIViewController

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.titleView = self.searchField;
    item.rightBarButtonItems = @[
        [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil]
    ];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationAirlineSearch;
}

@end
