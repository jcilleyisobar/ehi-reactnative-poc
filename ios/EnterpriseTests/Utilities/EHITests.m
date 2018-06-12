//
//  EHITests.m
//  Enterprise
//
//  Created by Ty Cobb on 1/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"

static NSTimeInterval ehi_asyncSpecTimeout = 10.0;

@implementation EHITests

void ehi_waitUntil(BOOL (^exitCondition)(void), void (^block)(void))
{
    // start the block with async operations
    block();
    
    // loop while force our run loop to flush (async operations are queued here)
    NSDate *loopUntil = [NSDate dateWithTimeIntervalSinceNow:ehi_asyncSpecTimeout];
    while(!exitCondition() && [loopUntil timeIntervalSinceNow] > 0) {
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:loopUntil];
    }
}

@end

@implementation EHIModel (Mock)

+ (instancetype)mock:(NSString *)filename
{
    // load the data from file
    filename = [[NSBundle mainBundle] pathForResource:filename ofType:@"json"];
    NSData *data = [NSData dataWithContentsOfFile:filename];
   
    // creat the json data and the model from that data
    id attributes = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    if(attributes && [attributes isKindOfClass:[NSDictionary class]]) {
        return [self modelWithDictionary:attributes];
    }
    
    return nil;
}

@end

@implementation NSNumber (Functional)

- (NSArray *(^)(id (^)(NSInteger)))generate
{
    return ^(id(^generator)(NSInteger)) {
        NSInteger count = self.integerValue;
       
        NSMutableArray *result = [[NSMutableArray alloc] initWithCapacity:count];
        for(int index=0 ; index<count ; index++) {
            id object = generator(index);
            if(object) {
                [result addObject:object];
            }
        }
        
        return result;
    };
}

@end

@implementation EHIUserManager (Tests)

+ (void)loginEnterprisePlusTestUser
{
    [self loginWithMockCredentials:@"credentials_ep"];
}

+ (void)loginEmeraldClubTestUser
{
    [self loginWithMockCredentials:@"credentials_ec"];
}

//
// Helpers
//

+ (void)loginWithMockCredentials:(NSString *)mockCredentials
{
    EHIUserCredentials *credentials = [EHIUserCredentials mock:mockCredentials];
    waitUntil(^(DoneCallback done) {
        [[self sharedInstance] authenticateUserWithCredentials:credentials handler:^(EHIUser *user, EHIServicesError *error) {
            done();
        }];
    });
}

@end
