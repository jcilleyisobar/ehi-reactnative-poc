//
//  EHIActionSheetBuilder.m
//  Enterprise
//
//  Created by fhu on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIActionSheetBuilder.h"
#import "EHIDialog.h"

@interface EHIActionSheetBuilder ()
@property (strong, nonatomic) EHIDialog *dialog;
@property (copy  , nonatomic) NSString *sheetTitle;
@property (strong, nonatomic) NSMutableArray *buttonTitles;
@property (strong, nonatomic) NSMutableArray *buttonActions;
@property (assign, nonatomic) NSInteger cancelIndex;
@end

@implementation EHIActionSheetBuilder

- (instancetype)init
{
    if(self = [super init]){
        _cancelIndex = NSNotFound;
        _dialog = [[EHIDialog alloc] initWithStyle:UIAlertControllerStyleActionSheet];

    }
    return self;
}

# pragma mark - Builder

- (void)showWithCompletion:(EHIActionSheetCompletion)completion
{
    EHIDialogModel *model = [EHIDialogModel new];
    
    NSArray *buttonTitles = self.buttonTitles.copy;
    model.actionHandler = ^(UIAlertAction *action){
        NSString *title = action.title;
        BOOL cancel = action.style == UIAlertActionStyleCancel;
        NSInteger index = buttonTitles.indexOf(title);
        
        ehi_call(completion)(index, cancel);
    };
    
    model.title = self.sheetTitle;
    model.buttonTitles = self.buttonTitles;
    model.cancelIndex  = self.cancelIndex;
    
    [self.dialog show:model];
}

- (void)showExecutingButtonAction
{
    [self showWithCompletion:^(NSInteger selectedIndex, BOOL canceled){
        if (!canceled) {
            EHIActionSheetButtonAction action = [self.buttonActions ehi_safelyAccess:selectedIndex];
            ehi_call(action)();
        }
    }];
}

# pragma mark - Message Style

- (EHIActionSheetBuilder *)withTitle:(NSString *)title
{
    self.sheetTitle = title;
    return self;
}

- (EHIActionSheetBuilder *)withButtonTitle:(NSString *)title
{
    if(!self.buttonTitles)
        self.buttonTitles = [NSMutableArray new];
    [self.buttonTitles addObject:title];
    return self;
}

- (EHIActionSheetBuilder *)withCancelButtonTitle:(NSString *)title
{
    self.cancelIndex = self.buttonTitles.count;
    return [self withButtonTitle:title ?: EHILocalizedString(@"alert_cancel_title", @"Cancel", @"Alert view cancel button title")];
}

# pragma mark - Block Style

- (EHIActionSheetBuilder *(^)(NSString *))title
{
    return ^(NSString *title) {
        return [self withTitle:title];
    };
}

- (EHIActionSheetBuilder *(^)(NSString *))button
{
    return ^(NSString *title) {
        return [self withButtonTitle:title];
    };
}

- (EHIActionSheetBuilder *(^)(NSString *, EHIActionSheetButtonAction))buttonWithAction
{
    if (!self.buttonActions) {
        self.buttonActions = [NSMutableArray new];
    }
    
    return ^(NSString *title, EHIActionSheetButtonAction block) {
        [self.buttonActions addObject:[block copy]];
        return [self withButtonTitle:title];
    };
}

- (EHIActionSheetBuilder *(^)(NSString *))cancelButton
{
    return ^(NSString *title) {
        return [self withCancelButtonTitle:title];
    };
}

- (void (^)(EHIActionSheetCompletion))show
{
    return ^(EHIActionSheetCompletion completion) {
        return [self showWithCompletion:completion];
    };
}


@end
