//
//  EHILocationDetailsAfterHoursModalViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationDetailsAfterHoursModalViewModel.h"

SpecBegin(EHILocationDetailsAfterHoursModalViewModelTests)

describe(@"EHILocationDetailsAfterHoursModalViewModel", ^{
    EHILocationDetailsAfterHoursModalViewModel *model = EHILocationDetailsAfterHoursModalViewModel.new;
    
    it(@"should have the right texts", ^{
        expect(model.title).to.localizeFrom(@"after_hours_return_modal_title");
        expect(model.details).to.localizeFrom(@"after_hours_return_modal_details");
        expect(model.hidesActionButton).to.beTruthy();
        expect(model.hidesCloseButton).to.beTruthy();
        expect(model.needsAutoDismiss).to.beTruthy();
    });
});

SpecEnd
