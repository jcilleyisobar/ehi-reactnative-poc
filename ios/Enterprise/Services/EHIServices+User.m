//
//  EHIServices+Authentication.m
//  Enterprise
//
//  Created by Ty Cobb on 1/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices_Private.h"
#import "EHIServices+User.h"
#import "EHIUserManager.h"
#import "EHIDataStore.h"
#import "EHIServices+Contracts.h"
#import "EHICreditCardProfileRequest.h"
#import "EHIUserPaymentMethodRequest.h"

@implementation EHIServices (User)

- (id<EHINetworkCancelable>)authenticateUserWithCredentials:(EHIUserCredentials *)credentials handler:(void (^)(EHIUser *, EHIServicesError *))handler
{
#if EHIUserMockLogin || TESTS
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://user_profile.json"];
#else
    EHINetworkRequest *request = [[EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile post:@"/profiles/%@/%@/EP/login", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey] body:^(EHINetworkRequest *request) {
        [credentials encodeWithRequest:request];
    }];
#endif

    return [self startRequest:request parseModel:[EHIUser class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)authenticateEmeraldClubUserWithCredentials:(EHIUserCredentials *)credentials handler:(void(^)(EHIUser *user, EHIServicesError *error))handler
{
#if EHIUserMockLogin || TESTS
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://user_profile.json"];
#else
    EHINetworkRequest *request = [[EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile post:@"/profiles/%@/%@/EC/login", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey] body:^(EHINetworkRequest *request) {
        [credentials encodeWithRequest:request];
    }];
#endif
    
    return [self startRequest:request parseModel:[EHIUser class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)refreshUser:(EHIUser *)user handler:(void(^)(EHIUser *user, EHIServicesError *error))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile get:@"/profiles/%@/%@/EP/profile?individualId=%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, user.individualId];
    
    return [self startRequest:request updateModel:user asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)changePassword:(NSString *)password confirmation:(NSString *)confirmation hander:(void(^)(EHIUser *user, EHIServicesError *error))handler
{
    // capture the user to update locally
    EHIUser *user = [EHIUser currentUser];
   
    // create the request
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile put:@"/profiles/%@/%@/profile/password?individualId=%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, user.profiles.individualId];
    [request body:^(EHINetworkRequest *request) {
        request[@"new_password"] = password;
        request[@"confirm_new_password"] = confirmation;
        request[@"remembers_credentials"] = EHIStringifyFlag([EHIUserManager sharedInstance].credentials.remembersCredentials);
    }];
    
    return [self startRequest:request updateModel:user asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)resetPassword:(EHIUser *)user handler:(void(^)(EHIServicesError *error))handler
{
    // create the request
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile put:@"profiles/%@/%@/profile/password/reset", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey];

    [request body:^(EHINetworkRequest *request) {
        request[@"first_name"] = user.firstName;
        request[@"last_name"]  = user.lastName;
        request[@"email"]      = user.contact.email;
    }];
    
    return [self startRequest:request handler:^(id response, EHIServicesError *error){
        ehi_call(handler)(error);
    }];
}

- (id<EHINetworkCancelable>)fetchCountriesPurgingData:(BOOL)purge handler:(void(^)(NSArray *countries, EHIServicesError *error))handler
{
    NSArray *countries = nil;
    
    if(purge) {
        [EHIDataStore purge:[EHICountry class] handler:nil];
    }
    else {
        countries = [EHIDataStore findInMemory:[EHICountry class]];
    }
    
    // invoke callback with in memory cache if available
    if(countries) {
        ehi_call(handler)(countries, nil);
        return nil;
    }
    
    // otherwise, get countries via network
    else {
#if EHIUserMockProfileOptions
        EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://countries.json"];
#else
        EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOLocation get:@"countries/%@/%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey];
#endif
        
        return [self startRequest:request handler:^(id response, EHIServicesError *error) {
            NSArray *countries = ((NSArray *)response[@"countries"] ?: @[])
            .map(^(NSDictionary *country){
                NSDictionary *countryContent = [country[@"country_content"] copy];
                return [[countryContent ehi_appendKey:@"country_name"
                                                value:country[@"country_name"]]
                                        ehi_appendKey:@"contracts"
                                                value:country[@"contracts"]];
            }).map(^(NSDictionary *country){
                return [EHICountry modelWithDictionary:country];
            });
            
            // completion blocks
            void (^dataStoreCompletionHandler)(BOOL didSucceed) = ^(BOOL didSucceed){
                EHICountry *currentCountry = [NSLocale ehi_country];
                EHIPromotionContract *weekendSpecial = [currentCountry weekendSpecial];
                if(weekendSpecial) {
                    [[EHIServices sharedInstance] fetchPromotion:weekendSpecial handler:^(EHIPromotionContract *promotion, EHIServicesError *error) {
                        currentCountry.weekendSpecial = !error ? promotion : nil;
                        ehi_call(handler)(countries, error);
                    }];
                }
                else {
                    ehi_call(handler)(countries, error);
                }
            };
            
            // cache returned countries
            [EHIDataStore saveAll:countries handler:dataStoreCompletionHandler];
        }];
    }
}

- (id<EHINetworkCancelable>)fetchCountriesWithHandler:(void(^)(NSArray *countries, EHIServicesError *error))handler
{
    return [self fetchCountriesPurgingData:NO handler:handler];
}

- (id<EHINetworkCancelable>)fetchRegionsForCountry:(EHICountry *)country handler:(void(^)(NSArray *regions, EHIServicesError *error))handler
{
    // invoke callback with in memory cache if available
    if(country.regions) {
        ehi_call(handler)(country.regions, nil);
        return nil;
    }
    
#if EHIUserMockProfileOptions
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeNone get:@"mock://regions.json"];
#else
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOLocation get:@"countries/%@/%@/%@/stateAndProvince", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, country.code];
#endif
    
    // get regions for country model via network
    return [self startRequest:request handler:^(id response, EHIServicesError *error) {

        NSArray *regions = response[@"region_lists"] ?: @[];
        country.regions = regions.map(^(NSDictionary *region){
            return [EHIRegion modelWithDictionary:region];
        }).sort;

        ehi_call(handler)(country.regions, error);
        
        // cache country model with regions
        [EHIDataStore save:country handler:nil];
    }];
}

- (id<EHINetworkCancelable>)updateUser:(EHIUser *)user withUser:(EHIUser *)newUser handler:(void(^)(EHIUser *user, EHIServicesError *error))handler
{
    NSString *individualId  = [EHIUser currentUser].individualId ?: @"";
    NSString *loyaltyNumber = [EHIUser currentUser].loyaltyNumber ?: @"";
    EHIUserAdditionalData *additionalData = [EHIUserAdditionalData modelForProfileUpdate];
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile put:@"profiles/%@/%@/EP/profile?individualId=%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, individualId];

    [request headers:^(EHINetworkRequest *request) {
        request[EHIRequestHeaderContentType] = EHIRequestParamJSONCharsetUTF8;
    }];

    [request body:^(EHINetworkRequest *request) {
        request[@"loyalty_number"]  = loyaltyNumber;
        request[@"additional_info"] = additionalData;
        [newUser encodeWithRequest:request];
    }];
    
    return [self startRequest:request parseAsynchronously:YES withBlock:^id(id responseData) {
        [user updateWithDictionary:responseData forceDeletions:NO];
        return user;
    } handler:handler];
}

- (id<EHINetworkCancelable>)searchRenter:(EHIUserProfileFetch *)fetchModel handler:(void(^)(EHIUser *profile, EHIServicesError *error))handler
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile post:@"profiles/%@/%@/searchProfile/driverLicenseCriteria", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey];
    
    [request body:^(EHINetworkRequest *request) {
        [fetchModel encodeWithRequest:request];
    }];

    return [self startRequest:request parseModel:[EHIUser class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)createEnrollProfile:(EHIEnrollProfile *)profile handler:(void(^)(EHIUser *user, EHIServicesError *error))handler
{
    EHIUserAdditionalData *additionalData = [EHIUserAdditionalData modelForProfileCreation];
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile
                                                       post:@"profiles/%@/%@/EP/profile", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey];

    [request body:^(EHINetworkRequest *request) {
        [profile encodeWithRequest:request];
        request[@"additional_info"]            = additionalData;
        request[@"request_origin_channel"]     = @"NONEXPEDITED";
        request[@"preference"][@"source_code"] = kEHIServicesParameterEnrollSourceCodeKey;
    }];
    
    return [self startRequest:request parseModel:[EHIUser class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)cloneEnrollProfile:(EHIEnrollProfile *)profile handler:(void(^)(EHIUser *user, EHIServicesError *error))handler;
{
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile
                                                       post:@"profiles/%@/%@/EP/profile/clone", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey];
    
    [request headers:^(EHINetworkRequest *request) {
        request[EHIRequestHeaderContentType] = EHIRequestParamJSONCharsetUTF8;
    }];
    
    [request body:^(EHINetworkRequest *request) {
        [profile encodeWithRequest:request];
        request[@"request_origin_channel"]     = @"NONEXPEDITED";
        request[@"preference"][@"source_code"] = kEHIServicesParameterEnrollSourceCodeKey;
    }];
    
    return [self startRequest:request parseModel:[EHIUser class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)fetchCardSubmissionTokenWithHandler:(void (^)(EHICreditCardSubmissionToken *, EHIServicesError *))handler {
    NSString *individualId  = [EHIUser currentUser].individualId ?: @"";
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile post:@"profiles/%@/%@/profile/payment/cardSubmissionKey?individualId=%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, individualId];
    
    return [self startRequest:request parseModel:[EHICreditCardSubmissionToken class] asynchronously:YES handler:handler];
}

- (id<EHINetworkCancelable>)addCreditCard:(EHICreditCard *)card withToken:(NSString *)token handler:(void (^)(EHIUserPaymentMethod *, EHIServicesError *))handler
{
    NSString *individualId = EHIUser.currentUser.individualId;
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile post:@"profiles/%@/%@/profile/payment?individualId=%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, individualId];

    EHIUserPaymentMethod *newPaymentMethod = [EHIUserPaymentMethod modelWithDictionary:@{
        @key(newPaymentMethod.paymentReferenceId) : token ?: @"",
        @key(newPaymentMethod.isPreferred)        : @(NO),
        @key(newPaymentMethod.expirationDate)     : [[NSString stringWithFormat:@"%ld-%ld",
                                                      (long)card.expirationYear,
                                                      (long)card.expirationMonth] ehi_dateWithFormat:@"yyyy-MM"],
    }];
    
    EHIUserAdditionalData *additionalData = [EHIUserAdditionalData modelForProfileCreation];
    [request body:^(EHINetworkRequest *request) {
        [additionalData encodeWithRequest:request];
        request[@"payment_method"] = newPaymentMethod;
    }];
    
    return [self startRequest:request parseAsynchronously:YES withBlock:^id(id responseData) {
        EHIUserPaymentProfile *payment = [EHIUserPaymentProfile modelWithDictionary:responseData[@"payment_profile"]];
        [self updateProfileWithPayment:payment];
        return payment;
    } handler:handler];
}

- (id<EHINetworkCancelable>)deletePaymentMethod:(EHIUserPaymentMethod *)paymentMethod handler:(void (^)(EHIUserPaymentProfile *, EHIServicesError *))handler
{
    NSString *individualId       = EHIUser.currentUser.individualId;
    NSString *paymentReferenceId = paymentMethod.paymentReferenceId;
    EHINetworkRequest *request = [EHINetworkRequest service:EHIServicesEnvironmentTypeGBOProfile update:@"profiles/%@/%@/profile/payment?individualId=%@&paymentReferenceId=%@", kEHIServicesBrandPathKey, kEHIServicesChannelPathKey, individualId, paymentReferenceId];
    
    return [self startRequest:request parseAsynchronously:YES withBlock:^id(id responseData) {
        EHIUserPaymentProfile *payment = [EHIUserPaymentProfile modelWithDictionary:responseData[@"payment_profile"]];
        [self updateProfileWithPayment:payment];
        return payment;
    } handler:handler];
}

# pragma mark - Helpers

- (NSString *)username
{
    return [EHIUser currentUser].profiles.basic.loyalty.number;
}

- (void)updateProfileWithPayment:(EHIUserPaymentProfile *)payment
{
    if(payment) {
        EHIUser.currentUser.payment.paymentMethods = payment.paymentMethods;
    }
}

@end
