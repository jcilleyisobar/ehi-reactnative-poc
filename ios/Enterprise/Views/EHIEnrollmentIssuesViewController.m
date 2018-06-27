//
//  EHIEnrollmentIssuesViewController.mViewController
//  Enterprise
//
//  Created by Rafael Machado on 8/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentIssuesViewController.h"
#import "EHIEnrollmentIssuesViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIActivityIndicator.h"
#import "EHIEnrollmentWarningCell.h"
#import "EHIFormFieldCell.h"
#import "EHIEnrollmentPasswordCell.h"
#import "EHIProfilePasswordRuleCell.h"
#import "EHIReviewSectionHeader.h"
#import "EHIBarButtonItem.h"
#import "EHIEnrollmentIssuesHeaderCell.h"
#import "EHIRequiredInfoCell.h"

@interface EHIEnrollmentIssuesViewController () <EHIFormFieldCellActions, EHIEnrollmentPasswordCellActions>
@property (strong, nonatomic) EHIEnrollmentIssuesViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingActivity;
@end

@implementation EHIEnrollmentIssuesViewController

# pragma mark - EHIViewController

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeExit target:self action:@selector(didTapExitButton:)];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHIEnrollmentIssuesSectionWarning)              : EHIEnrollmentWarningCell.class,
        @(EHIEnrollmentIssuesSectionHeader)               : EHIEnrollmentIssuesHeaderCell.class,
        @(EHIEnrollmentIssuesSectionRequiredInfo)         : EHIRequiredInfoCell.class,
        @(EHIEnrollmentIssuesSectionProfile)              : EHIFormFieldCell.class,
        @(EHIEnrollmentIssuesSectionAddress)              : EHIFormFieldCell.class,
        @(EHIEnrollmentIssuesSectionPhone)                : EHIFormFieldCell.class,
        @(EHIEnrollmentIssuesSectionEmail)                : EHIFormFieldCell.class,
        @(EHIEnrollmentIssuesSectionPasswordCreate)       : EHIEnrollmentPasswordCell.class,
        @(EHIEnrollmentIssuesSectionPasswordRules)        : EHIProfilePasswordRuleCell.class,
        @(EHIEnrollmentIssuesSectionPasswordConfirmation) : EHIEnrollmentPasswordCell.class,
        @(EHIEnrollmentIssuesSectionJoin)                 : EHIFormFieldCell.class
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
        
        EHIViewModel *model = [self.viewModel headerForSection:section.index];
        if(model) {
            section.header.isDynamicallySized = YES;
            section.header.klass = EHIReviewSectionHeader.class;
            section.header.model = model;
        }
    }
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIEnrollmentIssuesViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateWarning:)];
    
    EHIListDataSourceSection *header   = self.collectionView.sections[EHIEnrollmentIssuesSectionHeader];
    EHIListDataSourceSection *required = self.collectionView.sections[EHIEnrollmentIssuesSectionRequiredInfo];
    EHIListDataSourceSection *profile  = self.collectionView.sections[EHIEnrollmentIssuesSectionProfile];
    EHIListDataSourceSection *address  = self.collectionView.sections[EHIEnrollmentIssuesSectionAddress];
    EHIListDataSourceSection *phone    = self.collectionView.sections[EHIEnrollmentIssuesSectionPhone];
    EHIListDataSourceSection *email    = self.collectionView.sections[EHIEnrollmentIssuesSectionEmail];
    EHIListDataSourceSection *create   = self.collectionView.sections[EHIEnrollmentIssuesSectionPasswordCreate];
    EHIListDataSourceSection *rules    = self.collectionView.sections[EHIEnrollmentIssuesSectionPasswordRules];
    EHIListDataSourceSection *confirm  = self.collectionView.sections[EHIEnrollmentIssuesSectionPasswordConfirmation];
    EHIListDataSourceSection *join     = self.collectionView.sections[EHIEnrollmentIssuesSectionJoin];
    
    model.bind.map(@{
        source(model.title)                : dest(self, .title),
        source(model.headerTitle)          : dest(header, .model),
        source(model.requiredInfoModel)    : dest(required, .model),
        source(model.profileModels)        : dest(profile, .models),
        source(model.addressModels)        : dest(address, .models),
        source(model.phoneModel)           : dest(phone, .model),
        source(model.emailModel)           : dest(email, .model),
        source(model.createPasswordModel)  : dest(create, .model),
        source(model.passwordSection)      : dest(rules, .models),
        source(model.confirmPasswordModel) : dest(confirm, .model),
        source(model.joinModel)            : dest(join, .model),
        source(model.isLoading)            : dest(self, .loadingActivity.isAnimating)
    });
}

- (void)invalidateWarning:(MTRComputation *)computation
{
    NSString *warning = self.viewModel.warning;
    
    BOOL shouldScroll = warning.length > 0;
    __weak typeof(self) welf = self;
    [welf.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHIEnrollmentIssuesSectionWarning].model = warning;
    } completion:^(BOOL completed){
        if(shouldScroll) {
            [welf.collectionView reloadData];
            NSIndexPath *indexPath = [NSIndexPath indexPathForItem:0 inSection:EHIEnrollmentIssuesSectionWarning];
            [welf.collectionView scrollToItemAtIndexPath:indexPath atScrollPosition:UICollectionViewScrollPositionTop animated:YES];
        }
    }];
}

# pragma mark - EHIEnrollmentPasswordCellActions

- (void)passwordCellDidShowNoMatch:(EHIEnrollmentPasswordCell *)cell
{
    [self.collectionView ehi_invalidateLayoutAnimated:NO];
}

- (void)passwordCellWillDismissKeyboard:(EHIEnrollmentPasswordCell *)cell
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:cell];
    
    [self advanceToFirstResponderAfterIndexPath:indexPath];
}

# pragma mark - First Responder Chain

- (void)didResignFirstResponderForCell:(EHIFormFieldCell *)sender
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    
    [self advanceToFirstResponderAfterIndexPath:indexPath];
}

- (void)advanceToFirstResponderAfterIndexPath:(NSIndexPath *)indexPath
{
    [self.collectionView ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:indexPath];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

- (void)didTapExitButton:(id)sender
{
    [self.viewModel exit];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenEnrollmentIssues state:self.viewModel.currentState];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventEnrollmentLoad;
    }];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenEnrollmentIssues;
}

@end
