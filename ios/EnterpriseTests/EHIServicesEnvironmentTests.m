//
//  EHIServiceEnvironmentTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 09/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIServicesEnvironment.h"

SpecBegin(EHIServicesEnvironmentTests)

    describe(@"EHIServicesEnvironment", ^{

        context(@"ensure all covered", ^{
            context(@"MSI", ^{
                EHIEnvironmentType env = (EHIEnvironmentType)arc4random_uniform(EHIEnvironmentTypeNumEnvironments);
                switch(env) {
                    case EHIEnvironmentTypeSvcsQa: break;
                    case EHIEnvironmentTypeRcQa: break;
                    case EHIEnvironmentTypeHotHot: break;
                    case EHIEnvironmentTypePrdSuPqa: break;
                    case EHIEnvironmentTypePrdsup: break;
                    case EHIEnvironmentTypeDev: break;
                    case EHIEnvironmentTypeDevQa: break;
                    case EHIEnvironmentTypeRcDev: break;
                    case EHIEnvironmentTypeTmpEnv: break;
                    case EHIEnvironmentTypePrdSuPdev: break;
                    case EHIEnvironmentTypePenTest: break;
                    case EHIEnvironmentTypeEast: break;
                    case EHIEnvironmentTypeWest: break;
                    case EHIEnvironmentTypeBeta: break;
                    case EHIEnvironmentTypeProd: break;
                    case EHIEnvironmentTypeNumEnvironments: break;
                }
            });

            context(@"GBO", ^{
                EHIServicesEnvironmentType env = (EHIServicesEnvironmentType)arc4random_uniform(EHIServicesEnvironmentTypeGBOProfile);
                switch(env) {
                    case EHIServicesEnvironmentTypeNone: break;
                    case EHIServicesEnvironmentTypeGBOLocation: break;
                    case EHIServicesEnvironmentTypeGBORental: break;
                    case EHIServicesEnvironmentTypeGBOProfile: break;
                }
            });
        });

        context(@"domain", ^{
            context(@"GBO location", ^{
                it(@"SvcsQa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeSvcsQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-svcsqa.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"RcQa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeRcQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-rcqa.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"HotHot", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeHotHot].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-hh-qa.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"PrdSuPqa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdSuPqa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-prdsupqa.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"PrdSuP", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdsup].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-prdsup.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"Dev", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-dev.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"Dev Qa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeDevQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-devqa.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"RcDev", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeRcDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-rcdev.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"PrdSuPdev", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdSuPdev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-prdsupdev.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"TmpEnv", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeTmpEnv].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-tmpenv.gbo.csdev.ehiaws-nonprod.com/gbo-location/api/v2");
                });
                it(@"PenTest", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePenTest].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-prdsupqa.csdev.ehiaws.com/gbo-location/api/v2");
                });
                it(@"East", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeEast].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-east.enterprise.ehiaws.com/gbo-location/api/v2");
                });
                it(@"West", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeWest].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location-west.enterprise.ehiaws.com/gbo-location/api/v2");
                });
                it(@"Beta", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeBeta].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location.enterprise.ehiaws.com/gbo-location/api/v2");
                });
                it(@"Prod", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeProd].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-location.enterprise.ehiaws.com/gbo-location/api/v2");
                });
            });
            context(@"GBO rental", ^{
                it(@"SvcsQa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeSvcsQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-svcsqa.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"RcQa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-rcqa.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"HotHot", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeHotHot].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-hh-qa.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"PrdSuPqa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdSuPqa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-prdsupqa.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"PrdSuP", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdsup].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-prdsup.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"Dev", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-dev.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"Dev Qa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeDevQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-devqa.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"RcDev", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-rcdev.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"PrdSuPdev", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdSuPdev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-prdsupdev.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"TmpEnv", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeTmpEnv].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-tmpenv.gbo.csdev.ehiaws-nonprod.com/gbo-rental/api/v2");
                });
                it(@"PenTest", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePenTest].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-prdsupqa.csdev.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"East", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeEast].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-east.enterprise.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"West", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeWest].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental-west.enterprise.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"Beta", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeBeta].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental.enterprise.ehiaws.com/gbo-rental/api/v2");
                });
                it(@"Prod", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeProd].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-rental.enterprise.ehiaws.com/gbo-rental/api/v2");
                });
            });
            context(@"GBO profile", ^{
                it(@"SvcsQa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeSvcsQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-svcsqa.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"RcQa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeRcQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-rcqa.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"HotHot", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeHotHot].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-hh-qa.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"PrdSuPqa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdSuPqa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-prdsupqa.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"PrdSuP", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdsup].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-prdsup.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"Dev", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-dev.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"Dev Qa", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeDevQa].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-devqa.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"RcDev", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeRcDev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-rcdev.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"PrdSuPdev", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdSuPdev].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-prdsupdev.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"TmpEnv", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeTmpEnv].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-tmpenv.gbo.csdev.ehiaws-nonprod.com/gbo-profile/api/v2");
                });
                it(@"PenTest", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePenTest].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-prdsupqa.csdev.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"East", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeEast].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-east.enterprise.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"West", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeWest].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile-west.enterprise.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"Beta", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeBeta].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile.enterprise.ehiaws.com/gbo-profile/api/v2");
                });
                it(@"Prod", ^{
                    NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeProd].domainURL;
                    expect(domainURL).to.equal(@"https://www-gbo-profile.enterprise.ehiaws.com/gbo-profile/api/v2");
                });
            });
            context(@"Invalid cases", ^{
                NSString *domainURL = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeNone forEnvironment:EHIEnvironmentTypeNumEnvironments].domainURL;
                expect(domainURL).to.beNil();
            });
        });

        context(@"api keys", ^{
            context(@"GBO location", ^{
                it(@"SvcsQa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeSvcsQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeRcQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"HotHot", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeHotHot].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPqa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdSuPqa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuP", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdsup].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev Qa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeDevQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcDev", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeRcDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPdev", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePrdSuPdev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"TmpEnv", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeTmpEnv].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PenTest", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypePenTest].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"East", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeEast].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
                it(@"West", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeWest].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
                it(@"Beta", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeBeta].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
                it(@"Prod", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOLocation forEnvironment:EHIEnvironmentTypeProd].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
            });
            context(@"GBO rental", ^{
                it(@"SvcsQa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeSvcsQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"HotHot", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeHotHot].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQaNew", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPqa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdSuPqa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuP", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdsup].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev Qa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeDevQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcDev", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeRcDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPdev", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePrdSuPdev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"TmpEnv", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeTmpEnv].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PenTest", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypePenTest].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"East", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeEast].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
                it(@"West", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeWest].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
                it(@"Beta", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeBeta].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
                it(@"Prod", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBORental forEnvironment:EHIEnvironmentTypeProd].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
            });
            context(@"GBO profile", ^{
                it(@"SvcsQa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeSvcsQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeRcQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcQa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeHotHot].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPqa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdSuPqa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuP", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdsup].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"Dev Qa", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeDevQa].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"RcDev", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeRcDev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PrdSuPdev", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePrdSuPdev].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"TmpEnv", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeTmpEnv].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"PenTest", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypePenTest].apiKey;
                    expect(apiKey).to.equal(@"HCvQZexkp1fi87UvguURhmUfg3B4PrIhVXZza+X2Vfc=");
                });
                it(@"East", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeEast].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
                it(@"West", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeWest].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
                it(@"Beta", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeBeta].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
                it(@"Prod", ^{
                    NSString *apiKey = [EHIServicesEnvironment serviceWithType:EHIServicesEnvironmentTypeGBOProfile forEnvironment:EHIEnvironmentTypeProd].apiKey;
                    expect(apiKey).to.equal(@"adb6ks4jqnVGD/Gdn7JB2Cl1bSuEyh7lpF022l/sPLo=");
                });
            });
        });
    });

SpecEnd
