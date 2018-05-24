//
//  EHILocationsFilterListViewModelTests.m
//  Enterprise
//
//  Created by Rafael Machado on 19/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationsFilterListViewModel.h"
#import "EHIFilters.h"
#import "EHISettings.h"

SpecBegin(EHILocationsFilterListViewModelTests)

describe(@"EHILocationsFilterListViewModel", ^{
    __block EHILocationsFilterListViewModel *model;
    
    context(@"date/time component", ^{
        it(@"should hide date/time when there's no data", ^{
            model = [EHILocationsFilterListViewModel new];
            
            expect(model.hideDateComponent).to.beTruthy();
        });
        
        it(@"should show date/time when there's pickup date", ^{
            model = [EHILocationsFilterListViewModel new];
            [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionPickupDate];
            
            expect(model.hideDateComponent).to.beFalsy();
        });
        
        it(@"should show date/time when there's pickup time", ^{
            model = [EHILocationsFilterListViewModel new];
            [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionPickupTime];
            
            expect(model.hideDateComponent).to.beFalsy();
        });
        
        it(@"should show date/time when there's return date", ^{
            model = [EHILocationsFilterListViewModel new];
            [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionReturnDate];
            
            expect(model.hideDateComponent).to.beFalsy();
        });
        
        it(@"should show date/time when there's return time", ^{
            model = [EHILocationsFilterListViewModel new];
            [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionReturnTime];
            
            expect(model.hideDateComponent).to.beFalsy();
        });
        
        context(@"clear action", ^{
            it(@"should clear pickup date", ^{
                model = [EHILocationsFilterListViewModel new];
                [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionPickupDate];
                
                [model clearDateQuery];
                
                expect(model.dateTimeModel.pickupDateModel.value).to.beNil();
            });
            
            it(@"should clear pickup time", ^{
                model = [EHILocationsFilterListViewModel new];
                [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionPickupTime];
                
                [model clearDateQuery];
                
                expect(model.dateTimeModel.pickupTimeModel.value).to.beNil();
            });
            
            it(@"should clear return date", ^{
                model = [EHILocationsFilterListViewModel new];
                [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionReturnDate];
                
                [model clearDateQuery];
                
                expect(model.dateTimeModel.returnDateModel.value).to.beNil();
            });
            
            it(@"should clear return time", ^{
                model = [EHILocationsFilterListViewModel new];
                [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionReturnTime];
                
                [model clearDateQuery];
                
                expect(model.dateTimeModel.returnTimeModel.value).to.beNil();
            });
        });
    });
    
    context(@"filters banner", ^{
        it(@"should hide when there's no active filter", ^{
            model = EHILocationsFilterListViewModel.new;

            expect(model.hideFiltersBanner).to.beTruthy();
        });
        
        it(@"should show when there are active filters", ^{
            model = EHILocationsFilterListViewModel.new;
            
            [model updateActiveFilters:[EHIFilters locationTypeFilters]];
            
            expect(model.hideFiltersBanner).to.beFalsy();
        });
        context(@"clear action", ^{
            it(@"should clear filters time", ^{
                model = EHILocationsFilterListViewModel.new;
                
                [model updateActiveFilters:[EHIFilters locationTypeFilters]];
                [model clearFiltersQuery];
                
                expect(model.hideFiltersBanner).to.beTruthy();
            });
        });
    });
    
    context(@"clear button", ^{
        it(@"should show on date, when has data", ^{
            model = EHILocationsFilterListViewModel.new;
            [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionReturnTime];
            
            expect(model.dateTimeModel.hideClear).to.beFalsy();
        });
        
        it(@"should show on banner, when has data", ^{
            model = EHILocationsFilterListViewModel.new;
            [model updateActiveFilters:[EHIFilters locationTypeFilters]];
            
            expect(model.bannerModel.hideClear).to.beFalsy();
        });
        
        it(@"when have both values should hide on date", ^{
            model = EHILocationsFilterListViewModel.new;
            [model updateActiveFilters:[EHIFilters locationTypeFilters]];
            [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionReturnTime];
            
            expect(model.dateTimeModel.hideClear).to.beTruthy();
            expect(model.bannerModel.hideClear).to.beFalsy();
        });
    });
    
    context(@"filter tip", ^{
        it(@"on fresh install, show it", ^{
            [EHISettings resetLocationsMapFilterTip];
            model = [EHILocationsFilterListViewModel new];
            
            expect(model.showFilterTip).to.beTruthy();
        });
        
        it(@"from LDT, no filters, show it", ^{
            [EHISettings resetLocationsMapFilterTip];
            model = [EHILocationsFilterListViewModel new];
            model.isFromLDT   = YES;
            model.isFiltering = NO;
            
            expect(model.showFilterTip).to.beTruthy();
        });
        
        it(@"from LDT, when close, dont show it again", ^{
            [EHISettings resetLocationsMapFilterTip];
            model = [EHILocationsFilterListViewModel new];
            model.isFromLDT   = YES;
            model.isFiltering = NO;
            
            [model closeTip];
            
            expect(model.showFilterTip).to.beFalsy();
        });
        
        it(@"from LDT, filtering, hide it", ^{
            [EHISettings resetLocationsMapFilterTip];
            model = [EHILocationsFilterListViewModel new];
            model.isFromLDT   = YES;
            model.isFiltering = YES;
            
            expect(model.showFilterTip).to.beFalsy();
        });
        
        it(@"if presented already, hide it", ^{
            [EHISettings didShowLocationsMapFilterTip];
            model = [EHILocationsFilterListViewModel new];
            
            expect(model.showFilterTip).to.beFalsy();
        });
        
        it(@"when user closes it, hide it", ^{
            [EHISettings resetLocationsMapFilterTip];
            model = [EHILocationsFilterListViewModel new];
            
            expect(model.showFilterTip).to.beTruthy();
            
            [model closeTip];
            
            expect(model.showFilterTip).to.beFalsy();
            expect([EHISettings shouldShowLocationsMapFilterTip]).to.beFalsy();
        });
    });
    
    context(@"filter tip background", ^{
        it(@"should not have when there's no filters active", ^{
            model = [EHILocationsFilterListViewModel new];
            
            expect(model.isShowingFilters).to.beFalsy();
        });
        
        it(@"should have when there's any filters active", ^{
            model = [EHILocationsFilterListViewModel new];
            [model setDate:NSDate.new inSection:EHIDateTimeComponentSectionReturnTime];
            
            expect(model.isShowingFilters).to.beTruthy();
        });
        
        it(@"should have when there's any filters active", ^{
            model = [EHILocationsFilterListViewModel new];
            [model updateActiveFilters:[EHIFilters locationTypeFilters]];
            
            expect(model.isShowingFilters).to.beTruthy();
        });
    });
});

SpecEnd
