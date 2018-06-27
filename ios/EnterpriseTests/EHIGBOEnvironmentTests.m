//
//  EHIGBOEnvironmentTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 09/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIGBOEnvironment.h"

SpecBegin(EHIGBOEnvironmentTests)

    describe(@"EHIGBOEnvironment", ^{

        context(@"ensure all covered", ^{
            context(@"environments", ^{
                EHIEnvironmentType env = (EHIEnvironmentType)arc4random_uniform(EHIEnvironmentTypeNumEnvironments);
                switch(env) {
                    case EHIEnvironmentTypeSvcsQaXqa1: break;
                    case EHIEnvironmentTypeRcQaInt1: break;
                    case EHIEnvironmentTypeHotHot: break;
                    case EHIEnvironmentTypePrdSuPqa: break;
                    case EHIEnvironmentTypePrdsup: break;
                    case EHIEnvironmentTypeDev: break;
                    case EHIEnvironmentTypeDevQa: break;
                    case EHIEnvironmentTypeRcDev: break;
                    case EHIEnvironmentTypeTmpEnv: break;
                    case EHIEnvironmentTypePrdSuPdev: break;
                    case EHIEnvironmentTypePenTest: break;
                    case EHIEnvironmentTypeBeta: break;
                    case EHIEnvironmentTypeProd: break;
                    case EHIEnvironmentTypeNumEnvironments: break;
                }
            });

            context(@"services", ^{
                EHIServicesEnvironmentType env = (EHIServicesEnvironmentType)arc4random_uniform(EHIServicesEnvironmentTypeGBOProfile);
                switch(env) {
                    case EHIServicesEnvironmentTypeNone: break;
                    case EHIServicesEnvironmentTypeGBOLocation: break;
                    case EHIServicesEnvironmentTypeGBORental: break;
                    case EHIServicesEnvironmentTypeGBOProfile: break;
                    case EHIServicesEnvironmentTypeAEM: break;
                }
            });
        });
        
        context(@"domain", ^{
            context(@"GBO location", ^{
                it(@"SvcsQa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeSvcsQaXqa1].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-svcsqa.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"RcQa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeRcQaInt1].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-rcqa.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"HotHot", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeHotHot].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-hh-qa.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"PrdSuPqa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdSuPqa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-prdsupqa.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"PrdSuP", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdsup].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-prdsup.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"Dev", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-dev.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"Dev Qa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeDevQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-devqa.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"RcDev", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeRcDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-rcdev.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"PrdSuPdev", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdSuPdev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-prdsupdev.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"TmpEnv", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeTmpEnv].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-tmpenv.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"PenTest", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePenTest].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-prdsupqa.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"Beta", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeBeta].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location.enterprise.ehiaws.com/gbo-location/api/v2");
                });
                it(@"Prod", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeProd].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location.enterprise.ehiaws.com/gbo-location/api/v2");
                });
            });
            context(@"GBO rental", ^{
                it(@"SvcsQa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeSvcsQaXqa1].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-svcsqa.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"RcQa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcQaInt1].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-rcqa.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"HotHot", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeHotHot].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-hh-qa.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"PrdSuPqa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdSuPqa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-prdsupqa.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"PrdSuP", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdsup].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-prdsup.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"Dev", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-dev.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"Dev Qa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeDevQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-devqa.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"RcDev", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-rcdev.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"PrdSuPdev", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdSuPdev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-prdsupdev.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"TmpEnv", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeTmpEnv].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-tmpenv.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"PenTest", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePenTest].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-prdsupqa.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"Beta", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeBeta].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental.enterprise.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"Prod", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeProd].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental.enterprise.ehiaws.com/gbo-rental/api/v2");
                });
            });
            context(@"GBO profile", ^{
                it(@"SvcsQa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeSvcsQaXqa1].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-svcsqa.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"RcQa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeRcQaInt1].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-rcqa.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"HotHot", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeHotHot].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-hh-qa.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"PrdSuPqa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdSuPqa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-prdsupqa.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"PrdSuP", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdsup].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-prdsup.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"Dev", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-dev.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"Dev Qa", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeDevQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-devqa.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"RcDev", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeRcDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-rcdev.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"PrdSuPdev", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdSuPdev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-prdsupdev.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"TmpEnv", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeTmpEnv].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-tmpenv.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"PenTest", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePenTest].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-prdsupqa.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"Beta", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeBeta].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile.enterprise.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"Prod", ^{
                    NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeProd].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile.enterprise.ehiaws.com/gbo-profile/api/v2");
                });
            });
            context(@"Invalid cases", ^{
                NSString *domainURL = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeNone forEnvironment:EHIEnvironmentTypeNumEnvironments].domainURL;
                expect(domainURL).to.beNil();
            });
        });

        context(@"api keys", ^{
            context(@"GBO location", ^{
                it(@"SvcsQa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeSvcsQaXqa1].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeRcQaInt1].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"HotHot", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeHotHot].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPqa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdSuPqa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuP", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdsup].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev Qa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeDevQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcDev", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeRcDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPdev", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdSuPdev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"TmpEnv", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeTmpEnv].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PenTest", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePenTest].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Beta", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeBeta].apiKey;
                    expect(apiKey).to.equal(@"DmyKjglEVzHis5y0i2E/hFeHL+HasvVYgkATwHiDHyo=");
                });
                it(@"Prod", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeProd].apiKey;
                    expect(apiKey).to.equal(@"DmyKjglEVzHis5y0i2E/hFeHL+HasvVYgkATwHiDHyo=");
                });
            });
            context(@"GBO rental", ^{
                it(@"SvcsQa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeSvcsQaXqa1].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcQaInt1].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"HotHot", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeHotHot].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQaNew", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcQaInt1].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPqa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdSuPqa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuP", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdsup].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev Qa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeDevQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcDev", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPdev", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdSuPdev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"TmpEnv", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeTmpEnv].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PenTest", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePenTest].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Beta", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeBeta].apiKey;
                    expect(apiKey).to.equal(@"DmyKjglEVzHis5y0i2E/hFeHL+HasvVYgkATwHiDHyo=");
                });
                it(@"Prod", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeProd].apiKey;
                    expect(apiKey).to.equal(@"DmyKjglEVzHis5y0i2E/hFeHL+HasvVYgkATwHiDHyo=");
                });
            });
            context(@"GBO profile", ^{
                it(@"SvcsQa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeSvcsQaXqa1].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeRcQaInt1].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeHotHot].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPqa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdSuPqa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuP", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdsup].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev Qa", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeDevQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcDev", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeRcDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPdev", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdSuPdev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"TmpEnv", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeTmpEnv].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PenTest", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePenTest].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Beta", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeBeta].apiKey;
                    expect(apiKey).to.equal(@"DmyKjglEVzHis5y0i2E/hFeHL+HasvVYgkATwHiDHyo=");
                });
                it(@"Prod", ^{
                    NSString *apiKey = [EHIGBOEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeProd].apiKey;
                    expect(apiKey).to.equal(@"DmyKjglEVzHis5y0i2E/hFeHL+HasvVYgkATwHiDHyo=");
                });
            });
        });
    });

SpecEnd
