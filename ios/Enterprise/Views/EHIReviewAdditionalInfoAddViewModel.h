//
//  EHIReviewAdditionalInfoAddViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReviewAdditionalInfoAddViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithAdditionalInfo:(NSArray *)info;

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *details;
@property (copy, nonatomic, readonly) NSString *addInfoTitle;

@end
