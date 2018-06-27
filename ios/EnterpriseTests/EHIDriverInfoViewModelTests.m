//
//  EHIDriverInfoViewModelTests.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 1/16/18.
//Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIDriverInfoViewModel_Private.h"
#import "EHIDriverInfo.h"

SpecBegin(EHIDriverInfoViewModelTests)

describe(@"EHIDriverInfoViewModel", ^{
    __block EHIDriverInfoViewModel *model;
    __block EHIDriverInfo *driverInfo;
    context(@"given the scenario of updating driver info information sending the new driver info request", ^{
        context(@"when changing no information in existent driver info", ^{
            __block EHIDriverInfo *driverInfoUsedForRequest;
            beforeAll(^{
                model = [EHIDriverInfoViewModel new];
                driverInfo = [EHIDriverInfo modelWithDictionary:
                    @{
                        @"first_name"              : @"tester",
                        @"last_name"               : @"final name",
                        @"wantsEmailNotifications" : @"0",
                        @"shouldSerialize"         : @"0",
                        @"email_address"           : @"qq.edward@mailnation.com",
                        @"mask_email_address"      : @"q•••••d@mailnation.com",
                        @"phone"                   : @{
                                                        @"phone_number"      : @"1234567890",
                                                        @"mask_phone_number" : @"••••••7890",
                                                        @"priority"          : @"0"
                                                    }
                    }
                ];
                //just mocking data in the screen (same as json above)
                model.firstName = @"tester";
                model.lastName  = @"final name";
                model.phone     = @"••••••7890";
                model.email     = @"q•••••d@mailnation.com";
                driverInfoUsedForRequest = [model buildDriverInfoForRequestWithDriverInfo:driverInfo];
            });
            
            it(@"then the driver info request should have the same information in phone number and email fields", ^{
                expect(driverInfoUsedForRequest.phone.number).to.equal(@"1234567890");
                expect(driverInfoUsedForRequest.email).to.equal(@"qq.edward@mailnation.com");
            });
            
            it(@"then masked data should be nil", ^{
                expect(driverInfoUsedForRequest.phone.maskedNumber).to.equal(@"••••••7890");
                expect(driverInfoUsedForRequest.maskedEmail).to.equal(@"q•••••d@mailnation.com");
            });
        });
        
        context(@"when changing only email in existent information", ^{
            __block EHIDriverInfo *driverInfoUsedForRequest;
            beforeAll(^{
                model = [EHIDriverInfoViewModel new];
                driverInfo = [EHIDriverInfo modelWithDictionary:
                              @{
                                @"first_name"              : @"tester",
                                @"last_name"               : @"final name",
                                @"wantsEmailNotifications" : @"0",
                                @"shouldSerialize"         : @"0",
                                @"email_address"           : @"qq.edward@mailnation.com",
                                @"mask_email_address"      : @"q•••••d@mailnation.com",
                                @"phone"                   : @{
                                                                @"phone_number"      : @"1234567890",
                                                                @"mask_phone_number" : @"••••••7890",
                                                                @"priority"          : @"0"
                                                            }
                                }
                              ];
                //just mocking data in the screen (same as json above)
                model.firstName = @"tester";
                model.lastName  = @"final name";
                model.phone     = @"••••••7890";
                model.email     = @"newemail@gmail.com";
                driverInfoUsedForRequest = [model buildDriverInfoForRequestWithDriverInfo:driverInfo];
            });
            it(@"then phone information should remain untouched", ^{
                expect(driverInfoUsedForRequest.phone.number).to.equal(@"1234567890");
            });
            
            it(@"then email should contain the new information", ^{
                expect(driverInfoUsedForRequest.email).to.equal(@"newemail@gmail.com");
            });
            it(@"then masked data should be nil", ^{
                expect(driverInfoUsedForRequest.phone.maskedNumber).to.equal(@"••••••7890");
                expect(driverInfoUsedForRequest.maskedEmail).to.beNil();
            });
        });
        
        context(@"when changing only phone number in existent information", ^{
            __block EHIDriverInfo *driverInfoUsedForRequest;
            beforeAll(^{
                model = [EHIDriverInfoViewModel new];
                driverInfo = [EHIDriverInfo modelWithDictionary:
                              @{
                                @"first_name"              : @"tester",
                                @"last_name"               : @"final name",
                                @"wantsEmailNotifications" : @"0",
                                @"shouldSerialize"         : @"0",
                                @"email_address"           : @"qq.edward@mailnation.com",
                                @"mask_email_address"      : @"q•••••d@mailnation.com",
                                @"phone"                   : @{
                                                                @"phone_number"      : @"1234567890",
                                                                @"mask_phone_number" : @"••••••7890",
                                                                @"priority"          : @"0"
                                                            }
                                }
                              ];
                //just mocking data in the screen (same as json above)
                model.firstName = @"tester";
                model.lastName  = @"final name";
                model.phone     = @"155555555";
                model.email     = @"q•••••d@mailnation.com";
                driverInfoUsedForRequest = [model buildDriverInfoForRequestWithDriverInfo:driverInfo];
            });
            it(@"then phone information should update properly", ^{
                expect(driverInfoUsedForRequest.phone.number).to.equal(@"+1 155555555");
            });
            
            it(@"then email should have the initial data", ^{
                expect(driverInfoUsedForRequest.email).to.equal(@"qq.edward@mailnation.com");
            });
            
            it(@"then masked data should be nil", ^{
                expect(driverInfoUsedForRequest.phone.maskedNumber).to.beNil();
                expect(driverInfoUsedForRequest.maskedEmail).to.equal(@"q•••••d@mailnation.com");
            });
        });

        context(@"when setting opt-in for wantsEmailNotification", ^{
            __block EHIDriverInfo *driverInfoUsedForRequest;
            beforeAll(^{
                model = [EHIDriverInfoViewModel new];

                model = [EHIDriverInfoViewModel new];
                driverInfo = [EHIDriverInfo modelWithDictionary:
                              @{
                                @"first_name"              : @"tester",
                                @"last_name"               : @"final name",
                                @"email_address"           : @"qq.edward@mailnation.com",
                                @"mask_email_address"      : @"q•••••d@mailnation.com",
                                @"phone"                   : @{
                                        @"phone_number"      : @"1234567890",
                                        @"mask_phone_number" : @"••••••7890",
                                        @"priority"          : @"0"
                                        }
                                }
                              ];

                //just mocking data in the screen (same as json above)
                model.firstName = @"tester";
                model.lastName  = @"final name";
                model.phone     = @"155555555";
                model.email     = @"q•••••d@mailnation.com";
                model.wantsEmailNotifications = YES;

                driverInfoUsedForRequest = [model buildDriverInfoForRequestWithDriverInfo:driverInfo];

            });

            it(@"then wantsEmailNotifications flag value should update properly", ^{
                expect(driverInfoUsedForRequest.wantsEmailNotifications).to.equal(EHIOptionalBooleanTrue);
            });
        });

        context(@"when setting opt-out for wantsEmailNotification", ^{
            __block EHIDriverInfo *driverInfoUsedForRequest;
            beforeAll(^{
                model = [EHIDriverInfoViewModel new];

                model = [EHIDriverInfoViewModel new];
                driverInfo = [EHIDriverInfo modelWithDictionary:
                              @{
                                @"first_name"              : @"tester",
                                @"last_name"               : @"final name",
                                @"email_address"           : @"qq.edward@mailnation.com",
                                @"mask_email_address"      : @"q•••••d@mailnation.com",
                                @"phone"                   : @{
                                        @"phone_number"      : @"1234567890",
                                        @"mask_phone_number" : @"••••••7890",
                                        @"priority"          : @"0"
                                        }
                                }
                              ];

                //just mocking data in the screen (same as json above)
                model.firstName = @"tester";
                model.lastName  = @"final name";
                model.phone     = @"155555555";
                model.email     = @"q•••••d@mailnation.com";
                model.wantsEmailNotifications = NO;

                driverInfoUsedForRequest = [model buildDriverInfoForRequestWithDriverInfo:driverInfo];

            });

            it(@"then wantsEmailNotifications flag value should update properly", ^{
                expect(driverInfoUsedForRequest.wantsEmailNotifications).to.equal(EHIOptionalBooleanFalse);
            });
        });

        context(@"when no action is taken with wantsEmailNotification", ^{
            __block EHIDriverInfo *driverInfoUsedForRequest;
            beforeAll(^{
                model = [EHIDriverInfoViewModel new];
                driverInfo = [EHIDriverInfo modelWithDictionary:
                              @{
                                @"first_name"              : @"tester",
                                @"last_name"               : @"final name",
                                @"email_address"           : @"qq.edward@mailnation.com",
                                @"mask_email_address"      : @"q•••••d@mailnation.com",
                                @"phone"                   : @{
                                        @"phone_number"      : @"1234567890",
                                        @"mask_phone_number" : @"••••••7890",
                                        @"priority"          : @"0"
                                        }
                                }
                              ];

                //just mocking data in the screen (same as json above)
                model.firstName = @"tester";
                model.lastName  = @"final name";
                model.phone     = @"155555555";
                model.email     = @"q•••••d@mailnation.com";

                driverInfoUsedForRequest = [model buildDriverInfoForRequestWithDriverInfo:driverInfo];

            });

            it(@"then wantsEmailNotifications flag value should update properly", ^{
                expect(driverInfoUsedForRequest.wantsEmailNotifications).to.equal(EHIOptionalBooleanNull);
            });
        });
    });
    
});

SpecEnd
