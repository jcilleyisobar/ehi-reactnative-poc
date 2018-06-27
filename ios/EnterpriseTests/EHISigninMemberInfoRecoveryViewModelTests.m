//
//  EHISigninMemberInfoRecoveryViewModelTests.m
//  Enterprise
//
//  Created by mplace on 5/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHISigninRecoveryViewModel.h"
#import "EHIConfiguration.h"

SpecBegin(EHISigninMemberInfoRecoveryViewModelTests)

__block EHISigninRecoveryViewModel *model = [EHISigninRecoveryViewModel new];

describe(@"the member info recovery view model", ^{
    
    context(@"when the model is given the member number recovery type", ^{
        
        beforeAll(^{
            [model updateWithModel:@(EHISigninRecoveryTypeUsername)];
        });
        
        it(@"should provide a title", ^{
            expect(model.title).to.localizeFrom(@"signin_member_number_recovery_modal_title");
        });
        
        it(@"should provide details text", ^{
            expect(model.details.string).to.localizeFrom(@"signin_member_number_recovery_modal_details_text");
        });
        
        it(@"should provide a title for the action button", ^{
            expect(model.actionButtonTitle).to.localizeFrom(@"signin_member_number_recovery_action_button_title");
        });
        
    });
    
    it(@"should provide a title for the cancel button", ^{
        expect(model.cancelButtonTitle).to.localizeFrom(@"signin_member_info_recovery_cancel_button_title");
    });
    
});

SpecEnd
