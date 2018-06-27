//
//  EHISurveyViewController.m
//  Enterprise
//
//  Created by frhoads on 12/7/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISurveyViewController.h"
#import "EHISurveyViewModel.h"
#import "EHIActionButton.h"
#import "EHITextField.h"
#import "EHIActivityIndicator.h"

@interface EHISurveyViewController () <UITextFieldDelegate>
@property (strong, nonatomic) EHISurveyViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet UILabel *greetingsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *instructionsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *customerDetailLabel;
@property (weak  , nonatomic) IBOutlet UITextField *customerDetailsTextField;
@property (weak  , nonatomic) IBOutlet EHIActionButton *sendSurveyButton;
@property (weak  , nonatomic) IBOutlet EHIButton *surveyPolicyButton;
@end

@implementation EHISurveyViewController

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHISurveyViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHISurveyViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)               : dest(self, .title),
        source(model.isLoading)           : dest(self, .loadingIndicator.isAnimating),
        source(model.greetingsTitle)      : dest(self, .greetingsLabel.text),
        source(model.instructionsTitle)   : dest(self, .instructionsLabel.text),
        source(model.customerDetailTitle) : dest(self, .customerDetailLabel.attributedText),
        source(model.customerDetail)      : dest(self, .customerDetailsTextField.text),
        source(model.sendSurveyTitle)     : dest(self, .sendSurveyButton.ehi_title),
        source(model.isInvalidInput)      : dest(self, .sendSurveyButton.isFauxDisabled),
        source(model.surveyPolicyTitle)   : dest(self, .surveyPolicyButton.ehi_title),
    });
}

# pragma mark - UITextFieldDelegate

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *newText = [textField.text stringByReplacingCharactersInRange:range withString:string];
    
    self.viewModel.customerDetail = newText;

    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    return YES;
}

# pragma mark - Actions

- (IBAction)didTapSendSurveyButton:(id)sender
{
    [self.viewModel submitContact];
}

- (IBAction)didTapPrivacyPolicyButton:(id)sender
{
    [self.viewModel showSurveyPolicy];
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenSurvey state:EHIScreenSurvey];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

- (BOOL)automaticallyInvalidatesAnalyticsContext
{
    return NO;
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenSurvey;
}

@end
