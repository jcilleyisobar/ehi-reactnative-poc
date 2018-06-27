//
//  EHIReservationEnterprisePlusInfoViewController.m
//  Enterprise
//
//  Created by Alex Koller on 4/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationEnterprisePlusInfoViewController.h"
#import "EHIReservationEnterprisePlusInfoViewModel.h"
#import "EHILabel.h"
#import "EHITextField.h"
#import "EHIButton.h"
#import "NSNotificationCenter+Utility.h"

@interface EHIReservationEnterprisePlusInfoViewController () <UITextFieldDelegate>
@property (strong, nonatomic) EHIReservationEnterprisePlusInfoViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *detailsLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *emailTextField;
@property (weak  , nonatomic) IBOutlet EHIButton *actionButton;
@end

@implementation EHIReservationEnterprisePlusInfoViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationEnterprisePlusInfoViewModel new];
    }
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationEnterprisePlusInfoViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .titleLabel.text),
        source(model.detailsTitle)      : dest(self, .detailsLabel.attributedText),
        source(model.email)             : dest(self, .emailTextField.text),
        source(model.emailPlaceholder)  : dest(self, .emailTextField.attributedPlaceholder),
        source(model.actionButtonTitle) : dest(self, .actionButton.ehi_title),
        source(model.canSubmitEmail)    : dest(self, .actionButton.enabled)
    });
}

# pragma mark - UITextFieldDelegate

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *newText = [textField.text stringByReplacingCharactersInRange:range withString:string];
    self.viewModel.email = newText;
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    // update via reactions on viewModel
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

# pragma mark - Actions

- (IBAction)didTapCloseButton:(id)sender
{
    [self.emailTextField resignFirstResponder];
    [self.viewModel dismiss];
}

- (IBAction)didTapEmailButton:(id)sender
{
    [self.emailTextField resignFirstResponder];
    [self.viewModel emailReminder];
}

# pragma mark - Keyboard

- (BOOL)requiresKeyboardSupport
{
    return YES;
}

- (void)applyKeyboardInsets:(BOOL)shouldInset forNotification:(NSNotification *)notification
{
    if(shouldInset) {
        CGFloat offsetY = [notification ehi_keyboardOverlapInView:self.view];
        
        // resize the view so that it sits above keyboard
        [notification ehi_animateWithKeyboard:^{
            self.view.layer.transform = CATransform3DMakeTranslation(0.0, -1 * offsetY, 0.0);
        }];
    } else {
        // restore the view back to its original statee
        [notification ehi_animateWithKeyboard:^{
            self.view.layer.transform = CATransform3DIdentity;
        }];
    }
}

# pragma mark - EHIViewController

- (EHIModalTransitionStyle)customModalTransitionStyle
{
    return EHIModalTransitionStyleOverlay;
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationEPlusInfo;
}

@end
