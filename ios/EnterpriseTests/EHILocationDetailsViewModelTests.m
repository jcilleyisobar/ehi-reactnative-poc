//
//  EHILocationDetailsViewModelTests.m
//  Enterprise
//
//  Created by Ty Cobb on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationDetailsViewModel.h"

SpecBegin(EHILocationDetailsViewModelTests)

describe(@"the location details view model", ^{
  
    __block EHILocationDetailsViewModel *model;
    
    beforeAll(^{
        model = [EHILocationDetailsViewModel new];
    });
    
    it(@"should have a title", ^{
        expect(model.title).to.localizeFrom(@"location_details_title");
    });
    
    it(@"should have section headers", ^{
        NSDictionary *headers = @{
            @(EHILocationDetailsSectionInfo)     : @"location_details_info_header",
            @(EHILocationDetailsSectionHours)    : @"location_details_hours_header",
            @(EHILocationDetailsSectionPickup)   : @"location_details_pickup_header",
            @(EHILocationDetailsSectionPolicies) : @"location_details_policies_header",
        };
      
        headers.each(^(NSNumber *section, NSString *key) {
            EHISectionHeaderModel *header = [model headerForSection:section.integerValue];
            expect(header.title).to.localizeFrom(key);
        });
    });
    
    it(@"should not have a map section header", ^{
        expect([model headerForSection:EHILocationDetailsSectionMap]).to.beNil();
    });
    
    context(@"before it has fetched details", ^{
        
        beforeAll(^{
            [model updateWithModel:[EHILocation mock:@"city_stub"]];
        });
       
        it(@"should provide a location", ^{
            expect(model.location).toNot.beNil();
        });
       
        it(@"should not provide hours", ^{
            expect(model.hours).to.beNil();
        });
       
        it(@"should not proivde policies", ^{
            expect(model.policies).to.beNil();
        });
        
    });
    
    context(@"after it has fetched details", ^{
        
        beforeAll(^{
            EHILocation *city = [EHILocation mock:@"city"];
            city.hours = [EHILocationHours modelWithDictionary:@{
                @"data": @{
                   @"2017-12-20": @{
                       @"STANDARD" : @{
                           @"closed": @"0",
                           @"hours": @[
                               @{
                                   @"close": @"17:00",
                                   @"open":  @"08:00"
                                }
                            ],
                           @"open24Hours": @"0"
                        }
                    }
                }
            }];
            
            [model updateWithModel:city];
        });
        
        it(@"should provide hours", ^{
            // it would be good if we can figure out how to dynamically mock locations, since
            // we want to test that the hours are the next 7 days

			// TODO: implement mock that always includes current hours data
			//expect(model.hours.count).toNot.equal(0);
			expect(model.hours.count).toNot.equal(0);
        });
        
        it(@"should provide policies", ^{
            expect(model.allPolicies.count).toNot.equal(0);
        });
        
        it(@"should provide pickup location", ^{
            expect(model.pickupLocation).toNot.beNil();
        });
        
        context(@"if there are more than 3 policies", ^{
          
            it(@"should truncate the policy list", ^{
                expect(model.allPolicies.count).to.beGreaterThan(3);
                expect(model.policies.count).to.equal(4);
            });
            
            it(@"should sort the policies by code", ^{
                NSArray *policies = model.policies.first(3);
                policies.reduce(^(EHILocationPolicy *previous, EHILocationPolicy *policy) {
                    expect(previous.code).to.beLessThan(policy.code);
                    return policy;
                });
            });
            
        });
        
        context(@"when location is airport", ^{
            
            beforeAll(^{
                [model updateWithModel:[EHILocation mock:@"airport"]];
            });
            
            it(@"should not provide We'll Pick You Up Section", ^{
                expect(model.pickupLocation).to.beFalsy();
            });
            
        });
        
        context(@"when location is not On Brand", ^{
            
            beforeAll(^{
                [model updateWithModel:[EHILocation mock:@"city2"]];
            });
            
            it(@"should not provide We'll Pick You Up Section", ^{
                expect(model.pickupLocation).to.beTruthy();
            });
            
        });
        
        context(@"when location has conflicts", ^{
            
            beforeAll(^{
            EHILocation *location = [EHILocation modelWithDictionary:@{
                @"pickupValidity" : @{
                    @"validityType": @"INVALID_AT_THAT_TIME"
                }
            }];
                [model updateWithModel:location];
          });
            
            it(@"should show conflicts", ^{
                expect(model.conflictsModel).notTo.beNil();
            });
            
        });
        
    });

});

SpecEnd
