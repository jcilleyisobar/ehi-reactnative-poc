//
//  EHIDeals.h
//  Enterprise
//
//  Created by Rafael Ramos on 18/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIDealInfo.h"

typedef NS_ENUM(NSInteger, EHIDealsType) {
    EHIDealsTypeUnknown,
    EHIDealsTypeLocal,
    EHIDealsTypeInternacional
};

@interface EHIDeals : EHIModel
@property (copy  , nonatomic, readonly) NSString *displayName;
@property (copy  , nonatomic, readonly) NSArray<EHIDealInfo> *deals;
@property (assign, nonatomic, readonly) EHIDealsType type;
@end

EHIAnnotatable(EHIDeals);
