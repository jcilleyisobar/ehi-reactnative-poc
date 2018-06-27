//
//  EHISingleTimeCalendarViewModelTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHISingleTimeCalendarViewModel.h"

SpecBegin(EHISingleTimeCalendarViewModelTests)

describe(@"EHISingleTimeCalendarViewModel", ^{
    __block EHISingleTimeCalendarViewModel *model = [EHISingleTimeCalendarViewModel new];
    
    it(@"on pickup time selection", ^{
        model = [[EHISingleTimeCalendarViewModel alloc] initWithModel:@(EHISingleTimeCalendarTypePickupTime)];
        
        expect(model.title).to.localizeFrom(@"time_select_pickup_title");
        expect(model.isPickingReturnTime).to.beFalsy();
    });
    
    it(@"on return time selection", ^{
        model = [[EHISingleTimeCalendarViewModel alloc] initWithModel:@(EHISingleTimeCalendarTypeReturnTime)];

        expect(model.title).to.localizeFrom(@"time_select_return_title");
        expect(model.isPickingReturnTime).to.beTruthy();
    });
    
    it(@"should select 12pm on load", ^{
        model = [EHISingleTimeCalendarViewModel new];

        NSIndexPath *indexPath = [NSIndexPath indexPathForItem:24 inSection:0];
        expect(model.initialIndexPath).to.equal(indexPath);
    });
    
    it(@"should have 24 hours split in 30 minutes", ^{
        expect(model.times.count).to.equal(@(48));
    });
    
    it(@"should have SELECT as the selection title", ^{
        expect(model.selectionButtonTitle).to.localizeFrom(@"time_picker_button_title");
    });
    
    it(@"should select a valid time", ^{
        NSIndexPath *selectedIndexPath = [NSIndexPath indexPathForItem:10 inSection:0];
        
        expect([model shouldSelectTimeAtIndexPath:selectedIndexPath]).to.beTruthy();
    });
    
    it(@"should not select a date out of bounds", ^{
        NSIndexPath *selectedIndexPath = [NSIndexPath indexPathForItem:50 inSection:0];
        
        expect([model shouldSelectTimeAtIndexPath:selectedIndexPath]).to.beFalsy();
    });
    
    context(@"on pickup time selection", ^{
        it(@"return a valid pickup time", ^{
            model = [[EHISingleTimeCalendarViewModel alloc] initWithModel:@(EHISingleTimeCalendarTypePickupTime)];

            model.handler = ^(NSDate *pickupTime, NSDate *returnTime) {
                expect(pickupTime).toNot.beNil();
                expect(returnTime).to.beNil();
            };
            
            NSIndexPath *selectedIndexPath = [NSIndexPath indexPathForItem:10 inSection:0];
            [model selectTimeAtIndexPath:selectedIndexPath];
        });
        
        it(@"return a valid pickup time and dont change initial return time", ^{
            model = [[EHISingleTimeCalendarViewModel alloc] initWithModel:@(EHISingleTimeCalendarTypePickupTime)];
            
            NSDate *initialReturnTime  = [model.times[10] date];
            NSDate *selectedPickupTime = [model.times[15] date];
            model.returnTime = initialReturnTime;
            model.handler = ^(NSDate *pickupTime, NSDate *returnTime) {
                expect(pickupTime).to.equal(selectedPickupTime);
                expect(returnTime).to.equal(initialReturnTime);
            };
            
            NSIndexPath *selectedIndexPath = [NSIndexPath indexPathForItem:15 inSection:0];
            [model selectTimeAtIndexPath:selectedIndexPath];
        });
    });
    
    context(@"on return time selection", ^{
        it(@"return a valid return time", ^{
            model = [[EHISingleTimeCalendarViewModel alloc] initWithModel:@(EHISingleTimeCalendarTypeReturnTime)];

            model.handler = ^(NSDate *pickupTime, NSDate *returnTime) {
                expect(pickupTime).to.beNil();
                expect(returnTime).toNot.beNil();
            };
            
            NSIndexPath *selectedIndexPath = [NSIndexPath indexPathForItem:10 inSection:0];
            [model selectTimeAtIndexPath:selectedIndexPath];
        });
        
        it(@"return a valid return time and dont change initial pickup time", ^{
            model = [[EHISingleTimeCalendarViewModel alloc] initWithModel:@(EHISingleTimeCalendarTypeReturnTime)];
            
            NSDate *initialPickupTime  = [model.times[10] date];
            NSDate *selectedReturnTime = [model.times[15] date];
            model.pickupTime = initialPickupTime;
            model.handler = ^(NSDate *pickupTime, NSDate *returnTime) {
                expect(pickupTime).to.equal(initialPickupTime);
                expect(returnTime).to.equal(selectedReturnTime);
            };
            
            NSIndexPath *selectedIndexPath = [NSIndexPath indexPathForItem:15 inSection:0];
            [model selectTimeAtIndexPath:selectedIndexPath];
        });
    });
    
    context(@"prefilled pickup time", ^{
        it(@"should highlight the correct index path", ^{
            model = [[EHISingleTimeCalendarViewModel alloc] initWithModel:@(EHISingleTimeCalendarTypePickupTime)];
            
            NSDate *initialTime = [model.times[10] date];
            model.pickupTime    = initialTime;
            NSIndexPath *targetIndexPath = [NSIndexPath indexPathForItem:10 inSection:0];
            expect(model.initialIndexPath).to.equal(targetIndexPath);
        });
    });
    
    context(@"prefilled return time", ^{
        it(@"should highlight the correct index path", ^{
            model = [[EHISingleTimeCalendarViewModel alloc] initWithModel:@(EHISingleTimeCalendarTypeReturnTime)];

            NSDate *initialTime = [model.times[10] date];
            model.returnTime    = initialTime;
            NSIndexPath *targetIndexPath = [NSIndexPath indexPathForItem:10 inSection:0];
            expect(model.initialIndexPath).to.equal(targetIndexPath);
        });
    });
});

SpecEnd
