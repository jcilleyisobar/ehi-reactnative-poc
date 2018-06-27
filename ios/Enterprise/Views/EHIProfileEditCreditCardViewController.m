//
//  EHIProfileEditCreditCardViewController.mViewController
//  Enterprise
//
//  Created by Rafael Ramos on 9/29/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfileEditCreditCardViewController.h"
#import "EHIProfileEditPaymentViewModel.h"
#import "EHITextField.h"
#import "EHIActionButton.h"
#import "EHIToggleButton.h"
#import "EHIBarButtonItem.h"
#import "EHIActivityIndicator.h"

@interface EHIProfileEditCreditCardViewController () < UIPickerViewDataSource, UIPickerViewDelegate>
@property (strong, nonatomic) EHIProfileEditPaymentViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak  , nonatomic) IBOutlet UILabel *aliasLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *aliasTextField;
@property (weak  , nonatomic) IBOutlet UILabel *cardNumberLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *cardImageView;
@property (weak  , nonatomic) IBOutlet UILabel *maskedNumberLabel;
@property (weak  , nonatomic) IBOutlet UILabel *expirationDateLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *expirationDateTextField;
@property (weak  , nonatomic) IBOutlet EHIButton *editDateButton;

@property (weak  , nonatomic) IBOutlet UIView *makePreferredContainer;
@property (weak  , nonatomic) IBOutlet EHIToggleButton *togglePreferred;
@property (weak  , nonatomic) IBOutlet UILabel *togglePreferredLabel;

@property (weak  , nonatomic) IBOutlet UIView *preferredContainer;
@property (weak  , nonatomic) IBOutlet UILabel *preferredLabel;

@property (weak  , nonatomic) IBOutlet EHIButton *saveButton;

@property (strong, nonatomic) UIPickerView *picker;
@property (strong, nonatomic) UIToolbar *pickerToolbar;

@end

@implementation EHIProfileEditCreditCardViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfileEditPaymentViewModel new];
    }

    return self;
}

- (void)updateNavigationItem:(UINavigationItem *)item
{
    [super updateNavigationItem:item];
    
    item.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeDelete target:self action:@selector(didTapDeleteButton:)];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.expirationDateTextField.inputView = self.picker;
    self.expirationDateTextField.inputAccessoryView = self.pickerToolbar;
    self.expirationDateTextField.tintColor = [UIColor clearColor];
    self.expirationDateTextField.borderType = EHITextFieldBorderNone;
    
    self.aliasTextField.returnKeyType = UIReturnKeyDone;
    self.aliasTextField.autocapitalizationType = UITextAutocapitalizationTypeWords;
    
    [EHIAnalytics markViewAsSensitive:self.maskedNumberLabel];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIProfileEditPaymentViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidatePreferred:)];
    
    model.bind.map(@{
        source(model.title)                 : dest(self, .title),
        source(model.paymentAliasTitle)     : dest(self, .aliasLabel.text),
        source(model.alias)                 : dest(self, .aliasTextField.text),
        source(model.paymentNumberTitle)    : dest(self, .cardNumberLabel.text),
        source(model.maskedNumber)          : dest(self, .maskedNumberLabel.text),
        source(model.expirationDateTitle)   : dest(self, .expirationDateLabel.text),
        source(model.expirationDate)        : dest(self, .expirationDateTextField.text),
        source(model.editDateButtonTitle)   : dest(self, .editDateButton.ehi_title),
        source(model.togglePreferredTitle)  : dest(self, .togglePreferredLabel.text),
        source(model.isPreferred)           : dest(self, .togglePreferred.selected),
        source(model.preferredTitle)        : dest(self, .preferredLabel.text),
        source(model.saveTitle)             : dest(self, .saveButton.ehi_title),
        source(model.creditCardImage)       : dest(self, .cardImageView.ehi_imageName),
        source(model.isLoading)             : dest(self, .loadingIndicator.isAnimating),

        source(model.selectedMonthIndex)    : ^(NSNumber *monthIndex) {
                                                [self.picker selectRow:monthIndex.integerValue inComponent:EHIProfileEditPaymentDatePickerComponentMonth animated:YES];
                                            },
        source(model.selectedYearIndex)     : ^(NSNumber *yearIndex) {
                                                [self.picker selectRow:yearIndex.integerValue inComponent:EHIProfileEditPaymentDatePickerComponentYear animated:YES];
                                            },
    });
}

- (void)invalidatePreferred:(MTRComputation *)computation
{
    BOOL hideToggle = self.viewModel.hideTogglePrefereed;
    
    MASLayoutPriority priority = hideToggle ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.makePreferredContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
    
    priority = hideToggle ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.preferredContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}


- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Actions

- (IBAction)didTapSave:(id)sender
{
    [self.viewModel saveChanges];
}

- (void)didTapDeleteButton:(UIBarButtonItem *)sender
{
    [self.viewModel deletePayment];
}

- (IBAction)didTapToggleDefault:(UITapGestureRecognizer *)gesture
{
    [self.viewModel togglePreferred];
}

# pragma mark - UITextFieldDelegate

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *oldText = self.viewModel.alias ?: @"";
    
    // set the value without side effects, so that the reaction can update the field
    self.viewModel.alias = [oldText stringByReplacingCharactersInRange:range withString:string];
    
    // force reactions to run, this is necessary because iOS's quicktype cycle is faster than reactor's flush
    [[MTRReactor reactor] flush];
    
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    return [textField resignFirstResponder];
}

- (IBAction)didTapEditDate:(EHIButton *)sender
{
    [self.viewModel editDate];
    [self.expirationDateTextField becomeFirstResponder];
}

- (void)dismissPicker:(id)sender
{
    NSInteger monthRow = [self.picker selectedRowInComponent:EHIProfileEditPaymentDatePickerComponentMonth];
    [self.viewModel didSelectRow:monthRow inComponent:EHIProfileEditPaymentDatePickerComponentMonth];
    NSInteger yearRow = [self.picker selectedRowInComponent:EHIProfileEditPaymentDatePickerComponentYear];
    [self.viewModel didSelectRow:yearRow inComponent:EHIProfileEditPaymentDatePickerComponentYear];

    [self.expirationDateTextField resignFirstResponder];
}

# pragma mark - UIPickerViewDataSource

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 2;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return [self.viewModel numberOfRowsInComponent:component];
}

# pragma mark - UIPickerViewDelegate

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return [self.viewModel titleForRow:row inComponent:component];
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    [self.viewModel didSelectRow:row inComponent:component];
    
    [pickerView reloadAllComponents];
}

# pragma mark - Getters

- (UIPickerView *)picker
{
    if(!_picker) {
        _picker = [UIPickerView new];
        _picker.dataSource = self;
        _picker.delegate = self;
    }
    
    return _picker;
}

- (UIToolbar *)pickerToolbar
{
    if(!_pickerToolbar) {
        EHIBarButtonItem *doneButton = [EHIBarButtonItem buttonWithType:EHIButtonTypeDoneGreen target:self action:@selector(dismissPicker:)];
        
        // create toolbar and add the done button as an item
        _pickerToolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.picker.bounds), 44.0f)];
        _pickerToolbar.items = @[[EHIBarButtonItem flexibleSpace], doneButton];
        [_pickerToolbar setBackgroundColor:[UIColor whiteColor]];
    }
    
    return _pickerToolbar;
}

# pragma mark - Keyboard

- (UIScrollView *)keyboardSupportedScrollView
{
    return self.scrollView;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenProfileEditPaymentCard state:EHIScreenProfileEditPaymentCard];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenProfileEditPaymentCard;
}

@end
