//
//  EHIAEMEnvironmentTests.m
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIAEMEnvironment.h"

SpecBegin(EHIAEMEnvironmentTests)

describe(@"EHIAEMEnvironment", ^{
    context(@"domain", ^{
        it(@"Xqa1", ^{
            NSString *domainURL = [EHIAEMEnvironment serviceWithEnvironment:EHIEnvironmentTypeSvcsQaXqa1].domainURL;
            expect(domainURL).to.equal(@"https://enterprise-xqa1-aem.enterprise.com");
        });
        it(@"Int1", ^{
            NSString *domainURL = [EHIAEMEnvironment serviceWithEnvironment:EHIEnvironmentTypeRcQaInt1].domainURL;
            expect(domainURL).to.equal(@"https://enterprise-int1-aem.enterprise.com");
        });
    });
});

SpecEnd
