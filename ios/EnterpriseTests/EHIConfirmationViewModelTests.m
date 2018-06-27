//
//  EHIConfirmationViewModelTests.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 9/26/17.
//Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIConfirmationViewModel.h"
#import "NSDate+Utility.h"

SpecBegin(EHIConfirmationViewModelTests)

describe(@"EHIConfirmationViewModel", ^{
    __block EHIConfirmationViewModel *viewModel;
    
    beforeAll(^{
        viewModel = [EHIConfirmationViewModel new];
    });
    describe(@"Given the ratings popup scenario at beggining of confirmation screen ", ^{
        
        context(@"when its the first time user reaches the screen and there is no last shown date", ^{
            it(@"then rate popup days condition should be truth", ^{
                expect([viewModel allowRateAppPopupWithLastDateShown:nil]).to.beTruthy();
            });
        });
        
        context(@"when it has been more than a week that the popup has been shown", ^{
            it(@"then rate popup days condition should be truthy", ^{
                NSDate *currentDate  = [NSDate ehi_today];
                NSDate *eightDaysAgo = [currentDate ehi_addDays:-8];
                
                expect([viewModel allowRateAppPopupWithLastDateShown:eightDaysAgo]).to.beTruthy();
            });
        });
        context(@"when it has been less than a week that the popup has been shown", ^{
            it(@"then rate popup days condition should be falsy", ^{
                NSDate *currentDate  = [NSDate ehi_today];
                NSDate *fourDaysAgo = [currentDate ehi_addDays:-4];
                
                expect([viewModel allowRateAppPopupWithLastDateShown:fourDaysAgo]).to.beFalsy();
            });
        });

    });
   
});

SpecEnd
