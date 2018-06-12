//
//  EHIConfirmationAdditionalInfoViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 7/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIContractDetails.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIConfirmationAdditionalInfoViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic) NSString *title;
@property (copy, nonatomic) NSString *name;
@property (copy, nonatomic) NSAttributedString *value;
@property (assign, nonatomic) BOOL shouldShowSectionTitle;
@property (assign, nonatomic) BOOL isLastInSection;

@end

NS_ASSUME_NONNULL_END