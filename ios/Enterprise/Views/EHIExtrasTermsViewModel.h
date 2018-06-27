//
//  EHIExtrasTermsViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 16/04/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIExtrasTermsViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;

- (void)showTerms;

@end
