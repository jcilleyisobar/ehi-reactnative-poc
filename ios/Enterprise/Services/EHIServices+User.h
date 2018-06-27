//
//  EHIServices+Authentication.h
//  Enterprise
//
//  Created by Ty Cobb on 1/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIServices.h"
#import "EHIUser.h"
#import "EHIUserProfileFetch.h"
#import "EHIUserCredentials.h"
#import "EHICountry.h"
#import "EHIEnrollProfile.h"
#import "EHICreditCard.h"
#import "EHICreditCardSubmissionToken.h"
#import "EHIUserPaymentMethod.h"

@interface EHIServices (User)
- (id<EHINetworkCancelable>)authenticateUserWithCredentials:(EHIUserCredentials *)credentials handler:(void(^)(EHIUser *user, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)authenticateEmeraldClubUserWithCredentials:(EHIUserCredentials *)credentials handler:(void(^)(EHIUser *user, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)refreshUser:(EHIUser *)user handler:(void(^)(EHIUser *user, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)changePassword:(NSString *)password confirmation:(NSString *)confirmation hander:(void(^)(EHIUser *user, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)resetPassword:(EHIUser *)user handler:(void(^)(EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)fetchCountriesPurgingData:(BOOL)purge handler:(void(^)(NSArray *countries, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)fetchCountriesWithHandler:(void(^)(NSArray *countries, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)fetchRegionsForCountry:(EHICountry *)country handler:(void(^)(NSArray *regions, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)updateUser:(EHIUser *)user withUser:(EHIUser *)newUser handler:(void(^)(EHIUser *user, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)createEnrollProfile:(EHIEnrollProfile *)profile handler:(void(^)(EHIUser *user, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)cloneEnrollProfile:(EHIEnrollProfile *)profile handler:(void(^)(EHIUser *user, EHIServicesError *error))handler;
- (id<EHINetworkCancelable>)searchRenter:(EHIUserProfileFetch *)fetchModel handler:(void(^)(EHIUser *profile, EHIServicesError *error))handler;

- (id <EHINetworkCancelable>)fetchCardSubmissionTokenWithHandler:(void (^)(EHICreditCardSubmissionToken *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)addCreditCard:(EHICreditCard *)card withToken:(NSString *)token handler:(void (^)(EHIUserPaymentMethod *, EHIServicesError *))handler;
- (id<EHINetworkCancelable>)deletePaymentMethod:(EHIUserPaymentMethod *)paymentMethod handler:(void (^)(EHIUserPaymentProfile *, EHIServicesError *))handler;
@end
