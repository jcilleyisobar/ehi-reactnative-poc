//
//  EHIPromotionContract.h
//  Enterprise
//
//  Created by Rafael Ramos on 3/30/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIContractDetails.h"

typedef NS_ENUM(NSInteger, EHIPromotionContractType) {
    EHIPromotionContractTypeUnkown,
    EHIPromotionContractTypeWeekendSpecial,
    EHIPromotionContractTypeLastMinuteSpecial,
    EHIPromotionContractTypeMondayThrusdayPromotions,
    EHIPromotionContractTypeSaturdayNightStayPromotions,
};

@interface EHIPromotionContract : EHIContractDetails
@property (copy  , nonatomic, readonly) NSString *code;
@property (strong, nonatomic, readonly) NSArray<NSString* >*descriptions;
@property (assign, nonatomic, readonly) EHIPromotionContractType type;
@end

EHIAnnotatable(EHIPromotionContract);
