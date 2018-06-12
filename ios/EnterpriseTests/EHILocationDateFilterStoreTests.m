//
//  EHILocationDateFilterStoreTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/30/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHILocationDateFilterStore.h"

SpecBegin(EHILocationDateFilterStoreTests)

describe(@"EHILocationDateFilterStore", ^{
    it(@"should hold pikcup date", ^{
        EHILocationDateFilterStore *store = EHILocationDateFilterStore.new;
        store.pickupDate = NSDate.new;
        
        expect(store.pickupDate).notTo.beNil();
    });
    
    it(@"should hold return date", ^{
        EHILocationDateFilterStore *store = EHILocationDateFilterStore.new;
        store.returnDate = NSDate.new;
        
        expect(store.returnDate).notTo.beNil();
    });
    
    it(@"should hold pickup time", ^{
        EHILocationDateFilterStore *store = EHILocationDateFilterStore.new;
        store.pickupTime = NSDate.new;
        
        expect(store.pickupTime).notTo.beNil();
    });
    
    it(@"should hold pickup time, even if set to nil", ^{
        EHILocationDateFilterStore *store = EHILocationDateFilterStore.new;
        store.pickupTime = NSDate.new;
        store.pickupTime = nil;
        
        expect(store.pickupTime).notTo.beNil();
    });
    
    it(@"should hold return time", ^{
        EHILocationDateFilterStore *store = EHILocationDateFilterStore.new;
        store.returnTime = NSDate.new;
        
        expect(store.returnTime).notTo.beNil();
    });
    
    it(@"should hold pickup time, even if set to nil", ^{
        EHILocationDateFilterStore *store = EHILocationDateFilterStore.new;
        store.returnTime = NSDate.new;
        store.returnTime = nil;
        
        expect(store.returnTime).notTo.beNil();
    });
    
    it(@"should clear return time", ^{
        EHILocationDateFilterStore *store = EHILocationDateFilterStore.new;
        store.returnTime = NSDate.new;
        [store clearReturnTime];
        
        expect(store.returnTime).to.beNil();
    });
    
    it(@"should clear pickup time", ^{
        EHILocationDateFilterStore *store = EHILocationDateFilterStore.new;
        store.pickupTime = NSDate.new;
        [store clearPickupTime];
        
        expect(store.returnTime).to.beNil();
    });
});

SpecEnd
