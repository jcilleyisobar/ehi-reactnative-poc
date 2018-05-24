//
//  EHIActionSheetBuilder.h
//  Enterprise
//
//  Created by fhu on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void(^EHIActionSheetCompletion)(NSInteger selectedIndex, BOOL canceled);
typedef void(^EHIActionSheetButtonAction)();

@interface EHIActionSheetBuilder : NSObject

- (EHIActionSheetBuilder *)withTitle:(NSString *)title;
- (EHIActionSheetBuilder *)withButtonTitle:(NSString *)title;
- (EHIActionSheetBuilder *)withCancelButtonTitle:(NSString *)title;
- (void)showWithCompletion:(EHIActionSheetCompletion)completion;
- (void)showExecutingButtonAction;

- (EHIActionSheetBuilder *(^)(NSString *))title;
- (EHIActionSheetBuilder *(^)(NSString *))button;
- (EHIActionSheetBuilder *(^)(NSString *, void (^)()))buttonWithAction;
- (EHIActionSheetBuilder *(^)(NSString *))cancelButton;
- (void(^)(EHIActionSheetCompletion))show;

@end
