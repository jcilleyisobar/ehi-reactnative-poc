//
//  EHIEnrollmentConfirmationViewController.mViewController
//  Enterprise
//
//  Created by Rafael Machado on 8/11/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIEnrollmentConfirmationViewController.h"
#import "EHIEnrollmentConfirmationViewModel.h"
#import "EHIBenefitsView.h"
#import "EHILabel.h"
#import "EHIButton.h"

@interface EHIEnrollmentConfirmationViewController ()
@property (strong, nonatomic) EHIEnrollmentConfirmationViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIBenefitsView *benefitsView;
@property (weak  , nonatomic) IBOutlet UILabel *introLabel;
@property (weak  , nonatomic) IBOutlet UILabel *bulletItemOneLabel;
@property (weak  , nonatomic) IBOutlet UILabel *bulletItemTwoLabel;
@property (weak  , nonatomic) IBOutlet UILabel *bulletItemThreeLabel;
@property (weak  , nonatomic) IBOutlet UILabel *bulletItemFourLabel;
@property (weak  , nonatomic) IBOutlet UILabel *learnMoreLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *learnMoreButton;
@property (weak  , nonatomic) IBOutlet EHIButton *continueButton;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;
@end

@implementation EHIEnrollmentConfirmationViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIEnrollmentConfirmationViewModel new];
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    //back button should not be visible
    self.navigationItem.leftBarButtonItems = nil;
    
    self.closeButton.type = EHIButtonTypeClose;
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

- (BOOL)needsBottomLine
{
    return YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIEnrollmentConfirmationViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateBenefitsModel:)];
    
    model.bind.map(@{
        source(model.title)             : ^(NSString *title) {
                                            self.title = title;
                                            self.titleLabel.text = title;
                                        },
        source(model.intro)             : dest(self, .introLabel.text),
        source(model.benefitsViewModel) : dest(self, .benefitsView.viewModel),
        source(model.bulletOne)         : dest(self, .bulletItemOneLabel.text),
        source(model.bulletTwo)         : dest(self, .bulletItemTwoLabel.text),
        source(model.bulletThree)       : dest(self, .bulletItemThreeLabel.text),
        source(model.bulletFour)        : dest(self, .bulletItemFourLabel.text),
        source(model.learnMore)         : dest(self, .learnMoreLabel.text),
        source(model.learnMoreButton)   : dest(self, .learnMoreButton.ehi_title),
        source(model.continueTitle)     : dest(self, .continueButton.ehi_title)
    });
}

- (void)invalidateBenefitsModel:(MTRComputation *)computation
{
    EHIViewModel *viewModel = self.viewModel.benefitsViewModel;
    
    [self.benefitsView updateWithModel:viewModel];
}

- (IBAction)didTapClose:(id)sender
{
    [self.viewModel close];
}

- (IBAction)didTapLearnMore:(id)sender
{
    [self.viewModel showBenefits];
}

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenEnrollmentConfirmation state:self.viewModel.currentState];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenEnrollmentConfirmation;
}

@end
