//
//  EHILocationAssetFactoryTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/22/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationMapPinAssetFactory.h"

SpecBegin(EHILocationAssetFactoryTests)

	describe(@"EHILocationMapPinAssetFactory", ^{
		it(@"should return the correct asset names", ^{
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeEnterprise]).to.equal(@"map_pin_standard");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeEnterprise selected:NO]).to.equal(@"map_pin_standard");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeEnterprise selected:YES]).to.equal(@"map_pin_standard_selected");

			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeAlamo]).to.equal(@"map_pin_alamo");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeAlamo selected:NO]).to.equal(@"map_pin_alamo");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeAlamo selected:YES]).to.equal(@"map_pin_alamo_selected");

			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeNational]).to.equal(@"map_pin_national");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeNational selected:NO]).to.equal(@"map_pin_national");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeNational selected:YES]).to.equal(@"map_pin_national_selected");

			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeTrain]).to.equal(@"map_pin_rail");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeTrain selected:NO]).to.equal(@"map_pin_rail");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeTrain selected:YES]).to.equal(@"map_pin_rail_selected");

			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypePort]).to.equal(@"map_pin_port");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypePort selected:NO]).to.equal(@"map_pin_port");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypePort selected:YES]).to.equal(@"map_pin_port_selected");

			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeAirport]).to.equal(@"map_pin_airports");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeAirport selected:NO]).to.equal(@"map_pin_airports");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeAirport selected:YES]).to.equal(@"map_pin_airports_selected");

			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeMotorcycle]).to.equal(@"map_pin_motorcycles");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeMotorcycle selected:NO]).to.equal(@"map_pin_motorcycles");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeMotorcycle selected:YES]).to.equal(@"map_pin_motorcycles_selected");

			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeExotics]).to.equal(@"map_pin_exotics");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeExotics selected:NO]).to.equal(@"map_pin_exotics");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeExotics selected:YES]).to.equal(@"map_pin_exotics_selected");

			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeFavorite]).to.equal(@"map_pin_fav");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeFavorite selected:NO]).to.equal(@"map_pin_fav");
			expect([EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeFavorite selected:YES]).to.equal(@"map_pin_fav_selected");
		});

		it(@"defaults to enterprise asset", ^{
			EHILocation *location = EHILocation.new;

			NSString *targetAssetName   = [EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeEnterprise];
			NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

			expect(locationAssetName).to.equal(targetAssetName);
		});

		it(@"on exotics location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"attributes" : @[ @"EXOTICS" ]
			}];

			NSString *targetAssetName   = [EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeExotics];
			NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

			expect(locationAssetName).to.equal(targetAssetName);
		});

		it(@"on alamo location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"brand" : @"ALAMO"
			}];

			NSString *targetAssetName   = [EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeAlamo];
			NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

			expect(locationAssetName).to.equal(targetAssetName);
		});

		it(@"on national location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"brand" : @"NATIONAL"
			}];

			NSString *targetAssetName   = [EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeNational];
			NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

			expect(locationAssetName).to.equal(targetAssetName);
		});

		it(@"on location with motorcycle", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"attributes" : @[ @"MOTORCYCLES" ]
			}];

			NSString *targetAssetName   = [EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeMotorcycle];
			NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

			expect(locationAssetName).to.equal(targetAssetName);
		});

		it(@"on airport location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"locationType" : @"AIRPORT"
			}];

			NSString *targetAssetName   = [EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeAirport];
			NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

			expect(locationAssetName).to.equal(targetAssetName);
		});

		it(@"on port location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"locationType" : @"PORT_OF_CALL"
			}];

			NSString *targetAssetName   = [EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypePort];
			NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

			expect(locationAssetName).to.equal(targetAssetName);
		});

		it(@"on train location", ^{
			EHILocation *location = [EHILocation modelWithDictionary:@{
				@"locationType" : @"RAIL"
			}];

			NSString *targetAssetName   = [EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeTrain];
			NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

			expect(locationAssetName).to.equal(targetAssetName);
		});

		context(@"on a location with conflicts", ^{
			it(@"on enterprise location", ^{
				EHILocation *location = [EHILocation modelWithDictionary:@{
					@"pickupValidity" : @{
						@"validityType": @"INVALID_ALL_DAY"
					}
				}];

				NSString *targetAssetName   = [[EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeEnterprise] stringByAppendingString:@"_closed"];
				NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

				expect(locationAssetName).to.equal(targetAssetName);
			});

			it(@"on exotics location", ^{
				EHILocation *location = [EHILocation modelWithDictionary:@{
					@"attributes" : @[ @"EXOTICS" ],
					@"pickupValidity" : @{
						@"validityType": @"INVALID_ALL_DAY"
					}
				}];

				NSString *targetAssetName   = [[EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeExotics] stringByAppendingString:@"_closed"];
				NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

				expect(locationAssetName).to.equal(targetAssetName);
			});

			it(@"on airport location", ^{
				EHILocation *location = [EHILocation modelWithDictionary:@{
					@"locationType" : @"AIRPORT",
					@"pickupValidity" : @{
						@"validityType": @"INVALID_ALL_DAY"
					}
				}];

				NSString *targetAssetName   = [[EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeAirport] stringByAppendingString:@"_closed"];
				NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

				expect(locationAssetName).to.equal(targetAssetName);
			});

			it(@"on port location", ^{
				EHILocation *location = [EHILocation modelWithDictionary:@{
					@"locationType" : @"PORT_OF_CALL",
					@"pickupValidity" : @{
						@"validityType": @"INVALID_ALL_DAY"
					}
				}];

				NSString *targetAssetName   = [[EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypePort] stringByAppendingString:@"_closed"];
				NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

				expect(locationAssetName).to.equal(targetAssetName);
			});

			it(@"on train location", ^{
				EHILocation *location = [EHILocation modelWithDictionary:@{
					@"locationType" : @"RAIL",
					@"pickupValidity" : @{
						@"validityType": @"INVALID_ALL_DAY"
					}
				}];

				NSString *targetAssetName   = [[EHILocationMapPinAssetFactory assetWithType:EHILocationMapPinAssetTypeTrain] stringByAppendingString:@"_closed"];
				NSString *locationAssetName = [EHILocationMapPinAssetFactory assetForLocation:location];

				expect(locationAssetName).to.equal(targetAssetName);
			});
		});
	});

SpecEnd
