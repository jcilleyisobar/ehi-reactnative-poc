//
//  EHIAlertViewBuilder.m
//  Enterprise
//
//  Created by Ty Cobb on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAlertViewBuilder.h"
#import "EHIDialog.h"

#define EHIAlertViewIndexNil (-1)

@interface EHIAlertViewBuilder ()
@property (strong, nonatomic) EHIDialog *dialog;
@property (copy  , nonatomic) NSString *alertTitle;
@property (copy  , nonatomic) NSString *alertMessage;
@property (assign, nonatomic) EHIAlertViewStyle alertStyle;
@property (strong, nonatomic) NSMutableArray *buttonTitles;
@property (assign, nonatomic) NSInteger cancelIndex;
@property (copy  , nonatomic) id completion;
@end

@implementation EHIAlertViewBuilder

- (instancetype)init
{
    if(self = [super init]) {
        _cancelIndex = EHIAlertViewIndexNil;
        _dialog = [[EHIDialog alloc] initWithStyle:UIAlertControllerStyleAlert];
    }

    return self;
}

# pragma mark - Builder

- (void)showWithCompletion:(id)completion
{
    EHIDialogModel *model = [EHIDialogModel new];
    
    NSArray *buttonTitles   = self.buttonTitles.copy;
    EHIAlertViewStyle style = self.alertStyle;
    NSArray<UITextField *>* textFields = self.dialog.textFields;
    model.actionHandler = ^(UIAlertAction *action){
        NSString *title = action.title;
        BOOL cancel = action.style == UIAlertActionStyleCancel;
        NSInteger index = buttonTitles.indexOf(title);
        
        if(style == EHIAlertViewStyleDefault) {
            if(buttonTitles.count > 1) {
                ehi_call(((EHIAlertViewDismissHandler)completion))(index, cancel);
            } else {
                ehi_call(((EHIAlertViewConfirmHandler)completion))();
            }
        }
        // input alerts
        else {
            NSString *input = textFields.firstObject.text ?: @"";
            ehi_call(((EHIAlertViewInputHandler)completion))(input, index, cancel);
        };
    };
    
    model.title   = self.alertTitle;
    model.message = self.alertMessage;
    model.buttonTitles = self.buttonTitles;
    model.cancelIndex  = self.cancelIndex;
    model.secureInput  = self.alertStyle == EHIAlertViewStyleSecureTextInput;
    
    [self.dialog show:model];
}

//
// Helpers
//

# pragma mark - Message Style

- (EHIAlertViewBuilder *)withTitle:(NSString *)title
{
    self.alertTitle = title;
    return self;
}

- (EHIAlertViewBuilder *)withMessage:(NSString *)message
{
    self.alertMessage = message;
    return self;
}

- (EHIAlertViewBuilder *)withStyle:(EHIAlertViewStyle)style
{
    self.alertStyle = style;
    return self;
}

- (EHIAlertViewBuilder *)withButtonTitle:(NSString *)title
{
    if(!self.buttonTitles) {
        self.buttonTitles = [NSMutableArray new];
    }
    
    [self.buttonTitles addObject:title];
    return self;
}

- (EHIAlertViewBuilder *)withCancelButtonTitle:(NSString *)title
{
    self.cancelIndex = self.buttonTitles.count;
    return [self withButtonTitle:title ?: EHILocalizedString(@"standard_button_cancel", @"Cancel", @"Standard cancel button title")];
}

# pragma mark - Block Style

- (EHIAlertViewBuilder *(^)(NSString *))title
{
    return ^(NSString *title) {
        return [self withTitle:title];
    };
}

- (EHIAlertViewBuilder *(^)(NSString *))message
{
    return ^(NSString *message) {
        return [self withMessage:message];
    };
}

- (EHIAlertViewBuilder *(^)(NSString *))button
{
    return ^(NSString *title) {
        return [self withButtonTitle:title];
    };
}

- (EHIAlertViewBuilder *(^)(NSString *))cancelButton
{
    return ^(NSString *title) {
        return [self withCancelButtonTitle:title];
    };
}

- (EHIAlertViewBuilder *(^)(EHIAlertViewStyle))style
{
    return ^(EHIAlertViewStyle style) {
        return [self withStyle:style];
    };
}

- (void (^)(id))show
{
    return ^(id completion) {
        return [self showWithCompletion:completion];
    };
}

@end
