//
//  EHIDealsConfiguration.h
//  Enterprise
//
//  Created by Rafael Ramos on 18/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIDeals.h"
#import "EHIConfigurationHandler.h"

@class EHIPromotionContract;
@interface EHIDealsConfiguration : EHIModel
@property (assign, nonatomic, readonly) BOOL enabled;
@property (strong, nonatomic, readonly) EHIPromotionContract *weekendSpecial;
@property (copy  , nonatomic, readonly) EHIDeals *local;
@property (copy  , nonatomic, readonly) EHIDeals *internacional;
@property (copy  , nonatomic, readonly) EHIDeals *other;
@property (copy  , nonatomic, readonly) NSString *countryLanguageDeals;
@property (copy  , nonatomic, readonly) NSString *countryDeals;

/** Accesses the shared configuration */
+ (EHIDealsConfiguration *)configuration;

@end

@interface EHIDealsConfiguration (Readiness)
- (nullable EHIConfigurationHandler *)onReady:(EHIConfigurationCallback)callback;
@end
