//
//  EHIFormFieldButtonViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/16/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"

typedef void (^EHIFormFieldButtonAction)();

@interface EHIFormFieldButtonViewModel : EHIFormFieldViewModel <MTRReactive>
- (void)performButtonAction;
@end