//
//  EHILocationConflictViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/7/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationDetailsConflictViewModel.h"
#import "NSDate+Formatting.h"
#import "NSDate+Utility.h"

SpecBegin(EHILocationDetailsConflictViewModelTests)

describe(@"EHILocationDetailsConflictViewModel", ^{

	context(@"on hours conflicts", ^{
    	context(@"on pickup location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"pickupValidity" : @{
					@"validityType": @"INVALID_AT_THAT_TIME",
					@"locationHours": @{
						@"STANDARD": @{
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
	
			it(@"should show pickup title", ^{
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
				NSString *conflict = EHILocalizedString(@"locations_map_closed_pickup", @"PICK-UP", @"");
				expect(model.title).to.localizeFromMap(@"locations_map_closed_your_pickup", @{
					@"closed_on": conflict ?: @""
				});
			});
	
			it(@"should show formatted conflicts", ^{
	
				location.pickupDate = NSDate.new;
	
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
	
				NSString *date = [location.pickupDate ehi_localizedDateString].uppercaseString;
				EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.appendText(date).appendText(@":").space;
	
				NSString *conflict = location.pickupValidity.hours.standardTimes.slices.map(^(EHILocationTimesSlice *slice){
					NSString *open  = [slice.open ehi_localizedTimeString].lowercaseString;
					NSString *close = [slice.close ehi_localizedTimeString].lowercaseString;
	
					return [NSString stringWithFormat:@"%@ - %@", open, close];
				}).join(@", ");
	
				builder.appendText(conflict);
	
				//TUE, JUL 2: 00:00 am - 01:00 am, 07:00 am - 11:59 pm
				expect(model.openHours).to.equal(builder.string.string);
			});
    	});
	
    	context(@"on return location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"dropoffValidity" : @{
					@"validityType": @"INVALID_AT_THAT_TIME",
					@"locationHours": @{
						@"STANDARD": @{
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
	
			it(@"should show return title", ^{
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
				NSString *conflict = EHILocalizedString(@"locations_map_closed_return", @"RETURN", @"");
				expect(model.title).to.localizeFromMap(@"locations_map_closed_your_pickup", @{
					@"closed_on": conflict ?: @""
				});
			});
	
			it(@"should show formatted conflicts", ^{
				location.dropOffDate = NSDate.new;
	
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
	
				NSString *date = [location.dropOffDate ehi_localizedDateString].uppercaseString;
	
				EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.appendText(date).appendText(@":").space;
	
				NSString *conflict = location.dropoffValidity.hours.standardTimes.slices.map(^(EHILocationTimesSlice *slice){
					NSString *open  = [slice.open ehi_localizedTimeString].lowercaseString;
					NSString *close = [slice.close ehi_localizedTimeString].lowercaseString;
	
					return [NSString stringWithFormat:@"%@ - %@", open, close];
				}).join(@", ");
	
				builder.appendText(conflict);
	
				//TUE, JUL 2: 00:00 am - 01:00 am, 07:00 am - 11:59 pm
				expect(model.openHours).to.equal(builder.string.string);
			});
    	});
	
		context(@"on pickup and return location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"pickupValidity" : @{
					@"validityType": @"INVALID_AT_THAT_TIME",
					@"locationHours": @{
						@"STANDARD": @{
							@"openHours": @[@{
								@"open" : @"00:00",
								@"close": @"01:00"
							}, @{
								@"open" : @"07:00",
								@"close": @"23:59"
							}]
						}
					}
				},
				@"dropoffValidity" : @{
					@"validityType": @"INVALID_AT_THAT_TIME",
					@"locationHours": @{
						@"STANDARD": @{
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
	
			it(@"should show pickup and return title", ^{
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
				NSString *returnTitle = EHILocalizedString(@"locations_map_closed_return", @"RETURN", @"");
				NSString *pickupTitle = EHILocalizedString(@"locations_map_closed_pickup", @"PICK-UP", @"");
				NSString *conflict = [NSString stringWithFormat:@"%@ & %@", pickupTitle, returnTitle];
	
				expect(model.title).to.localizeFromMap(@"locations_map_closed_your_pickup", @{
					@"closed_on": conflict ?: @""
				});
			});
	
			it(@"should show formatted conflicts", ^{
    	        location.pickupDate  = NSDate.new;
				location.dropOffDate = [NSDate.new ehi_addDays:2];
    	        
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
	
				NSString *pickupDate = [location.pickupDate ehi_localizedDateString].uppercaseString;
    	        
				EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.appendText(pickupDate).appendText(@":").space;
	
    	        NSArray *pickupSlices     = location.pickupValidity.hours.standardTimes.slices;
				NSString *pickupConflicts = pickupSlices.map(^(EHILocationTimesSlice *slice){
					NSString *open  = [slice.open ehi_localizedTimeString].lowercaseString;
					NSString *close = [slice.close ehi_localizedTimeString].lowercaseString;
	
					return [NSString stringWithFormat:@"%@ - %@", open, close];
				}).join(@", ");
	
    	        builder = builder.appendText(pickupConflicts).newline;
    	        
    	        NSString *returnDate = [location.dropOffDate ehi_localizedDateString].uppercaseString;
	
    	        builder = builder.appendText(returnDate).appendText(@":").space;
    	        
    	        NSArray *dropOffSlices = location.dropoffValidity.hours.standardTimes.slices;
    	        NSString *dropOffConflicts = dropOffSlices.map(^(EHILocationTimesSlice *slice){
    	            NSString *open  = [slice.open ehi_localizedTimeString].lowercaseString;
    	            NSString *close = [slice.close ehi_localizedTimeString].lowercaseString;
    	            
    	            return [NSString stringWithFormat:@"%@ - %@", open, close];
    	        }).join(@", ");
    	        
    	        builder = builder.appendText(dropOffConflicts);
    	        
				//TUE, JUL 2: 00:00 am - 01:00 am, 07:00 am - 11:59 pm
    	        //TUE, JUL 4: 00:00 am - 01:00 am, 07:00 am - 11:59 pm
				expect(model.openHours).to.equal(builder.string.string);
			});
		});
	});

	context(@"on day conflicts", ^{
    	context(@"on pickup location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"pickupValidity" : @{
					@"validityType": @"INVALID_ALL_DAY"
				}
			}];
	
			it(@"should show pickup title", ^{
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
				NSString *conflict = EHILocalizedString(@"locations_map_closed_pickup", @"PICK-UP", @"");
				expect(model.title).to.localizeFromMap(@"locations_map_closed_your_pickup", @{
					@"closed_on": conflict ?: @""
				});
			});
	
			it(@"should show formatted conflicts", ^{
	
				location.pickupDate = NSDate.new;
	
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
	
				NSString *closed = EHILocalizedString(@"location_details_hours_closed", @"CLOSED", @"");
				NSString *date   = [location.pickupDate ehi_localizedDateString].uppercaseString;
				EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.appendText(date).appendText(@":").space.appendText(closed);
	
				//TUE, JUL 2: CLOSED
				expect(model.openHours).to.equal(builder.string.string);
			});
    	});
	
    	context(@"on return location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"dropoffValidity" : @{
					@"validityType": @"INVALID_ALL_DAY",
				}
			}];
	
			it(@"should show return title", ^{
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
				NSString *conflict = EHILocalizedString(@"locations_map_closed_return", @"RETURN", @"");
				expect(model.title).to.localizeFromMap(@"locations_map_closed_your_pickup", @{
					@"closed_on": conflict ?: @""
				});
			});
	
			it(@"should show formatted conflicts", ^{
				location.dropOffDate = NSDate.new;
	
				EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
	
				NSString *date = [location.dropOffDate ehi_localizedDateString].uppercaseString;
				NSString *closed = EHILocalizedString(@"location_details_hours_closed", @"CLOSED", @"");
		
				EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new.appendText(date).appendText(@":").space.appendText(closed);
	
				//TUE, JUL 2: CLOSED
				expect(model.openHours).to.equal(builder.string.string);
			});
		});
	});

	context(@"on after hours return", ^{
		EHILocation *location = [EHILocation modelWithDictionary:@{
			@"pickupValidity" : @{
				@"validityType": @"INVALID_AT_THAT_TIME",
				@"locationHours": @{
					@"STANDARD": @{
						@"openHours": @[@{
							@"open" : @"00:00",
							@"close": @"01:00"
						}, @{
							@"open" : @"07:00",
							@"close": @"23:59"
						}]
					}
				}
			},
			@"dropoffValidity" : @{
				@"validityType": @"VALID_AFTER_HOURS",
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

		it(@"should show title", ^{
			EHILocationDetailsConflictViewModel *model = [[EHILocationDetailsConflictViewModel alloc] initWithModel:location];
			NSString *pickupTitle = EHILocalizedString(@"locations_map_closed_pickup", @"PICK-UP", @"");

			expect(model.title).to.localizeFromMap(@"locations_map_closed_your_pickup", @{
				@"closed_on": pickupTitle ?: @""
			});
		});
	});
});

SpecEnd