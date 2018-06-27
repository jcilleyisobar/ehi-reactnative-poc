//
//  EHIDealsConfiguration.m
//  Enterprise
//
//  Created by Rafael Ramos on 18/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIDealsConfiguration.h"
#import "EHIPromotionContract.h"
#import "EHIServices+Deals.h"

@interface EHIDealsConfiguration ()
@property (copy  , nonatomic) NSArray<EHIDeals> *deals;
@property (assign, nonatomic) BOOL isReady;
@property (assign, nonatomic) BOOL isFetching;
@property (strong, nonatomic) NSMutableArray *readinessHandlers;
@end

@implementation EHIDealsConfiguration

+ (NSDictionary *)mappings:(EHIDealsConfiguration *)model
{
    return @{
        @"dealsEnabled" : @key(model.enabled),
        @"promotions"   : @key(model.deals),
    };
}

+ (EHIDealsConfiguration *)configuration
{
    static EHIDealsConfiguration *sharedInstance;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [self new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if(self = [super init]) {
        _readinessHandlers = [NSMutableArray array];
    }
    
    return self;
}

- (NSString *)countryLanguageDeals
{
    return [[NSString alloc] initWithFormat:@"deals.%@.%@.json", [NSLocale ehi_region].uppercaseString, [NSLocale ehi_language]];
}

- (NSString *)countryDeals
{
    return [[NSString alloc] initWithFormat:@"deals.%@.json", [NSLocale ehi_region].uppercaseString];
}

- (EHIPromotionContract *)weekendSpecial
{
    return [[NSLocale ehi_country] weekendSpecial];
}

- (EHIDeals *)local
{
    return (self.deals ?: @[]).find(^(EHIDeals *deals){
        return deals.type == EHIDealsTypeLocal;
    });
}

- (EHIDeals *)internacional
{
    return (self.deals ?: @[]).find(^(EHIDeals *deals){
        return deals.type == EHIDealsTypeInternacional;
    });
}

- (EHIDeals *)other
{
    return (self.deals ?: @[]).find(^(EHIDeals *deals){
        return deals.type == EHIDealsTypeUnknown;
    });
}

- (void)fetchConfiguration
{
    dispatch_group_t group = dispatch_group_create();
    
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] fetchConfiguration:self handler:^(EHIDealsConfiguration *configuration, EHIServicesError *error) {
        [error consume];
        
        dispatch_group_leave(group);
    }];
    
    dispatch_group_enter(group);
    [[EHIServices sharedInstance] fetchDeals:self handler:^(EHIDealsConfiguration *configuration, EHIServicesError *error) {
        [error consume];
        
        dispatch_group_leave(group);
    }];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        // we're ready as long as we didn't error
        self.isReady    = YES;
        self.isFetching = NO;
        
        // call back the appropriate handlers
        [self notifyHandlers:self.isReady];
    });
}

- (void)notifyHandlers:(BOOL)isReady
{
    NSMutableIndexSet *indicesForNotifiedHandlers = [NSMutableIndexSet new];
    
    (self.readinessHandlers ?: @[]).each(^(EHIConfigurationHandler *handler, NSInteger index) {
        if(isReady) {
            handler.block(isReady);
            [indicesForNotifiedHandlers addIndex:index];
        }
    });
    
    [self.readinessHandlers removeObjectsAtIndexes:indicesForNotifiedHandlers];
}

@end

@implementation EHIDealsConfiguration (Readiness)

- (EHIConfigurationHandler *)onReady:(EHIConfigurationCallback)callback
{
    BOOL shouldFetch = !(self.isReady || self.isFetching);
    if(shouldFetch) {
        self.isFetching = YES;
        
        [self fetchConfiguration];
    }
    
    EHIConfigurationHandler *handler = nil;
    
    if(self.isReady) {
        callback(YES);
    } else {
        handler = [[EHIConfigurationHandler alloc] initWithBlock:callback];
        [self.readinessHandlers addObject:handler];
    }
    
    return handler;
}

@end
