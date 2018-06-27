//
//  EHIProfileEditDriverLicenseViewController.m
//  Enterprise
//
//  Created by Alex Koller on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDriverLicenseEditViewController.h"
#import "EHIDriverLicenseEditViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIFormFieldCell.h"
#import "EHIActionButton.h"
#import "EHIActivityIndicator.h"
#import "EHIRequiredInfoCell.h"

@interface EHIDriverLicenseEditViewController () <EHIFormFieldCellActions>
@property (strong, nonatomic) EHIDriverLicenseEditViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActionButton *saveChangesButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@end

@implementation EHIDriverLicenseEditViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDriverLicenseEditViewModel new];
    }
    return self;
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    self.viewModel.editHandler = attributes.handler;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHIDriverLicenseEditCellSectionRequiredInfo) : EHIRequiredInfoCell.class,
        @(EHIDriverLicenseEditCellSectionForm)         : EHIFormFieldCell.class,
    }];
    
    self.collectionView.sections.isDynamicallySized = YES;
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDriverLicenseEditViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateSections:)];
    
    EHIListDataSourceSection *required = self.collectionView.sections[EHIDriverLicenseEditCellSectionRequiredInfo];
    
    model.bind.map(@{
        source(model.title)           : dest(self, .title),
        source(model.isLoading)       : dest(self, .loadingIndicator.isAnimating),
        source(model.saveButtonTitle) : dest(self, .saveChangesButton.ehi_title),
        source(model.requiredModel)   : dest(required, .model),
        source(model.invalidForm)     : ^(NSNumber *isDisabled) {
                                            self.saveChangesButton.isFauxDisabled = isDisabled.boolValue;
                                            [self invalidateViewBelowSafeArea:isDisabled.boolValue];
                                        },
    });
}

- (void)invalidateSections:(MTRComputation *)computation
{
    NSArray *viewModels = self.viewModel.formViewModels;
    
    [self.collectionView flushWithCompletion:^{
        self.collectionView.sections[EHIDriverLicenseEditCellSectionForm].models = viewModels;
        [self.collectionView ehi_invalidateLayoutAnimated:!computation.isFirstRun];
    }];
}

# pragma mark - EHIFormFieldCellActions

- (void)didResignFirstResponderForCell:(EHIFormFieldCell *)sender
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    
    [self.collectionView ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:indexPath];
}

# pragma mark - Actions

- (IBAction)didTapSaveChangesButton:(id)sender
{
    [self.viewModel saveChanges];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

- (UIButton *)keyboardSupportedActionButton
{
    return self.saveChangesButton;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenProfile state:EHIScreenLicenseEdit];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenLicenseEdit;
}

@end
