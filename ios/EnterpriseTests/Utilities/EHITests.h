//
//  EHITests.h
//  Enterprise
//
//  Created by Ty Cobb on 1/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import Foundation;

#define EXP_SHORTHAND
#define MOCKITO_SHORTHAND

#import <Specta/Specta.h>
#import <Expecta/Expecta.h>
#import <OCMockito/OCMockito.h>
#import <YOLOKit/YOLO.h>

#import "EHIMatchers.h"
#import "EHIMacros.h"
#import "EHIGeometry.h"
#import "EHIModel.h"
#import "EHIMapTransformer.h"
#import "EHILocalization.h"
#import "UIColor+EHIColor.h"
#import "EHILoyaltyTierDataProvider.h"
#import "EHIPrice.h"
#import "EHIUserManager.h"

#import "UIFont+EHIFont.h"
#import "NSAttributedString+Construction.h"
#import "EHIPriceFormatter.h"
#import "NSString+Formatting.h"

#import "NSCollections+Functional.h"

#define fail() expect(@"failed explicitly").to.equal(@"no failures")
#define unimplemented() expect(@"a test").to.beTruthy()

@interface EHITests : NSObject

/**
 @brief Runs a block and exits when a certain condition is met
 
 Code that is dispatched to the main queue is not automatically run during tests. 
 If a certain method causes asynchronous behavior, wrap it in this function to ensure
 any code dispatched to the main queue is run. This function waits until a predetermined
 timeout or the exit condition returns @c YES. If the method you use has a return handler,
 you can use the more straightforward @c waitUntil(void (^block)(DoneCallback done)); method 
 provided by Specta.
 */

void ehi_waitUntil(BOOL (^exitCondition)(void), void (^block)(void));

@end

@interface EHIModel (Mock)

/**
 @brief Attempts to instantiate a model with data from disk
 
 The data is assumed to be a JSON file in the test target's bundle with the @c filename
 specified.

 @param filename The mock data's filename
 
 @return A model instance built from this data
*/

+ (instancetype)mock:(NSString *)filename;

@end

@interface NSNumber (Functional)

/**
 @brief Generates an array of the size specified by the callee
 
 This method returns a block which can be called with another generator block that 
 creates an object for each index.
 
 @code 
 @10.generate(^(NSInteger index) {
    return [Model new];
 });
 @endcode
*/

- (NSArray *(^)(id(^)(NSInteger)))generate;

@end

@interface EHIUserManager (Tests)

/** Mocks a user manager with a logged in enterprise plus user */
+ (void)loginEnterprisePlusTestUser;

/** Mocks a user manager with a logged in emerald club user */
+ (void)loginEmeraldClubTestUser;

@end
