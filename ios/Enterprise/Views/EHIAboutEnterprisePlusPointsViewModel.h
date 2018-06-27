//
//  EHIAboutEnterprisePlusPointsViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIAboutEnterprisePlusPointsType) {
    EHIAboutEnterprisePlusPointsEarn,
    EHIAboutEnterprisePlusPointsRedeem,
    EHIAboutEnterprisePlusPointsTransfer
};

@interface EHIAboutEnterprisePlusPointsViewModel : EHIViewModel <MTRReactive>
@property (assign, nonatomic, readonly) EHIAboutEnterprisePlusPointsType type;
@property (copy  , nonatomic, readonly) NSString *header;
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *detail;
@property (copy  , nonatomic, readonly) NSString *iconImageName;

@property (assign, nonatomic, readonly) BOOL isLast;

+ (NSArray *)all;

@end
