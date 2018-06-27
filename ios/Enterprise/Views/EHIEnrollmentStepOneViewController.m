//
//  EHIEnrollmentStepOneViewController.m
//  Enterprise
//
//  Created by Rafael Machado on 8/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepOneViewController.h"
#import "EHIEnrollmentStepOneViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIActivityIndicator.h"
#import "EHIEnrollmentStepHeaderCell.h"
#import "EHIFormFieldCell.h"
#import "EHIEnrollmentWarningCell.h"
#import "EHIRequiredInfoCell.h"

@interface EHIEnrollmentStepOneViewController () <EHIFormFieldCellActions>
@property (strong, nonatomic) EHIEnrollmentStepOneViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@end

@implementation EHIEnrollmentStepOneViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIEnrollmentStepOneViewModel new];
    }

    return self;
}

# pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
        
    [self.collectionView.sections construct:@{
        @(EHIEnrollmentStepOneSectionWarning)      : EHIEnrollmentWarningCell.class,
        @(EHIEnrollmentStepOneSectionHeader)       : EHIEnrollmentStepHeaderCell.class,
        @(EHIEnrollmentStepOneSectionRequiredInfo) : EHIRequiredInfoCell.class,
        @(EHIEnrollmentStepOneSectionProfile)      : EHIFormFieldCell.class,
        @(EHIEnrollmentStepOneSectionButton)       : EHIFormFieldCell.class
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
    }
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

- (void)didTapBackButton:(UIButton *)button
{
    [super didTapBackButton:button];
    
    [self.viewModel didTapBack];
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    self.viewModel.signinFlow = [attributes.userObject boolValue];
    self.viewModel.handler = attributes.handler;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIEnrollmentStepOneViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *header   = self.collectionView.sections[EHIEnrollmentStepOneSectionHeader];
    EHIListDataSourceSection *required = self.collectionView.sections[EHIEnrollmentStepOneSectionRequiredInfo];
    EHIListDataSourceSection *form     = self.collectionView.sections[EHIEnrollmentStepOneSectionProfile];
    EHIListDataSourceSection *button   = self.collectionView.sections[EHIEnrollmentStepOneSectionButton];
    
    [MTRReactor autorun:self action:@selector(invalidateForm:)];
    [MTRReactor autorun:self action:@selector(invalidateWarning:)];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .title),
        source(model.isLoading)         : dest(self, .loadingIndicator.isAnimating),
        source(model.headerModel)       : dest(header, .model),
        source(model.requiredInfoModel) : dest(required, .model),
        source(model.formModels)        : dest(form, .models),
        source(model.buttonModel)       : dest(button, .model),
    });
}

- (void)invalidateForm:(MTRComputation *)computation
{
    NSArray *forms = self.viewModel.formModels;
    
    __weak typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHIEnrollmentStepOneSectionProfile].models = forms;
    } completion:nil];
}

- (void)invalidateWarning:(MTRComputation *)computation
{
    NSString *warning = self.viewModel.warning;
    
    __weak typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHIEnrollmentStepOneSectionWarning].model = warning;
    } completion:^(BOOL completed){
        if(warning.length > 0) {
            [welf.collectionView reloadData];
            NSIndexPath *indexPath = [NSIndexPath indexPathForItem:0 inSection:EHIEnrollmentStepOneSectionWarning];
            [welf.collectionView scrollToItemAtIndexPath:indexPath atScrollPosition:UICollectionViewScrollPositionTop animated:YES];
        }
    }];
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

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenEnrollmentStepOne state:EHIScreenEnrollmentStepOne];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:^(EHIAnalyticsContext *context) {
        context.macroEvent = EHIAnalyticsMacroEventEnrollmentStepOne;
    }];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenEnrollmentStepOne;
}

@end
