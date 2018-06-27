//
//  EHIEnrollmentStepThreeViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 8/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepThreeViewController.h"
#import "EHIEnrollmentStepThreeViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIActivityIndicator.h"
#import "EHIEnrollmentWarningCell.h"
#import "EHIEnrollmentStepHeaderCell.h"
#import "EHIFormFieldCell.h"
#import "EHIEnrollmentPasswordCell.h"
#import "EHIProfilePasswordRuleCell.h"
#import "EHIRequiredInfoCell.h"
#import "EHIRequiredInfoFootnoteCell.h"

@interface EHIEnrollmentStepThreeViewController () <EHIFormFieldCellActions>
@property (strong, nonatomic) EHIEnrollmentStepThreeViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingActivity;
@property (assign, nonatomic) NSInteger responderSection;
@end

@implementation EHIEnrollmentStepThreeViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIEnrollmentStepThreeViewModel new];
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHIEnrollmentStepThreeSectionWarning)              : EHIEnrollmentWarningCell.class,
        @(EHIEnrollmentStepThreeSectionHeader)               : EHIEnrollmentStepHeaderCell.class,
        @(EHIEnrollmentStepThreeSectionRequiredInfo)         : EHIRequiredInfoCell.class,
        @(EHIEnrollmentStepThreeSectionPhone)                : EHIFormFieldCell.class,
        @(EHIEnrollmentStepThreeSectionEmail)                : EHIFormFieldCell.class,
        @(EHIEnrollmentStepThreeSectionPassword)             : EHIEnrollmentPasswordCell.class,
        @(EHIEnrollmentStepThreeSectionPasswordRules)        : EHIProfilePasswordRuleCell.class,
        @(EHIEnrollmentStepThreeSectionPasswordConfirmation) : EHIEnrollmentPasswordCell.class,
        @(EHIEnrollmentStepThreeSectionJoin)                 : EHIFormFieldCell.class,
        @(EHIEnrollmentStepThreeSectionRequiredInfoFootnote) : EHIRequiredInfoFootnoteCell.class,
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
    }
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIEnrollmentStepThreeViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateLayout:)];
    
    EHIListDataSourceSection *header       = self.collectionView.sections[EHIEnrollmentStepThreeSectionHeader];
    EHIListDataSourceSection *required     = self.collectionView.sections[EHIEnrollmentStepThreeSectionRequiredInfo];
    EHIListDataSourceSection *phone        = self.collectionView.sections[EHIEnrollmentStepThreeSectionPhone];
    EHIListDataSourceSection *email        = self.collectionView.sections[EHIEnrollmentStepThreeSectionEmail];
    EHIListDataSourceSection *password     = self.collectionView.sections[EHIEnrollmentStepThreeSectionPassword];
    EHIListDataSourceSection *rules        = self.collectionView.sections[EHIEnrollmentStepThreeSectionPasswordRules];
    EHIListDataSourceSection *confirmation = self.collectionView.sections[EHIEnrollmentStepThreeSectionPasswordConfirmation];
    EHIListDataSourceSection *join         = self.collectionView.sections[EHIEnrollmentStepThreeSectionJoin];
    EHIListDataSourceSection *footnote     = self.collectionView.sections[EHIEnrollmentStepThreeSectionRequiredInfoFootnote];
    
    model.bind.map(@{
        source(model.title)                : dest(self, .title),
        source(model.headerModel)          : dest(header, .model),
        source(model.requiredInfoModel)    : dest(required, .model),
        source(model.phoneModel)           : dest(phone, .model),
        source(model.emailModel)           : dest(email, .model),
        source(model.createPasswordModel)  : dest(password, .model),
        source(model.passwordSection)      : dest(rules, .models),
        source(model.confirmPasswordModel) : dest(confirmation, .model),
        source(model.joinModel)            : dest(join, .model),
        source(model.footnoteModel)        : dest(footnote, .model),
        source(model.isLoading)            : dest(self, .loadingActivity.isAnimating)
    });
}

- (void)invalidateLayout:(MTRComputation *)computation
{
    NSString *warning = self.viewModel.warning;
    
    self.collectionView.sections[EHIEnrollmentStepThreeSectionWarning].model = warning;

    __weak typeof(self) welf = self;
    [self.collectionView ehi_invalidateLayoutAnimated:!computation.isFirstRun completion:^(BOOL finished) {
        if(welf.viewModel.warning.length > 0) {
            NSIndexPath *indexPath = [NSIndexPath indexPathForItem:0 inSection:EHIEnrollmentStepThreeSectionWarning];
            [welf.collectionView scrollToItemAtIndexPath:indexPath atScrollPosition:UICollectionViewScrollPositionTop animated:YES];
        }
    }];
}

# pragma mark - First Responder Chain

- (void)didResignFirstResponderForCell:(EHIFormFieldCell *)sender
{
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:sender];
    
    [self advanceToFirstResponderAfterIndexPath:indexPath];
}

- (void)advanceToFirstResponderAfterIndexPath:(NSIndexPath *)indexPath
{
    self.responderSection = indexPath.section;
     [self.collectionView ehi_advanceFirstResponderToNextCellInSectionAfterIndexPath:indexPath];
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

- (void)applyKeyboardInsets:(BOOL)shouldInset forNotification:(NSNotification *)notification
{
    CGFloat additionalInset = 0.f;

    switch (self.responderSection) {
        case EHIEnrollmentStepThreeSectionEmail:
            additionalInset = 130.f;
            break;
        case EHIEnrollmentStepThreeSectionPassword:
            additionalInset = 80.f;
            break;
        case EHIEnrollmentStepThreeSectionPasswordConfirmation:
            additionalInset = 130.f;
            break;
        default:
            additionalInset = 0.0f;
            break;
    }
    
    // animate the scroll view insets
    [self.keyboardSupportedScrollView ehi_animateKeyboardNotification:notification additionalInset:additionalInset animations:^{
        [self.view layoutIfNeeded];
    }];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenEnrollmentStepThree state:self.viewModel.currentState];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventEnrollmentStepThree;
    }];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenEnrollmentStepThree;
}

@end
