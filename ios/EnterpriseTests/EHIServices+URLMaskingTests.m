//
//  EHIServices+URLMaskingTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 24/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIServices+URLMasking.h"

SpecBegin(EHIServicesURLMaskingTests)

describe(@"EHIServices+URLMasking", ^{
    
    beforeAll(^{
        [EHIUserManager loginEnterprisePlusTestUser];
    });
    
    it(@"replace loyalty id",^{
        NSString *loyaltyId = [EHIUser currentUser].loyaltyNumber;
        NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/users/%@/EP/trips/upcoming", loyaltyId]];
        NSString *result = @"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/users/<loyaltyId>/EP/trips/upcoming";
        
        expect([EHIServices.new maskURL:url]).to.equal(result);
    });
    
    it(@"replace payment id",^{
        NSURL *url = [NSURL URLWithString:@"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/EP/profile/payment/asd123asdasd213asd12"];
        NSString *result = @"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/EP/profile/payment/<paymentId>";
        
        expect([EHIServices.new maskURL:url]).to.equal(result);
    });
    
    it(@"replace payment id and loyalty id",^{
        NSString *loyaltyId = [EHIUser currentUser].loyaltyNumber;
        NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/users/%@/EP/profile/payment/asd123asdasd213asd12", loyaltyId]];
        NSString *result = @"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/users/<loyaltyId>/EP/profile/payment/<paymentId>";
        
        expect([EHIServices.new maskURL:url]).to.equal(result);
    });
    
    //api/users/{loyalty_number}/EP/profile/payment/{payment_reference_id}
    
    it(@"replace reservation id", ^{
        NSURL *url = [NSURL URLWithString:@"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/reservations/1005437833"];
        NSString *result = @"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/reservations/<resId>";
       
        expect([EHIServices.new maskURL:url]).to.equal(result);
    });
    
    it(@"replace reservation id", ^{
        NSURL *url = [NSURL URLWithString:@"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/reservations/modify/1005437833"];
        NSString *result = @"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/reservations/modify/<resId>";
        
        expect([EHIServices.new maskURL:url]).to.equal(result);
    });
    
    it(@"replace reservation id", ^{
        NSURL *url = [NSURL URLWithString:@"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/reservations/modify/1005437833/selectedCarClass"];
        NSString *result = @"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/reservations/modify/<resId>/selectedCarClass";
        
        expect([EHIServices.new maskURL:url]).to.equal(result);
    });
    
    it(@"replace contract number", ^{
        NSURL *url = [NSURL URLWithString:@"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/contracts/123456"];
        NSString *result = @"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/contracts/<contractId>";
        
        expect([EHIServices.new maskURL:url]).to.equal(result);
    });
    
    it(@"replace contract number", ^{
        NSURL *url = [NSURL URLWithString:@"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/contracts/1a2b3c4d5e6"];
        NSString *result = @"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/contracts/<contractId>";
        
        expect([EHIServices.new maskURL:url]).to.equal(result);
    });
    
    it(@"skip locations", ^{
        NSURL *url = [NSURL URLWithString:@"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/locations/10010101"];
        NSString *result = @"https://www-enterprise-msi-rcqa.csdev.ehiaws.com/enterprise-msi/api/locations/<locationId>";
        
        expect([EHIServices.new maskURL:url]).to.equal(result);
    });
    
    it(@"skip solr", ^{
        NSURL *url = [NSURL URLWithString:@"https://xqa1.location.enterprise.com/enterprise-sls/search/location/mobile/text/Chicago?brand=ENTERPRISE&countryCode=US&fallback=en_GB&includeExotics=true&locale=en_US&oneway=false"];
        
        expect([EHIServices.new maskURL:url]).to.beNil();
    });
    
    it(@"skip solr", ^{
        NSURL *url = [NSURL URLWithString:@"https://prd.location.enterprise.com/enterprise-sls/search/location/mobile/text/Chicago?brand=ENTERPRISE&countryCode=US&fallback=en_GB&includeExotics=true&locale=en_US&oneway=false"];
        
        expect([EHIServices.new maskURL:url]).to.beNil();
    });
});

SpecEnd
