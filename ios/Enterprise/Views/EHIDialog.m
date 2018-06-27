//
//  EHIDialog.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/25/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIDialog.h"
#import "UIAlertController+Window.h"

@interface EHIDialog ()
@property (strong, nonatomic) UIAlertController *controller;
@end

@implementation EHIDialog

- (instancetype)initWithStyle:(UIAlertControllerStyle)style
{
    if(self = [super init]) {
        _controller = [UIAlertController alertControllerWithTitle:nil message:nil preferredStyle:style];
    }
    
    return self;
}

- (NSArray *)textFields
{
    return self.controller.textFields;
}
    
- (void)show:(EHIDialogModel *)model
{
    [self bind:model];

    [self.controller show:YES];
}

- (void)bind:(EHIDialogModel *)model
{
    // bind title
    self.controller.title   = model.title;
    self.controller.message = model.message;
    
    // add text field, if necessary
    BOOL secureInput = model.secureInput;
    if(secureInput) {
        [self.controller addTextFieldWithConfigurationHandler:^(UITextField *textField) {
            textField.secureTextEntry = YES;
        }];
    }
    
    // create buttons and add them
    NSArray *buttons = model.buttonTitles;
    NSInteger cancelIndex = model.cancelIndex;
    EHIDialogModelHandler handler = model.actionHandler;
    (buttons ?: @[]).map(^(NSString *title, int index){
        UIAlertActionStyle style = index == cancelIndex ? UIAlertActionStyleCancel : UIAlertActionStyleDefault;
        return [UIAlertAction actionWithTitle:title style:style handler:handler];
    }).each(^(UIAlertAction *action){
        [self.controller addAction:action];
    });
}

@end
