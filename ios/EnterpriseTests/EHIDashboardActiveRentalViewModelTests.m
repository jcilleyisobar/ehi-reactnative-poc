//
//  EHIDashboardActiveRentalViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 19/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIDashboardActiveRentalViewModel.h"
#import "NSDate+Utility.h"
#import "NSDate+Formatting.h"

SpecBegin(EHIDashboardActiveRentalViewModelTests)

describe(@"EHIDashboardActiveRentalViewModel", ^{
    context(@"no after hours", ^{
        it(@"should hide return instructions", ^{
            EHIDashboardActiveRentalViewModel *model = [[EHIDashboardActiveRentalViewModel alloc] initWithModel:EHIUserRental.new];
            expect(model.shouldHideReturnLocation).to.beTruthy();
        });
    });

    context(@"on return with after hours", ^{
        it(@"should show return instructions", ^{
            EHIUserRental *rental = [EHIUserRental modelWithDictionary:@{
                @"return_time"     : @"2018-02-19T09:30",
                @"return_location" : @{
                    @"id"                 : @"1",
                    @"policies"           : @[],
                    @"after_hours_return" : @(YES),
                },
                @"pickup_location" : @{
                    @"id"       : @"2",
                    @"policies" : @[],
                }
            }];

            EHILocationHours *hours = [EHILocationHours modelWithDictionary:@{
                @"data": @{
                    @"2018-02-19": @{
                        @"DROP": @{
                            @"hours": @[
                                @{
                                    @"open": @"05:00",
                                    @"close": @"23:59"
                                }
                            ],
                            @"open24Hours": @"false",
                            @"closed": @"false"
                        }
                    }
                }
            }];

            rental.returnLocation.hours = hours;
            EHIDashboardActiveRentalViewModel *model = [[EHIDashboardActiveRentalViewModel alloc] initWithModel:rental];
            expect(model.shouldHideReturnLocation).to.beFalsy();
        });
    });

    context(@"on return without after hours", ^{
       it(@"when date is outside interval, should hide return instructions", ^{
            EHIUserRental *rental = [EHIUserRental modelWithDictionary:@{
                @"return_time"     : @"2018-02-20T09:30",
                @"return_location" : @{
                    @"id"                 : @"1",
                    @"policies"           : @[],
                    @"after_hours_return" : @(YES),
                },
                @"pickup_location" : @{
                    @"id"       : @"2",
                    @"policies" : @[],
                }
            }];

            EHILocationHours *hours = [EHILocationHours modelWithDictionary:@{
                @"data": @{
                    @"2018-02-19": @{
                        @"DROP": @{
                            @"hours": @[
                                @{
                                    @"open": @"05:00",
                                    @"close": @"23:59"
                                }
                            ],
                            @"open24Hours": @"false",
                            @"closed": @"false"
                        }
                    }
                }
            }];

            rental.returnLocation.hours = hours;
            EHIDashboardActiveRentalViewModel *model = [[EHIDashboardActiveRentalViewModel alloc] initWithModel:rental];
            expect(model.shouldHideReturnLocation).to.beTruthy();
        });

        it(@"when location is closed, should hide return instructions", ^{
            EHIUserRental *rental = [EHIUserRental modelWithDictionary:@{
                @"return_time"     : @"2018-02-20T09:30",
                @"return_location" : @{
                    @"id"                 : @"1",
                    @"policies"           : @[],
                    @"after_hours_return" : @(YES),
                },
                @"pickup_location" : @{
                    @"id"       : @"2",
                    @"policies" : @[],
                }
            }];

            EHILocationHours *hours = [EHILocationHours modelWithDictionary:@{
                @"data": @{
                    @"2018-02-19": @{
                        @"DROP": @{
                            @"open24Hours": @"false",
                            @"closed": @"true"
                        }
                    }
                }
            }];

            rental.returnLocation.hours = hours;
            EHIDashboardActiveRentalViewModel *model = [[EHIDashboardActiveRentalViewModel alloc] initWithModel:rental];
            expect(model.shouldHideReturnLocation).to.beTruthy();
        });

        it(@"when return time doesn't fall into the after hours time frame, should hide return instructions", ^{
            EHIUserRental *rental = [EHIUserRental modelWithDictionary:@{
                @"return_time"     : @"2018-02-20T09:30",
                @"return_location" : @{
                    @"id"                 : @"1",
                    @"policies"           : @[],
                    @"after_hours_return" : @(YES),
                },
                @"pickup_location" : @{
                    @"id"       : @"2",
                    @"policies" : @[],
                }
            }];

            EHILocationHours *hours = [EHILocationHours modelWithDictionary:@{
                @"data": @{
                    @"2018-02-19": @{
                        @"DROP": @{
                            @"hours": @[
                                @{
                                    @"open": @"15:00",
                                    @"close": @"23:59"
                                }
                            ],
                            @"open24Hours": @"false",
                            @"closed": @"false"
                        }
                    }
                }
            }];

            rental.returnLocation.hours = hours;
            EHIDashboardActiveRentalViewModel *model = [[EHIDashboardActiveRentalViewModel alloc] initWithModel:rental];
            expect(model.shouldHideReturnLocation).to.beTruthy();
        });
    });

    context(@"on pickup with after hours", ^{
        it(@"should show return instructions", ^{
            EHIUserRental *rental = [EHIUserRental modelWithDictionary:@{
                @"return_time"     : @"2018-02-19T09:30",
                @"pickup_location" : @{
                    @"id"                 : @"1",
                    @"policies"           : @[],
                    @"after_hours_return" : @(YES),
                }
            }];

            EHILocationHours *hours = [EHILocationHours modelWithDictionary:@{
                @"data": @{
                    @"2018-02-19": @{
                        @"DROP": @{
                            @"hours": @[
                                @{
                                    @"open": @"05:00",
                                    @"close": @"23:59"
                                }
                            ],
                            @"open24Hours": @"false",
                            @"closed": @"false"
                        }
                    }
                }
            }];

            rental.pickupLocation.hours = hours;
            EHIDashboardActiveRentalViewModel *model = [[EHIDashboardActiveRentalViewModel alloc] initWithModel:rental];
            expect(model.shouldHideReturnLocation).to.beFalsy();
        });
    });

    context(@"on pickup without after hours", ^{
       it(@"when date is outside interval, should hide return instructions", ^{
            EHIUserRental *rental = [EHIUserRental modelWithDictionary:@{
                @"return_time"     : @"2018-02-20T09:30",
                @"pickup_location" : @{
                    @"id"                 : @"1",
                    @"policies"           : @[],
                    @"after_hours_return" : @(YES),
                }
            }];

            EHILocationHours *hours = [EHILocationHours modelWithDictionary:@{
                @"data": @{
                    @"2018-02-19": @{
                        @"DROP": @{
                            @"hours": @[
                                @{
                                    @"open": @"05:00",
                                    @"close": @"23:59"
                                }
                            ],
                            @"open24Hours": @"false",
                            @"closed": @"false"
                        }
                    }
                }
            }];

            rental.pickupLocation.hours = hours;
            EHIDashboardActiveRentalViewModel *model = [[EHIDashboardActiveRentalViewModel alloc] initWithModel:rental];
            expect(model.shouldHideReturnLocation).to.beTruthy();
       });

        it(@"when location is closed, should hide return instructions", ^{
            EHIUserRental *rental = [EHIUserRental modelWithDictionary:@{
                @"return_time"     : @"2018-02-20T09:30",
                @"pickup_location" : @{
                    @"id"                 : @"1",
                    @"policies"           : @[],
                    @"after_hours_return" : @(YES),
                }
            }];

            EHILocationHours *hours = [EHILocationHours modelWithDictionary:@{
                @"data": @{
                    @"2018-02-19": @{
                        @"DROP": @{
                            @"open24Hours": @"false",
                            @"closed": @"true"
                        }
                    }
                }
            }];

            rental.pickupLocation.hours = hours;
            EHIDashboardActiveRentalViewModel *model = [[EHIDashboardActiveRentalViewModel alloc] initWithModel:rental];
            expect(model.shouldHideReturnLocation).to.beTruthy();
       });

        it(@"when return time doesn't fall into the after hours time frame, should hide return instructions", ^{
            EHIUserRental *rental = [EHIUserRental modelWithDictionary:@{
                @"return_time"     : @"2018-02-20T09:30",
                @"pickup_location" : @{
                    @"id"                 : @"1",
                    @"policies"           : @[],
                    @"after_hours_return" : @(YES),
                }
            }];

            EHILocationHours *hours = [EHILocationHours modelWithDictionary:@{
                @"data": @{
                    @"2018-02-19": @{
                        @"DROP": @{
                            @"hours": @[
                                @{
                                    @"open": @"15:00",
                                    @"close": @"23:59"
                                }
                            ],
                            @"open24Hours": @"false",
                            @"closed": @"false"
                        }
                    }
                }
            }];

            rental.pickupLocation.hours = hours;
            EHIDashboardActiveRentalViewModel *model = [[EHIDashboardActiveRentalViewModel alloc] initWithModel:rental];
            expect(model.shouldHideReturnLocation).to.beTruthy();
        });
    });
});

SpecEnd
