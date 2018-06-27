//
//  EHIMemberInfoEditViewController.m
//  Enterprise
//
//  Created by Alex Koller on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMemberInfoEditViewController.h"
#import "EHIMemberInfoEditViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIActionButton.h"
#import "EHIActivityIndicator.h"
#import "EHIFormFieldCell.h"
#import "EHIRequiredInfoCell.h"
#import "EHIRequiredInfoFootnoteCell.h"

@interface EHIMemberInfoEditViewController () <EHIFormFieldCellActions, EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIMemberInfoEditViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActionButton *saveChangesButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@end

@implementation EHIMemberInfoEditViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIMemberInfoEditViewModel new];
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
        @(EHIMemberInfoEditSectionRequiredInfo)         : EHIRequiredInfoCell.class,
        @(EHIMemberInfoEditSectionForm)                 : EHIFormFieldCell.class,
        @(EHIMemberInfoEditSectionRequiredInfoFootnote) : EHIRequiredInfoFootnoteCell.class,
    }];
    
    self.collectionView.sections.isDynamicallySized = YES;
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIMemberInfoEditViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateRows:)];
    [MTRReactor autorun:self action:@selector(invalidateConstraints:)];
    
    EHIListDataSourceSection *required = self.collectionView.sections[EHIMemberInfoEditSectionRequiredInfo];
    EHIListDataSourceSection *footnote = self.collectionView.sections[EHIMemberInfoEditSectionRequiredInfoFootnote];
    
    model.bind.map(@{
        source(model.title)           : dest(self, .title),
        source(model.isLoading)       : dest(self, .loadingIndicator.isAnimating),
        source(model.saveButtonTitle) : dest(self, .saveChangesButton.ehi_title),
        source(model.requiredModel)   : dest(required, .model),
        source(model.footnoteModel)   : dest(footnote, .model),
        source(model.invalidForm)     : ^(NSNumber *isDisabled) {
                                            self.saveChangesButton.isFauxDisabled = isDisabled.boolValue;
                                            [self invalidateViewBelowSafeArea:isDisabled.boolValue];
                                        },
    });
}

- (void)invalidateRows:(MTRComputation *)computation
{
    NSArray *viewModels = self.viewModel.formViewModels;
    __weak typeof(self) welf = self;
    [self.collectionView flushWithCompletion:^{
        welf.collectionView.sections[EHIMemberInfoEditSectionForm].models = viewModels;
        [welf.collectionView ehi_invalidateLayoutAnimated:!computation.isFirstRun];
    }];
}

- (void)invalidateConstraints:(MTRComputation *)computation
{
    BOOL shouldInvalidate = self.viewModel.shouldInvalidateConstraints;
    
    if (shouldInvalidate) {
        [self.collectionView ehi_invalidateLayoutAnimated:YES];
        
        self.viewModel.shouldInvalidateConstraints = NO;
    }
}

# pragma mark - EHIFormFieldCellActions

- (void)didResignFirstResponderForCell:(EHIFormFieldCell *)sender
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    
    [self.collectionView ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:indexPath];
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel didSelectItemAtIndex:indexPath.item];
}

# pragma mark - Actions

- (IBAction)didTapView:(id)sender
{
    [self.view endEditing:YES];
}

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
    [EHIAnalytics changeScreen:EHIScreenProfile state:EHIScreenMemberInfoEdit];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenMemberInfoEdit;
}

@end
