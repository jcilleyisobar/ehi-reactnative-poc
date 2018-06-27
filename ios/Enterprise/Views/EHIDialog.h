//
//  EHIDialog.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/25/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIDialogModel.h"

@interface EHIDialog : NSObject

- (instancetype)initWithStyle:(UIAlertControllerStyle)style;

- (NSArray <UITextField *>*)textFields;
- (void)show:(EHIDialogModel *)target;

@end
