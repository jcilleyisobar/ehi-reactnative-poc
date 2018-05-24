//
//  EHIKeyFactsFooterViewModel.h
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIKeyFactsFooterViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *headerText;
@property (copy, nonatomic) NSString *subHeaderText;
@property (copy, nonatomic) NSString *phoneLinkText;
@property (copy, nonatomic) NSString *emailLinkText;
@property (copy, nonatomic) NSString *footerText;
@property (copy, nonatomic) NSString *footerLinkText;

@property (assign, nonatomic) BOOL shouldHideTopDivider;

- (void)emailTapped;
- (void)footerTapped;
- (void)phoneTapped;

@end
