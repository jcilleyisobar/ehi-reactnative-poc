//
//  EHILocationsMapListTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/25/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationsMapListViewModel.h"
#import "NSString+Formatting.h"

SpecBegin(EHILocationsMapListTests)

    describe(@"EHILocationsMapCalloutViewModel", ^{
        __block EHILocationsMapListViewModel *model = EHILocationsMapListViewModel.new;

        it(@"should show title and address as the subtitle", ^{
            model = EHILocationsMapListViewModel.new;

            EHILocation *location = [EHILocation modelWithDictionary:@{
                @"name": @"Austin",
                @"address": @{
                    @"street_addresses": @[
                        @"3819 Presidential Blvd"
                    ],
                },
            }];

            [model updateWithModel:location];

            expect(model.title.string).to.equal(location.localizedName);
            expect(model.subtitle).to.equal(location.address.formattedAddress);
            expect(model.style).to.equal(EHILocationsMapListStyleValid);
			expect(model.flexibleTravelTitle).to.beNil();
			expect(model.conflictTitle.string).to.beNil();
			expect(model.openHoursTitle).to.beNil();
			expect(model.shouldShowDetails).to.beFalsy();
        });

        it(@"should show title (with airport code) and address as the subtitle", ^{
            model = EHILocationsMapListViewModel.new;

            EHILocation *location = [EHILocation modelWithDictionary:@{
                @"location_type": @"airport",
                @"name": @"Austin Bergstrom Airport",
                @"airport_code": @"AUS",
                @"address": @{
                    @"street_addresses": @[
                        @"3819 Presidential Blvd"
                    ],
                },
            }];

            [model updateWithModel:location];

            NSString *locationName = [NSString stringWithFormat:@"%@ %@", location.localizedName, location.airportCode];
            expect(model.title.string).to.equal(locationName);
            expect(model.subtitle).to.equal(location.address.formattedAddress);
            expect(model.style).to.equal(EHILocationsMapListStyleValid);
			expect(model.flexibleTravelTitle).to.beNil();
            expect(model.conflictTitle.string).to.beNil();
            expect(model.openHoursTitle).to.beNil();
			expect(model.shouldShowDetails).to.beFalsy();
        });

        context(@"on a location with conflicts", ^{
            before(^{
                model = EHILocationsMapListViewModel.new;

                EHILocation *conflictLocation = [EHILocation modelWithDictionary:@{
                    @"name": @"Austin",
                    @"address": @{
                        @"street_addresses": @[
                            @"3819 Presidential Blvd"
                        ],
                    },
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

                [model updateWithModel:conflictLocation]; 
            });

            it(@"on collapased state", ^{
                expect(model.style).to.equal(EHILocationsMapListStyleInvalid);
                expect(model.flexibleTravelTitle).to.localizeFrom(@"locations_map_flexible_travel_button");
                expect(model.openHoursTitle).to.localizeFrom(@"locations_map_location_hours_operation");
                expect(model.isExpanded).to.beFalsy();

				NSString *pickupTitle = EHILocalizedString(@"locations_map_closed_pickup", @"PICK-UP", @"");
				NSString *title = EHILocalizedString(@"locations_map_closed_your_pickup", @"CLOSED ON YOUR #{closed_on}", @"");
				title = [title ehi_applyReplacementMap:@{
					@"closed_on": pickupTitle ?: @""
				}];
                
                title = [NSString stringWithFormat:@"%@ - %@", title, EHILocalizedString(@"locations_map_view_hours", @"VIEW HOURS", @"")];
                
                expect(model.conflictTitle.string).to.equal(title);
                
            });

            it(@"on expanded state", ^{
                [model changeState];
                
                NSString *pickupTitle = EHILocalizedString(@"locations_map_closed_pickup", @"PICK-UP", @"");
                NSString *title = EHILocalizedString(@"locations_map_closed_your_pickup", @"CLOSED ON YOUR #{closed_on}", @"");
                title = [title ehi_applyReplacementMap:@{
                    @"closed_on": pickupTitle ?: @""
                }];                
                title = [NSString stringWithFormat:@"%@ - %@", title, EHILocalizedString(@"locations_map_hide_hours", @"HIDE HOURS", @"")];
                
                expect(model.conflictTitle.string).to.equal(title);
            });
        });

		context(@"on after hours return", ^{
			before(^{
				model = EHILocationsMapListViewModel.new;
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

				[model updateWithModel:location];
			});

			it(@"should show after hours action title", ^{
				NSString *about = EHILocalizedString(@"locations_map_after_hours_about_button", @"(about?)", @"");
				expect(model.afterHoursTitle.string).to.localizeFromMap(@"locations_map_after_hours_return_label", @{
					@"about": about ?: @""
				});
				expect(model.shouldShowDetails).to.beTruthy();
			});
		});
    });

SpecEnd
