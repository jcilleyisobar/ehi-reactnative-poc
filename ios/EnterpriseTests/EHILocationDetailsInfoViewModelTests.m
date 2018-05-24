//
//  EHILocationDetailsInfoViewModelTests.m
//  Enterprise
//
//  Created by Ty Cobb on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationDetailsInfoViewModel.h"
#import "EHIMapping.h"

SpecBegin(EHILocationDetailsInfoViewModelTests)

describe(@"the location details info view model", ^{
   
    __block EHILocation *location;
    __block EHILocationDetailsInfoViewModel *model;
    
    beforeAll(^{
        location = [EHILocation mock:@"city"];
        model = [[EHILocationDetailsInfoViewModel alloc] initWithModel:location];
    });
    
    it(@"should show the location's name as the title", ^{
        expect(model.title.string).to.equal(location.displayName);
    });
    
    it(@"should show the location's formatted address", ^{
        expect(model.address).to.equal([location.address formattedAddress:YES]);
    });
    
    it(@"should show the location's phone number", ^{
        expect(model.phoneNumber).to.equal(location.formattedPhoneNumber);
    });
    
    it(@"shouldn't show the wayfinding button", ^{
        expect(model.hasWayfindingDirections).to.beFalsy();
        expect(model.wayfindings.count).to.equal(0);
    });
    
    it(@"should hide the after hours label", ^{
        expect(model.afterHoursTitle).to.beNil();
        expect(model.hideAfterHours).to.beTruthy();
    });
    
    context(@"before it's favorited", ^{
        
        it(@"should not be favorited", ^{
            expect(model.isFavorited).to.beFalsy();
        });
        
        it(@"should show the unfavorited button title", ^{
            expect(model.favoritesTitle).to.localizeFrom(@"location_details_not_favorited_title");
        });

    });
    
    context(@"after it's favorited and location is OnBrand", ^{

        beforeAll(^{
            [model toggleIsFavorited];
        });
        
//        it(@"should be favorited", ^{
//            expect(model.isFavorited).to.beTruthy();
//        });
//
//        it(@"should show the favorited button title", ^{
//            expect(model.favoritesTitle).to.localizeFrom(@"location_details_favorited_title");
//        });
        
    });
    
    context(@"when it has an airport", ^{
        
        beforeAll(^{
            location = [EHILocation mock:@"airport"];
            [model updateWithModel:location];
        });
        
        it(@"should show the location's name with the airport code appended to the end as the title", ^{
            expect(model.title.string).to.contain(location.displayName);
        });
        
        it(@"should show the wayfinding button", ^{
            expect(model.hasWayfindingDirections).to.beTruthy();
            expect(model.wayfindings.count).toNot.equal(0);
        });
        
    });
    
    context(@"when location is not on brand", ^{
        
        beforeAll(^{
            model = [[EHILocationDetailsInfoViewModel alloc] initWithModel:[EHILocation mock:@"city2"]];
        });
        
        context(@"after it's favorited", ^{
            
            beforeAll(^{
                [model toggleIsFavorited];
            });
            
//            it(@"should not be favorited", ^{
//                expect(model.isFavorited).to.beFalsy();
//            });
            
//            it(@"should not show favorite button title", ^{
//                expect(model.favoritesTitle).to.localizeFrom(@"location_details_not_favorited_title");
//            });
            
        });
        
        context(@"should not have wayfinding directions", ^{
            expect(model.hasWayfindingDirections).to.beFalsy();
        });
        
    });
    
    context(@"when location has dropff time conflicts", ^{
		__block EHILocation *conflictLocation;
		beforeAll(^{
			conflictLocation = [EHILocation modelWithDictionary:@{
				@"dropoffValidity": @{
					@"validityType" : @"VALID_AFTER_HOURS",
					@"locationHours": @{
						@"DROP": @{
							@"openHours": @[@{
							    @"open" : @"00:00",
								@"close": @"01:00"
							}, @{
							    @"open" : @"07:00",
							    @"close": @"23:59"
							}]
						}
					}
				}
			}];
		});

		it(@"should show after hours title", ^{
			[model updateWithModel:conflictLocation];

			NSString *about = EHILocalizedString(@"locations_map_after_hours_about_button", @"(about?)", @"");
            expect(model.afterHoursTitle.string).to.localizeFromMap(@"locations_map_after_hours_return_label", @{
				@"about" : about
			});
            
            expect(model.hideAfterHours).to.beFalsy();
		});
    });

    context(@"when location has dropff day conflicts", ^{
        __block EHILocation *conflictLocation;
        beforeAll(^{
            conflictLocation = [EHILocation modelWithDictionary:@{
                @"dropoffValidity": @{
                    @"validityType" : @"INVALID_ALL_DAY",
                    @"locationHours": @{
                        @"DROP": @{
                            @"openHours": @[@{
                                @"open" : @"00:00",
                                @"close": @"01:00"
                            }, @{
                                @"open" : @"07:00",
                                @"close": @"23:59"
                            }]
                        }
                    }
                }
            }];
        });

        it(@"should show after hours title", ^{
            [model updateWithModel:conflictLocation];

            expect(model.afterHoursTitle.string).to.beNil();
            expect(model.hideAfterHours).to.beTruthy();
        });
    });
    
});

SpecEnd
