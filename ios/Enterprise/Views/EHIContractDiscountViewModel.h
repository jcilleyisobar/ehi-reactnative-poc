//
//  EHIContractDiscountViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 5/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIContractDiscoutFlow) {
    EHIContractDiscoutFlowCarClassSelect,
    EHIContractDiscoutFlowConfirmation
};

@interface EHIContractDiscountViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSAttributedString *title;
@property (copy  , nonatomic, readonly) NSString *code;
@property (copy  , nonatomic, readonly) NSString *codePrefix;
@property (copy  , nonatomic, readonly) NSString *iconName;
@property (copy  , nonatomic, readonly) NSString *terms;
@property (copy  , nonatomic, readonly) NSString *termsButtonTitle;
@property (assign, nonatomic, readonly) BOOL shouldShowTerms;
@property (assign, nonatomic, readonly) BOOL shouldShowSubtitle;

- (instancetype)initWithFlow:(EHIContractDiscoutFlow)flow;

- (void)didTapPolicies;

@end
