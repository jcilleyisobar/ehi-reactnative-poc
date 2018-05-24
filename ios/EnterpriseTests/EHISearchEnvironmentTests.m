//
//  EHISearchEnvironmentTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 07/08/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHISearchEnvironment.h"

SpecBegin(EHISearchEnvironmentTests)

describe(@"EHISearchEnvironment", ^{

    it(@"count", ^{
        expect(EHISearchEnvironmentTypeNumEnvironments).to.equal(6);
    });

    context(@"default environment selection", ^{
        before(^{
            [NSUserDefaults.standardUserDefaults setValue:nil forKey:EHISearchEnvironmentTypeKey];
        });
    #if !defined(DEBUG) && !defined(UAT)
        it(@"unarchive prod", ^{
            EHISearchEnvironment *unarchive = [EHISearchEnvironment unarchive];
            expect(unarchive.environmentType).to.equal(EHISearchEnvironmentTypeProd);
        });
    #else
        it(@"any configuration", ^{
            EHISearchEnvironment *unarchive = [EHISearchEnvironment unarchive];
            expect(unarchive.environmentType).to.equal(EHISearchEnvironmentTypeXqa1);
        });
    #endif
    });

    context(@"names", ^{
        it(@"xqa1", ^{
            NSString *name = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeXqa1].displayName;
            expect(name).to.equal(@"SOLR XQA1");
        });
        it(@"xqa2", ^{
            NSString *name = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeXqa2].displayName;
            expect(name).to.equal(@"SOLR XQA2");
        });
        it(@"xqa3", ^{
            NSString *name = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeXqa3].displayName;
            expect(name).to.equal(@"SOLR XQA3");
        });
        it(@"int1", ^{
            NSString *name = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeInt1].displayName;
            expect(name).to.equal(@"SOLR INT1");
        });
        it(@"int2", ^{
            NSString *name = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeInt2].displayName;
            expect(name).to.equal(@"SOLR INT2");
        });
        it(@"prod", ^{
            NSString *name = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeProd].displayName;
            expect(name).to.equal(@"PROD");
        });
    });

    context(@"domain names", ^{
        it(@"xqa1", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeXqa1].domainURL;
            expect(domainName).to.equal(@"https://xqa1.location.enterprise.com");
        });
        it(@"xqa2", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeXqa2].domainURL;
            expect(domainName).to.equal(@"https://xqa2.location.enterprise.com");
        });
        it(@"xqa3", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeXqa3].domainURL;
            expect(domainName).to.equal(@"https://xqa3.location.enterprise.com");
        });
        it(@"int1", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeInt1].domainURL;
            expect(domainName).to.equal(@"https://int1.location.enterprise.com");
        });
        it(@"int2", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeInt2].domainURL;
            expect(domainName).to.equal(@"https://int2.location.enterprise.com");
        });
        it(@"prod", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeProd].domainURL;
            expect(domainName).to.equal(@"https://prd.location.enterprise.com");
        });
    });

    context(@"base path", ^{
        it(@"", ^{
            EHISearchEnvironment *environment = [EHISearchEnvironment new];
            expect(environment.basePath).to.equal(@"/enterprise-sls/search/location/mobile");
        });
    });

    context(@"api key path", ^{
        it(@"", ^{
            EHISearchEnvironment *environment = [EHISearchEnvironment new];
            expect(environment.apiKey).to.equal(@"bd6dfa74-8881-4db5-8268-1de81dd504e8");
        });
    });

    context(@"service url", ^{
        it(@"xqa1", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeXqa1].serviceURL;
            expect(domainName).to.equal(@"https://xqa1.location.enterprise.com/enterprise-sls/search/location/mobile");
        });
        it(@"xqa2", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeXqa2].serviceURL;
            expect(domainName).to.equal(@"https://xqa2.location.enterprise.com/enterprise-sls/search/location/mobile");
        });
        it(@"xqa3", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeXqa3].serviceURL;
            expect(domainName).to.equal(@"https://xqa3.location.enterprise.com/enterprise-sls/search/location/mobile");
        });
        it(@"int1", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeInt1].serviceURL;
            expect(domainName).to.equal(@"https://int1.location.enterprise.com/enterprise-sls/search/location/mobile");
        });
        it(@"int2", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeInt2].serviceURL;
            expect(domainName).to.equal(@"https://int2.location.enterprise.com/enterprise-sls/search/location/mobile");
        });
        it(@"prod", ^{
            NSString *domainName = [EHISearchEnvironment environmentWithType:EHISearchEnvironmentTypeProd].serviceURL;
            expect(domainName).to.equal(@"https://prd.location.enterprise.com/enterprise-sls/search/location/mobile");
        });
    });

    #if defined(DEBUG) || defined(UAT)
    context(@"archiving", ^{
        it(@"should persist when chosen", ^{
            EHISearchEnvironment *oldUnarchived = [EHISearchEnvironment unarchive];
            [oldUnarchived setType:EHISearchEnvironmentTypeInt2];

            EHISearchEnvironment *newUnarchived = [EHISearchEnvironment unarchive];
            expect(newUnarchived.environmentType).to.equal(EHISearchEnvironmentTypeInt2);
            
             [NSUserDefaults.standardUserDefaults setValue:nil forKey:EHISearchEnvironmentTypeKey];
        });
    });
    #endif
});

SpecEnd
