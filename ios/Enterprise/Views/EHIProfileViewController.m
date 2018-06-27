//
//  EHIProfileViewController.m
//  Enterprise
//
//  Created by fhu on 5/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfileViewController.h"
#import "EHIProfileViewModel.h"
#import "EHIProfileAuthenticationViewController.h"
#import "EHIProfileBasicCell.h"
#import "EHIProfilePaymentCell.h"
#import "EHIProfileFooterCell.h"
#import "EHIListCollectionView.h"
#import "EHICollectionTitleView.h"
#import "EHISectionHeaderModel.h"
#import "EHIActivityIndicator.h"
#import "EHIProfilePaymentAddCell.h"

@interface EHIProfileViewController () <EHISectionHeaderActions, EHIProfileAuthenticationDelegate, EHIListCollectionViewDelegate, EHIProfilePaymentAddCellActions>
@property (strong, nonatomic) EHIProfileViewModel *viewModel;
@property (weak  , nonatomic) EHIProfileAuthenticationViewController *authenticateViewController;
@property (weak  , nonatomic) IBOutlet UIView *authenticateContainer;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@end

@implementation EHIProfileViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIProfileViewModel new];
    }
    
    return self;
}

# pragma mark - View Lifecycle

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    [super prepareForSegue:segue sender:sender];
    
    if([segue.identifier isEqualToString:@"EHISegueEmbedAuthenticateViewController"]) {
        self.authenticateViewController = segue.destinationViewController;
        self.authenticateViewController.delegate = self;
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHIProfileSectionBasic)   : [EHIProfileBasicCell class],
        @(EHIProfileSectionDriver)  : [EHIProfileBasicCell class],
        @(EHIProfileSectionPayment) : [EHIProfilePaymentCell class],
        @(EHIProfileSectionAddCard) : [EHIProfilePaymentAddCell class]
    }];
    
    // the footer is always visible
    EHIListDataSourceSection *footerCell = self.collectionView.sections[EHIProfileSectionFooter];
    footerCell.klass = EHIProfileFooterCell.class;
    footerCell.model = [EHIModel placeholder];
}

# pragma mark - EHIListCollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didDequeueReusableView:(EHICollectionReusableView *)reusableView kind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    reusableView.tag = indexPath.section;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIProfileViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *basicInformation   = self.collectionView.sections[EHIProfileSectionBasic];
    EHIListDataSourceSection *licenseInformation = self.collectionView.sections[EHIProfileSectionDriver];
    EHIListDataSourceSection *addCard            = self.collectionView.sections[EHIProfileSectionAddCard];
    
    model.bind.map(@{
        source(model.title)              : dest(self, .title),
        source(model.basicInformation)   : dest(basicInformation, .models),
        source(model.licenseInformation) : dest(licenseInformation, .models),
        source(model.addCardModel)       : dest(addCard, .model),
        source(model.isLoading)          : dest(self, .loadingIndicator.isAnimating),
    });
    
    [MTRReactor autorun:self action:@selector(invalidateAuthenticateView:)];
    [MTRReactor autorun:self action:@selector(invalidatePaymentSection:)];
    [MTRReactor autorun:self action:@selector(invalidateHeaders:)];
}

- (void)invalidateAuthenticateView:(MTRComputation *)computation
{
    BOOL hideAuthenticate = self.viewModel.authenticated;
    
    // only show signout when user doesn't have to re-authenticate
    UIBarButtonItem *item = hideAuthenticate ? self.viewModel.signoutButton : nil;
    [self.navigationItem setRightBarButtonItem:item animated:!computation.isFirstRun];
    
    // animate the overlay as needed
    UIView.animate(!computation.isFirstRun).duration(0.15f).transform(^{
        self.authenticateContainer.alpha = hideAuthenticate ? 0.0f : 1.0f;
    }).start(nil);
}

- (void)invalidatePaymentSection:(MTRComputation *)computation
{
    EHIViewModel *model = self.viewModel.paymentInformation;
    
    __weak __typeof(self) welf = self;
    [self.collectionView performAnimated:!computation.isFirstRun batchUpdates:^{
        welf.collectionView.sections[EHIProfileSectionPayment].model = model;
    } completion:^(BOOL completed){
        [welf.collectionView ehi_invalidateLayoutAnimated:NO];
    }];
}

- (void)invalidateHeaders:(MTRComputation *)computation
{
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        // everything but the footer is dynamically sized
        section.isDynamicallySized = section.index != EHIProfileSectionFooter;
        
        // add the header if appropriate
        EHISectionHeaderModel *headerModel = [self.viewModel headerForSection:section.index];
        if(headerModel) {
            section.header.klass = [EHISectionHeader class];
            section.header.model = headerModel;
            section.header.metrics = [self metricForSection:section.index];
        }
    }
}

# pragma mark - EHISectionHeaderActions

- (void)sectionHeaderDidTapActionButton:(EHISectionHeader *)sectionHeader
{
    [self.viewModel didTapActionForHeaderForSection:sectionHeader.tag];
}

# pragma mark - EHIProfileAuthenticateEvents

- (void)profileAuthenticationViewControllerDidAuthenticate:(EHIProfileAuthenticationViewController *)viewController
{
    // update authenticated state on sign-in
    [self.viewModel setAuthenticated:YES];
}

# pragma mark - EHIProfilePaymentAddCellActions

- (void)didTapAddCreditCard:(EHIProfilePaymentAddCell *)cell
{
    [self.viewModel didTapAddCreditCard];
}

# pragma mark - Layout

- (EHILayoutMetrics *)metricForSection:(EHIProfileSection)section
{
    EHILayoutMetrics *metrics = [EHISectionHeader.metrics copy];
    
    metrics.backgroundColor = [UIColor ehi_grayColor2];
    metrics.primaryFont     = [UIFont ehi_fontWithStyle:EHIFontStyleBold size:14.0f];
    metrics.secondaryFont   = [UIFont ehi_fontWithStyle:EHIFontStyleLight size:14.0f];
    if(section == EHIProfileSectionBasic) {
        // the fist section don't have the fancy divider
        metrics.fixedSize = (CGSize){.width = EHILayoutValueNil, .height = 36.0f};
    } else {
        metrics.fixedSize = (CGSize){.width = EHILayoutValueNil, .height = 46.0f};
    }
    
    return metrics;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenProfile state:EHIScreenProfile];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenProfile;
}

@end
