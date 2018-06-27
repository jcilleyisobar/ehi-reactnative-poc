//
//  EHIAdditionalInformationViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 4/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAdditionalInformationViewController.h"
#import "EHIAdditionalInformationViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIActivityIndicator.h"
#import "EHIButton.h"
#import "EHIFormFieldCell.h"
#import "EHIFormFieldTextViewCell.h"
#import "EHIActionButton.h"
#import "EHIRestorableConstraint.h"
#import "EHIRequiredInfoView.h"

@interface EHIAdditionalInformationViewController ()
@property (strong, nonatomic) EHIAdditionalInformationViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet UILabel *instructionsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;
@property (weak  , nonatomic) IBOutlet EHIActionButton *submitButton;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *navigationHeight;
@property (weak  , nonatomic) IBOutlet EHIRequiredInfoView *requiredInfoWarningContainer;
@end

@implementation EHIAdditionalInformationViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIAdditionalInformationViewModel new];
    }

    return self;
}

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.closeButton.type = EHIButtonTypeClose;
    self.collectionView.section.klass = EHIFormFieldCell.class;
    self.collectionView.section.isDynamicallySized = YES;
    self.requiredInfoWarningContainer.viewModel = self.viewModel.requiredInfoModel;
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    self.viewModel.handler = attributes.handler;
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIAdditionalInformationViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *section = self.collectionView.section;
    
    model.bind.map(@{
        source(model.title)             : ^(NSString *title){
                                            self.titleLabel.text = title;
                                            self.title           = title;
                                        },
        source(model.instructionsTitle) : dest(self, .instructionsLabel.text),
        source(model.submitTitle)       : dest(self, .submitButton.ehi_title),
        source(model.isInvalid)         : dest(self, .submitButton.isFauxDisabled),
        source(model.formModels)        : dest(section, .models),
        source(model.isLoading)         : dest(self, .loadingIndicator.isAnimating),
        source(model.hideNavigation)    : dest(self, .navigationHeight.isDisabled),
    });
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

# pragma mark - Actions

- (IBAction)didTapClose:(EHIButton *)sender
{
    [self.viewModel close];
}

- (IBAction)didTapSubmit:(EHIButton *)sender
{
    [self.viewModel submit];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationAdditionalInfo state:EHIScreenReservationAdditionalInfo];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationAdditionalInfo;
}

@end
