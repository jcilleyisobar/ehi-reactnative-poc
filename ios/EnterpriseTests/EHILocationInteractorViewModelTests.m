//
//  EHILocationInteractorViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/27/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationInteractorViewModel.h"

SpecBegin(EHILocationInteractorViewModelTests)

describe(@"EHILocationInteractorViewModel", ^{
    it(@"when selecting pickup location", ^{
        EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
        
        model.isSelectingPickupLocation = YES;
        model.hasDropoffLocation = NO;
        
        expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
    });
    
    it(@"when editing pickup on one way res flow", ^{
        EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
        
        model.isSelectingPickupLocation = YES;
        model.hasDropoffLocation = YES;
        
        expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypePickupOneWay);
    });
    
    it(@"when editing dropoff on one way res flow", ^{
        EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
        
        model.isSelectingPickupLocation = NO;
        model.hasDropoffLocation = YES;
        
        expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeDropoffOneWay);
    });
    
    it(@"when selecting dropoff location", ^{
        EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
        
        model.isSelectingPickupLocation = NO;
        model.hasDropoffLocation = NO;
        
        expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeDropoffOneWay);
    });

	context(@"round trip", ^{
		context(@"when selecting pickup location", ^{
			context(@"on pickup date/time",^{
				it(@"valid location, send date", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					model.location = EHILocation.new;

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = NO;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
					expect(model.shouldSendPickupDate).to.beTruthy();
                    expect(model.canSendPickupTime).to.beTruthy();
				});

				it(@"valid location, send time", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					model.location = EHILocation.new;

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = NO;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
					expect(model.shouldSendPickupTime).to.beTruthy();
                    expect(model.canSendPickupTime).to.beTruthy();
				});

				it(@"invalid all day location, dont send date/time", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					model.location = [EHILocation modelWithDictionary:@{
						@"pickupValidity" : @{
							@"validityType": @"INVALID_ALL_DAY"
						}
					}];

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = NO;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
					expect(model.shouldSendPickupDate).to.beFalsy();
					expect(model.shouldSendPickupTime).to.beFalsy();
				});

				it(@"invalid at that time location, dont send time", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					model.location = [EHILocation modelWithDictionary:@{
						@"pickupValidity" : @{
							@"validityType": @"INVALID_AT_THAT_TIME"
						}
					}];

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = NO;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
					expect(model.shouldSendPickupDate).to.beTruthy();
					expect(model.shouldSendPickupTime).to.beTruthy();
                    expect(model.canSendDropoffTime).to.beFalsy();
                    expect(model.canSendPickupTime).to.beFalsy();
				});

                it(@"valid location, removed pickup date, remote dropoff data", ^{
                    EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
                    model.location = EHILocation.new;

                    model.isSelectingPickupLocation = YES;
                    model.hasDropoffLocation = NO;

                    expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
                    expect(model.shouldSendPickupDate).to.beTruthy();
                    expect(model.shouldWipeDropoffData).to.beTruthy();
                    expect(model.canSendPickupTime).to.beTruthy();
                });
			});

			context(@"on return date/time", ^{
				it(@"valid location, with pickup date, send date/time", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					EHILocationFilterQuery *filterQuery = EHILocationFilterQuery.new;
					filterQuery.datesFilter = EHILocationFilterDateQuery.new;
					filterQuery.datesFilter.pickupDate = NSDate.new;

					model.filterQuery = filterQuery;
					model.location = EHILocation.new;

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = NO;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
					expect(model.shouldSendDropoffDate).to.beTruthy();
					expect(model.shouldSendDropoffTime).to.beTruthy();
				});

				it(@"valid location, without pickup date, send nothing", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					model.location = EHILocation.new;

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = NO;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
					expect(model.shouldSendDropoffDate).to.beFalsy();
					expect(model.shouldSendDropoffTime).to.beFalsy();
				});
			});
		});

		context(@"when selecting dropoff location", ^{
			it(@"valid location, has pickup date, send return date", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
				EHILocationFilterQuery *filterQuery = EHILocationFilterQuery.new;
				filterQuery.datesFilter = EHILocationFilterDateQuery.new;
				filterQuery.datesFilter.pickupDate = NSDate.new;

				model.filterQuery = filterQuery;
				model.location = EHILocation.new;

				model.isSelectingPickupLocation = YES;
				model.hasDropoffLocation = NO;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
				expect(model.shouldSendDropoffDate).to.beTruthy();
                expect(model.canSendDropoffTime).to.beTruthy();
			});

			it(@"valid location, has pickup date, send return time", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
				EHILocationFilterQuery *filterQuery = EHILocationFilterQuery.new;
				filterQuery.datesFilter = EHILocationFilterDateQuery.new;
				filterQuery.datesFilter.pickupDate = NSDate.new;

				model.filterQuery = filterQuery;
				model.location = EHILocation.new;

				model.isSelectingPickupLocation = YES;
				model.hasDropoffLocation = NO;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
				expect(model.shouldSendDropoffTime).to.beTruthy();
                expect(model.canSendDropoffTime).to.beTruthy();
			});

			it(@"valid location, hasn't pickup date, send return date/time", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
				model.location = EHILocation.new;

				model.isSelectingPickupLocation = YES;
				model.hasDropoffLocation = NO;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
				expect(model.shouldSendDropoffTime).to.beFalsy();
				expect(model.shouldSendDropoffTime).to.beFalsy();
                expect(model.canSendDropoffTime).to.beFalsy();
			});

			it(@"invalid all day location, has pickup date, dont send return date/time", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
				EHILocationFilterQuery *filterQuery = EHILocationFilterQuery.new;
				filterQuery.datesFilter = EHILocationFilterDateQuery.new;
				filterQuery.datesFilter.pickupDate = NSDate.new;

				model.filterQuery = filterQuery;
				model.location    =[EHILocation modelWithDictionary:@{
					@"dropoffValidity" : @{
						@"validityType": @"INVALID_ALL_DAY"
					}
				}];

				model.isSelectingPickupLocation = YES;
				model.hasDropoffLocation = NO;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
				expect(model.shouldSendDropoffDate).to.beFalsy();
				expect(model.shouldSendDropoffTime).to.beFalsy();
                expect(model.canSendDropoffTime).to.beFalsy();
			});

			it(@"invalid all day location, hasn't pickup date, dont send return date/time", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
				model.location = [EHILocation modelWithDictionary:@{
					@"dropoffValidity" : @{
						@"validityType": @"INVALID_ALL_DAY"
					}
				}];

				model.isSelectingPickupLocation = YES;
				model.hasDropoffLocation = NO;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
				expect(model.shouldSendDropoffDate).to.beFalsy();
				expect(model.shouldSendDropoffTime).to.beFalsy();
                expect(model.canSendDropoffTime).to.beFalsy();
			});

			it(@"invalid at that time location, has pickup date, dont send return time", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
				EHILocationFilterQuery *filterQuery = EHILocationFilterQuery.new;
				filterQuery.datesFilter = EHILocationFilterDateQuery.new;
				filterQuery.datesFilter.pickupDate = NSDate.new;

				model.filterQuery = filterQuery;
				model.location    = [EHILocation modelWithDictionary:@{
					@"dropoffValidity" : @{
						@"validityType": @"INVALID_AT_THAT_TIME"
					}
				}];

				model.isSelectingPickupLocation = YES;
				model.hasDropoffLocation = NO;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
				expect(model.shouldSendDropoffDate).to.beTruthy();
				expect(model.shouldSendDropoffTime).to.beTruthy();
                expect(model.canSendDropoffTime).to.beFalsy();
			});

			it(@"invalid at that time location, hasn't pickup date, dont send return date/time", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
				model.location    = [EHILocation modelWithDictionary:@{
					@"dropoffValidity" : @{
						@"validityType": @"INVALID_AT_THAT_TIME"
					}
				}];

				model.isSelectingPickupLocation = YES;
				model.hasDropoffLocation = NO;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeRoundTrip);
				expect(model.shouldSendDropoffDate).to.beFalsy();
				expect(model.shouldSendDropoffTime).to.beFalsy();
                expect(model.canSendDropoffTime).to.beFalsy();
			});
		});
	});

	context(@"one way", ^{
		context(@"when editing pickup location", ^{
			context(@"on pickup date/time",^{
				it(@"valid location, send date", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					model.location = EHILocation.new;

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = YES;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypePickupOneWay);
					expect(model.shouldSendPickupDate).to.beTruthy();
				});

				it(@"valid location, send time", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					model.location = EHILocation.new;

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = YES;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypePickupOneWay);
					expect(model.shouldSendPickupTime).to.beTruthy();
				});

				it(@"invalid all day location, dont send date/time", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					model.location = [EHILocation modelWithDictionary:@{
						@"pickupValidity" : @{
							@"validityType": @"INVALID_ALL_DAY"
						}
					}];

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = YES;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypePickupOneWay);
					expect(model.shouldSendPickupDate).to.beFalsy();
					expect(model.shouldSendPickupTime).to.beFalsy();
				});

				it(@"invalid at that time location, dont send time", ^{
					EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
					model.location = [EHILocation modelWithDictionary:@{
						@"pickupValidity" : @{
							@"validityType": @"INVALID_AT_THAT_TIME"
						}
					}];

					model.isSelectingPickupLocation = YES;
					model.hasDropoffLocation = YES;

					expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypePickupOneWay);
					expect(model.shouldSendPickupDate).to.beTruthy();
					expect(model.shouldSendPickupTime).to.beTruthy();
                    expect(model.canSendPickupTime).to.beFalsy();
				});
			});
		});

		context(@"when editing dropoff location", ^{
			it(@"valid location, send dropoff date/time", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
                EHILocationFilterQuery *filterQuery = EHILocationFilterQuery.new;
                filterQuery.datesFilter = EHILocationFilterDateQuery.new;
                filterQuery.datesFilter.pickupDate = NSDate.new;
                
                model.filterQuery = filterQuery;
				model.location = EHILocation.new;

				model.isSelectingPickupLocation = NO;
				model.hasDropoffLocation = YES;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeDropoffOneWay);
				expect(model.shouldSendPickupDate).to.beFalsy();
				expect(model.shouldSendPickupTime).to.beFalsy();
				expect(model.shouldSendDropoffDate).to.beTruthy();
				expect(model.shouldSendDropoffTime).to.beTruthy();
			});

			it(@"invalid all day location, don't send dropoff date/time", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
				EHILocationFilterQuery *filterQuery = EHILocationFilterQuery.new;
				filterQuery.datesFilter = EHILocationFilterDateQuery.new;
				filterQuery.datesFilter.pickupDate = NSDate.new;

				model.filterQuery = filterQuery;
				model.location = [EHILocation modelWithDictionary:@{
					@"dropoffValidity" : @{
						@"validityType": @"INVALID_ALL_DAY"
					}
				}];

				model.isSelectingPickupLocation = NO;
				model.hasDropoffLocation = YES;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeDropoffOneWay);
				expect(model.shouldSendPickupDate).to.beFalsy();
				expect(model.shouldSendPickupTime).to.beFalsy();
				expect(model.shouldSendDropoffDate).to.beFalsy();
				expect(model.shouldSendDropoffTime).to.beFalsy();
			});

			it(@"invalid at that time location, send dropoff date", ^{
				EHILocationInteractorViewModel *model = EHILocationInteractorViewModel.new;
				EHILocationFilterQuery *filterQuery = EHILocationFilterQuery.new;
				filterQuery.datesFilter = EHILocationFilterDateQuery.new;
				filterQuery.datesFilter.pickupDate = NSDate.new;

				model.filterQuery = filterQuery;
				model.location = [EHILocation modelWithDictionary:@{
					@"dropoffValidity" : @{
						@"validityType": @"INVALID_AT_THAT_TIME"
					}
				}];

				model.isSelectingPickupLocation = NO;
				model.hasDropoffLocation = YES;

				expect(model.queryType).to.equal(EHILocationFilterQueryLocationTypeDropoffOneWay);
				expect(model.shouldSendPickupDate).to.beFalsy();
				expect(model.shouldSendPickupTime).to.beFalsy();
				expect(model.shouldSendDropoffDate).to.beTruthy();
				expect(model.shouldSendDropoffTime).to.beTruthy();
                expect(model.canSendDropoffTime).to.beFalsy();
			});
		});
	});
});

SpecEnd
