//
//  EHIEnrollmentStepTwoViewController.mViewController
//  Enterprise
//
//  Created by Rafael Machado on 8/6/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepTwoViewController.h"
#import "EHIEnrollmentStepTwoViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIActivityIndicator.h"
#import "EHIEnrollmentStepHeaderCell.h"
#import "EHIFormFieldCell.h"
#import "EHIEnrollmentWarningCell.h"
#import "EHIEnrollmentStepTwoMatchView.h"
#import "EHIRequiredInfoCell.h"

@interface EHIEnrollmentStepTwoViewController () <EHIEnrollmentStepTwoMatchActions>
@property (strong, nonatomic) EHIEnrollmentStepTwoViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet EHIEnrollmentStepTwoMatchView *matchOverlayView;
@end

@implementation EHIEnrollmentStepTwoViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIEnrollmentStepTwoViewModel new];
    }

    return self;
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHIEnrollmentStepTwoSectionWarning)      : EHIEnrollmentWarningCell.class,
        @(EHIEnrollmentStepTwoSectionHeader)       : EHIEnrollmentStepHeaderCell.class,
        @(EHIEnrollmentStepTwoSectionRequiredInfo) : EHIRequiredInfoCell.class,
        @(EHIEnrollmentStepTwoSectionAddress)      : EHIFormFieldCell.class,
        @(EHIEnrollmentStepTwoSectionButton)       : EHIFormFieldCell.class
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
    }
    
    EHIViewModel *matchViewModel = self.viewModel.matchViewModel;
    if(matchViewModel) {
        self.matchOverlayView.viewModel = matchViewModel;
    }
    
    self.matchOverlayView.hidden = matchViewModel == nil;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIEnrollmentStepTwoViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *header   = self.collectionView.sections[EHIEnrollmentStepTwoSectionHeader];
    EHIListDataSourceSection *required = self.collectionView.sections[EHIEnrollmentStepTwoSectionRequiredInfo];
    EHIListDataSourceSection *address  = self.collectionView.sections[EHIEnrollmentStepTwoSectionAddress];
    EHIListDataSourceSection *button   = self.collectionView.sections[EHIEnrollmentStepTwoSectionButton];
    
    [MTRReactor autorun:self action:@selector(invalidateForm:)];
    [MTRReactor autorun:self action:@selector(invalidateWarning:)];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .title),
        source(model.isLoading)         : dest(self, .loadingIndicator.isAnimating),
        source(model.headerModel)       : dest(header, .model),
        source(model.requiredInfoModel) : dest(required, .model),
        source(model.formModels)        : dest(address, .models),
        source(model.buttonModel)       : dest(button, .model),
    });
}

- (void)invalidateForm:(MTRComputation *)computation
{
    NSArray *forms = self.viewModel.formModels;
    
    __weak typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHIEnrollmentStepTwoSectionAddress].models = forms;
    } completion:nil];
}

- (void)invalidateWarning:(MTRComputation *)computation
{
    NSString *warning = self.viewModel.warning;
    
    __weak typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHIEnrollmentStepTwoSectionWarning].model = warning;
    } completion:^(BOOL completed){
        if(warning.length > 0) {
            [welf.collectionView reloadData];
            NSIndexPath *indexPath = [NSIndexPath indexPathForItem:0 inSection:EHIEnrollmentStepTwoSectionWarning];
            [welf.collectionView scrollToItemAtIndexPath:indexPath atScrollPosition:UICollectionViewScrollPositionTop animated:YES];
        }
    }];
}

# pragma mark - EHIEnrollmentStepTwoMatchActions

- (void)enrollmentStepTwoMatchDidTapChange
{
    [self.viewModel changeAddress];
    
    [UIView animateWithDuration:0.3 animations:^{
        self.matchOverlayView.transform = CGAffineTransformMakeTranslation(0, CGRectGetHeight(self.view.frame));
    }];
}

- (void)enrollmentStepTwoMatchDidTapKeep
{
    [self.viewModel keepAddress];
}

# pragma mark - EHIFormFieldCellActions

- (void)didResignFirstResponderForCell:(EHIFormFieldCell *)sender
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    
    [self.collectionView ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:indexPath];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenEnrollmentStepTwo state:self.viewModel.currentState];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventEnrollmentStepTwo;
    }];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenEnrollmentStepTwo;
}

@end
