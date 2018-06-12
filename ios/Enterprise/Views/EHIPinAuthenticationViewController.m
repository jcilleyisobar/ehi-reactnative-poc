//
//  EHIPinAuthenticationViewController.m
//  Enterprise
//
//  Created by cgross on 4/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPinAuthenticationViewController.h"
#import "EHIPinAuthenticationViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIActivityIndicator.h"
#import "EHIButton.h"
#import "EHIFormFieldCell.h"
#import "EHIFormFieldTextViewCell.h"
#import "EHIActionButton.h"
#import "EHIRequiredInfoView.h"

@interface EHIPinAuthenticationViewController ()
@property (strong, nonatomic) EHIPinAuthenticationViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet UILabel *instructionsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;
@property (weak  , nonatomic) IBOutlet EHIActionButton *submitButton;
@property (weak  , nonatomic) IBOutlet EHIRequiredInfoView *requiredInfoWarningContainer;
@end

@implementation EHIPinAuthenticationViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPinAuthenticationViewModel new];
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
    self.requiredInfoWarningContainer.viewModel = self.viewModel.requiredInfoViewModel;
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

- (void)registerReactions:(EHIPinAuthenticationViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *section = self.collectionView.section;
    
    model.bind.map(@{
        source(model.title)             : dest(self, .titleLabel.text),
        source(model.instructionsTitle) : dest(self, .instructionsLabel.text),
        source(model.submitTitle)       : dest(self, .submitButton.ehi_title),
        source(model.isReadyToSubmit)   : dest(self, .submitButton.enabled),
        source(model.formModel)        : dest(section, .model),
        source(model.isLoading)         : dest(self, .loadingIndicator.isAnimating),
    });
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

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.collectionView;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationPinAuthentication state:EHIScreenReservationPinAuthentication];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationPinAuthentication;
}

@end
