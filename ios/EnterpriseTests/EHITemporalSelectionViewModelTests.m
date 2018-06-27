//
//  EHITemporalSelectionViewModelSpecTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 03/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "NSDate+Formatting.h"
#import "EHITemporalSelectionViewModel.h"

SpecBegin(EHITemporalSelectionViewModelTests)

describe(@"EHITemporalSelectionViewModel", ^{
    __block EHITemporalSelectionViewModel *model = [EHITemporalSelectionViewModel new];
    // GMT: Wed, 03 May 2017 13:00:00 GMT
    const NSTimeInterval epoc = 1493816400;

    it(@"on tap clear value should wipe out the value", ^{
        model = [EHITemporalSelectionViewModel new];
        model.value = [NSDate new];
        [model didTapClearValue];
        
        expect(model.value).to.beNil();
    });
    
    context(@"on map layout", ^{
        EHITemporalSelectionConfig mapConfig = (EHITemporalSelectionConfig){
            .type   = EHITemporalSelectionTypeDate,
            .layout = EHITemporalSelectionLayoutMap,
        };

        context(@"on date selection", ^{
            it(@"with empty date should show any date and hide clear button", ^{
                model = [[EHITemporalSelectionViewModel alloc] initWithConfig:mapConfig];
                
                expect(model.valueString.string).to.localizeFrom(@"locations_map_any_day_label");
                expect(model.hideClear).to.beTruthy();
            });

            it(@"with valid date should show formatted date and hide button", ^{
                model = [[EHITemporalSelectionViewModel alloc] initWithConfig:mapConfig];
                model.value = [NSDate dateWithTimeIntervalSince1970:epoc];
                expect(model.valueString.string).to.equal(@"May 3");
                expect(model.hideClear).to.beTruthy();
            });
        });
        
        context(@"on time selection", ^{
            EHITemporalSelectionConfig mapConfig = (EHITemporalSelectionConfig){
                .type   = EHITemporalSelectionTypeTime,
                .layout = EHITemporalSelectionLayoutMap,
            };
            
            it(@"with empty time and hide clear button should show any time", ^{
                model = [[EHITemporalSelectionViewModel alloc] initWithConfig:mapConfig];
                expect(model.valueString.string).to.localizeFrom(@"locations_map_any_time_label");
                expect(model.hideClear).to.beTruthy();
            });
            
            
            it(@"with valid time should show formatted time and clear button", ^{
                model = [[EHITemporalSelectionViewModel alloc] initWithConfig:mapConfig];
                NSDate *date = [NSDate dateWithTimeIntervalSince1970:epoc];
                model.value  = date;
                NSString *result = [date ehi_localizedTimeString];
                
                expect(model.valueString.string).to.equal(result);
                expect(model.hideClear).to.beTruthy();
            });
        });
    });
    
    context(@"on filter layout", ^{
        EHITemporalSelectionConfig filterConfig = (EHITemporalSelectionConfig){
            .type   = EHITemporalSelectionTypeDate,
            .layout = EHITemporalSelectionLayoutFilter,
        };

        context(@"on date selection", ^{
            it(@"with empty date should show any date and hide clear button", ^{
                model = [[EHITemporalSelectionViewModel alloc] initWithConfig:filterConfig];
                
                expect(model.valueString.string).to.localizeFrom(@"locations_map_any_day_label");
                expect(model.hideClear).to.beTruthy();
            });
            
            it(@"with valid date should show formatted date and clear button", ^{
                model = [[EHITemporalSelectionViewModel alloc] initWithConfig:filterConfig];
                model.value = [NSDate dateWithTimeIntervalSince1970:epoc];
                expect(model.valueString.string).to.equal(@"May 3");
                expect(model.hideClear).to.beFalsy();
            });
        });

        context(@"on time selection", ^{
            EHITemporalSelectionConfig filterConfig = (EHITemporalSelectionConfig){
                .type   = EHITemporalSelectionTypeTime,
                .layout = EHITemporalSelectionLayoutFilter,
            };
            
            it(@"with empty time and hide clear button should show any time", ^{
                model = [[EHITemporalSelectionViewModel alloc] initWithConfig:filterConfig];
                expect(model.valueString.string).to.localizeFrom(@"locations_map_any_time_label");
                expect(model.hideClear).to.beTruthy();
            });
            
            it(@"with valid time should show formatted time and clear button", ^{
                model = [[EHITemporalSelectionViewModel alloc] initWithConfig:filterConfig];
                NSDate *date = [NSDate dateWithTimeIntervalSince1970:epoc];
                model.value  = date;
                NSString *result = [date ehi_localizedTimeString];
                
                expect(model.valueString.string).to.equal(result);
                expect(model.hideClear).to.beFalsy();
            });
        });
    });
});

SpecEnd
