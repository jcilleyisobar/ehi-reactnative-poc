//
//  EHICreditCardFormatterTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/13/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHICreditCardFormatter.h"
#import "NSString+Formatting.h"

SpecBegin(EHICreditCardFormatterTests)

describe(@"EHICreditCardFormatter", ^{    
    it(@"given null, return empty", ^{
        NSString *cardNumber   = nil;
        NSString *maskedNumber = [EHICreditCardFormatter maskCardNumber:cardNumber];
        
        expect(maskedNumber).to.equal(@"");
    });
    
    it(@"given empty, return empty", ^{
        NSString *cardNumber   = @"";
        NSString *maskedNumber = [EHICreditCardFormatter maskCardNumber:cardNumber];
        
        expect(maskedNumber).to.equal(@"");
    });
    
    it(@"given spaces, return empty", ^{
        NSString *cardNumber   = [@" " ehi_repeat:8];
        NSString *maskedNumber = [EHICreditCardFormatter maskCardNumber:cardNumber];
        
        expect(maskedNumber).to.equal(@"");
    });
    
    it(@"given less than 4 digits, format as 12 * plus digits", ^{
        NSString *cardNumber   = @"40";
        NSString *maskedNumber = [EHICreditCardFormatter maskCardNumber:cardNumber];
        
        expect(maskedNumber).to.equal(@"************40");
    });
    
    it(@"given less than 4 digits with spaces, trim and format as 12 * plus digits", ^{
        NSString *cardNumber   = @"    40          ";
        NSString *maskedNumber = [EHICreditCardFormatter maskCardNumber:cardNumber];
        
        expect(maskedNumber).to.equal(@"************40");
    });
    
    it(@"given 4 digits, format as 12 * plus digits", ^{
        NSString *cardNumber   = @"4040";
        NSString *maskedNumber = [EHICreditCardFormatter maskCardNumber:cardNumber];
        
        expect(maskedNumber).to.equal(@"************4040");
    });
    
    it(@"given more than 4 digits, format as 12 * plus last 4 digits", ^{
        NSString *cardNumber   = @"414243";
        NSString *maskedNumber = [EHICreditCardFormatter maskCardNumber:cardNumber];
        
        expect(maskedNumber).to.equal(@"************4243");
    });
});

SpecEnd
