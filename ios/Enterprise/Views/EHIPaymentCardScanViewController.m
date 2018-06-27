//
//  EHIPaymentCardScanViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 1/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentCardScanViewController.h"
#import "EHIPaymentCardScanViewModel.h"
#import "CardIO.h"

@interface EHIPaymentCardScanViewController () <CardIOPaymentViewControllerDelegate>
@property (strong, nonatomic) EHIPaymentCardScanViewModel *viewModel;
@property (strong, nonatomic) CardIOPaymentViewController *cardIOViewController;
@end

@implementation EHIPaymentCardScanViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPaymentCardScanViewModel new];
    }

    return self;
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    self.viewModel.handler = attributes.handler;
}

# pragma mark - View Lifecycle

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [CardIOUtilities preload];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self addCardIOViewController];
}

- (void)addCardIOViewController
{
    [self addChildViewController:self.cardScanViewController];
    [self.view addSubview:self.cardScanViewController.view];
}

# pragma mark - Card.io

- (CardIOPaymentViewController *)cardScanViewController
{
    if(!_cardIOViewController) {
        _cardIOViewController = [[CardIOPaymentViewController alloc] initWithPaymentDelegate:self];
        _cardIOViewController.scannedImageDuration = 0.f;
        _cardIOViewController.useCardIOLogo = NO;
        _cardIOViewController.hideCardIOLogo = YES;
        _cardIOViewController.scanExpiry = YES;
        _cardIOViewController.collectCVV = YES;
        _cardIOViewController.guideColor = [UIColor ehi_greenColor];
    }
    
    return _cardIOViewController;
}

# pragma mark - Card.io Delegate

- (void)userDidCancelPaymentViewController:(CardIOPaymentViewController *)paymentViewController
{
    [self.viewModel didCancelCardScan];
}

- (void)userDidProvideCreditCardInfo:(CardIOCreditCardInfo *)cardInfo inPaymentViewController:(CardIOPaymentViewController *)paymentViewController
{
    [self.viewModel didScanCreditCard:cardInfo];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPaymentCardScanViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.scanInstructions) : dest(self, .cardIOViewController.scanInstructions)
    });
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenCardScan;
}

@end
