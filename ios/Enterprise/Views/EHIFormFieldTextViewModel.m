//
//  EHIFormFieldTextViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldTextViewModel.h"
#import "EHIPhone.h"
#import "EHIPhoneNumberFormatter.h"

@implementation EHIFormFieldTextViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _hidesDeleteButton = YES;
        _limit = NSIntegerMax;
    }
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIPhone class]]) {
        [self updateWithPhone:model];
    }
}

//
// Helpers
//

- (void)updateWithPhone:(EHIPhone *)phone
{
    self.isPhoneField = YES;
    self.inputValue = phone.number;
    self.categoryOptions = [EHIPhone userPhoneTypeOptionsStrings];
    self.selectedCategory = [EHIPhone userPhoneTypeOptions].indexOf(@(phone.type));
}

# pragma mark - Accessors

- (EHIFormFieldType)type
{
    return EHIFormFieldTypeText;
}

- (NSString *)inputValue
{
    NSString *value = [super inputValue];
    
    // filter empty strings to nil
    if(value.length == 0) {
        return nil;
    }
    
    return value;
}

- (BOOL)allowsCategorySelection
{
    return self.categoryOptions != nil;
}

- (NSString *)selectedCategoryName
{
    return self.categoryOptions[self.selectedCategory];
}

# pragma mark - Setters

- (void)setInputValue:(NSString *)inputValue
{
    if (inputValue.length > self.limit) {
        return;
    }

    if(self.isPhoneField && !inputValue.ehi_isMasked) {
        self.phoneModel = [EHIPhoneNumberFormatter format:inputValue];
        inputValue = self.phoneModel.formattedPhone;
    }
    
    [super setInputValue:inputValue];
}

- (void)setIsEmailField:(BOOL)isEmailField
{
    _isEmailField = isEmailField;
    
    self.keyboardType = isEmailField ? UIKeyboardTypeEmailAddress : UIKeyboardTypeDefault;
}

- (void)setIsPhoneField:(BOOL)isPhoneField
{
    _isPhoneField = isPhoneField;
    
    self.keyboardType = isPhoneField ? UIKeyboardTypeNumberPad : UIKeyboardTypeDefault;
}

- (void)setSelectedCategory:(NSUInteger)selectedCategory
{
    if(_selectedCategory == selectedCategory) {
        return;
    }
    
    _selectedCategory = selectedCategory;
    
    // adjust placeholder if category options placeholders have been set
    if(self.categoryOptionPlaceholders) {
        self.placeholder = self.categoryOptionPlaceholders[_selectedCategory];
    }
}

- (void)setCategoryOptionPlaceholders:(NSArray *)categoryOptionPlaceholders
{
    _categoryOptionPlaceholders = categoryOptionPlaceholders;
    
    // update the placeholder if user elects to use category option placeholders
    self.placeholder = _categoryOptionPlaceholders[self.selectedCategory];
}

@end

@implementation EHIFormFieldTextViewModel (Generators)

+ (instancetype)accountFieldForCorporateAccount:(EHIContractDetails *)corporateAccount
{
    EHIFormFieldTextViewModel *account = [EHIFormFieldTextViewModel new];
    account.title = EHILocalizedString(@"profile_edit_account_title", @"ACCOUNT", @"");
    account.inputValue = corporateAccount.name;
    account.hidesDeleteButton = NO;
    account.isUneditable = YES;
    account.isLastInGroup = YES;
    
    return account;
}

+ (instancetype)phoneFieldForPhone:(EHIPhone *)phone withTitle:(NSString *)title
{
    EHIFormFieldTextViewModel *preferredPhone = [EHIFormFieldTextViewModel new];
    preferredPhone.title = title;
    preferredPhone.categoryOptions = [EHIPhone userPhoneTypeOptionsStrings];
    preferredPhone.isPhoneField = YES;
    preferredPhone.isLastInGroup = YES;
    preferredPhone.inputValue = phone.maskedNumber;
 
    // handle unknowns by defaulting to first option
    NSUInteger index = preferredPhone.categoryOptions.indexOf(phone.typeTitle);
    preferredPhone.selectedCategory = index != NSNotFound ? index : 0;
    
    return preferredPhone;
}

@end
