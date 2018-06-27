//
//  EHIEnrollmentStepTwoMatchViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 05/01/18.
//Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIEnrollmentStepTwoMatchViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *stepTitle;
@property (copy, nonatomic, readonly) NSString *matchMessage;
@property (copy, nonatomic, readonly) NSString *addressTitle;
@property (copy, nonatomic, readonly) NSString *formattedAddress;
@property (copy, nonatomic, readonly) NSString *changeTitle;
@property (copy, nonatomic, readonly) NSString *keepTitle;

- (void)didTapChange;
- (void)didTapKeep;

@end
