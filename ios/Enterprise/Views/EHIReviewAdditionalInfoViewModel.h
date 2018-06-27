//
//  EHIReviewAdditionalInfoViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIContractAdditionalInfo.h"
#import "EHIReviewAdditionalInfoAddViewModel.h"

typedef NS_ENUM(NSInteger, EHIReviewAdditionalSection) {
    EHIReviewAdditionalSectionAddInfo,
    EHIReviewAdditionalSectionItems,
};

@interface EHIReviewAdditionalInfoViewModel : EHIViewModel <MTRReactive>

- (instancetype)initWithAdditionalInfo:(NSArray *)info;

@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic, readonly) EHIReviewAdditionalInfoAddViewModel *addModel;
@property (strong, nonatomic, readonly) NSArray *itemModels;
@property (assign, nonatomic, readonly) BOOL hideArrow;

- (void)showAdditionalInfo;

@end
