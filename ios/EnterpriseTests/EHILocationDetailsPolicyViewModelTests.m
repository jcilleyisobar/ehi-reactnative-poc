//
//  EHILocationDetailsPolicyViewModelTests.m
//  Enterprise
//
//  Created by Ty Cobb on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationDetailsPolicyViewModel.h"

SpecBegin(EHILocationDetailsPolicyViewModelTests)

describe(@"the details policy view model", ^{
    
    __block EHILocationDetailsPolicyViewModel *model;
    
    beforeAll(^{
        model = [EHILocationDetailsPolicyViewModel new];
    });
    
    context(@"when displaying a policy", ^{
        
        __block EHILocationPolicy *policy;
        
        beforeAll(^{
            policy = mock(EHILocationPolicy.class);
            [given(policy.codeDetails) willReturn:@"Reckless Driving"];
            [model updateWithModel:policy];
        });
        
        it(@"should display the policies name", ^{
            expect(model.title).to.equal(policy.codeDetails);
        });
        
    });
    
    context(@"when displaying a more policies link", ^{
        
        beforeAll(^{
            [model updateWithModel:[EHILocationPolicy placeholder]];
        });
        
        it(@"should display the 'more policies' title", ^{
            expect(model.title).to.localizeFrom(@"location_details_more_policies");
        });
        
    });
   
});

SpecEnd
