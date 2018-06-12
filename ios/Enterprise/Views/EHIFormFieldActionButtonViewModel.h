//
//  EHIFormFieldActionButtonViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 8/4/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIFormFieldButtonViewModel.h"

@interface EHIFormFieldActionButtonViewModel : EHIFormFieldButtonViewModel <MTRReactive>
@property (assign, nonatomic) BOOL isFauxDisabled;
@end
