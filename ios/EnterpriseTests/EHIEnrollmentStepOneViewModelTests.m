//
//  EHIEnrollmentStepOneViewModelTests.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 1/26/18.
//Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIEnrollmentStepOneViewModel.h"
#import "EHIUserLicenseProfile.h"

SpecBegin(EHIEnrollmentStepOneViewModelTests)

describe(@"EHIEnrollmentStepOneViewModel", ^{
    __block EHIEnrollmentStepOneViewModel *enrollStepOneViewModel;
    __block EHICountry *selectedCountry;
    __block EHIUser *user;
    __block EHIUserLicenseProfile *userLicenseProfile;
    
    describe(@"Given the scenario of having an license user for a country with license_issuing_authority_required", ^{
        before(^{
            
            enrollStepOneViewModel = [EHIEnrollmentStepOneViewModel new];
            
            //mocking selected country
            selectedCountry = [EHICountry modelWithDictionary:@{
                @"code"                               : EHICountryCodeUK,
                @"license_issuing_authority_required" : @(YES)
            }];
            
            [enrollStepOneViewModel setSelectedCountry:selectedCountry];
        });
        
        context(@"when creating user profile to the request and having issuing_authority field filled", ^{
            before(^{
                //mocking user license profile
                userLicenseProfile = [EHIUserLicenseProfile modelWithDictionary:@{
                    @"country_subdivision_name" : @"Hawaii",
                    @"country_subdivision_code" : @"HI",
                    @"issuing_authority"        : @"DVPLA",
                }];
                
                user = [enrollStepOneViewModel createUserWithBasicProfile:nil andLicenseProfile:userLicenseProfile];
            });
            
            it(@"then we should send issuing_authority field filled correctly", ^{
                expect(user.license.issuingAuthority).to.equal(@"DVPLA");
                expect(user.license.subdivisionCode).beNil();
                expect(user.license.subdivisionName).beNil();
            });
        });
        
        context(@"when creating user profile to the request and not having issuing_authority field filled", ^{
            before(^{
                //mocking user license profile
                userLicenseProfile = [EHIUserLicenseProfile modelWithDictionary:@{
                    @"country_subdivision_name" : @"Hawaii",
                    @"country_subdivision_code" : @"HI"
                }];
                
                user = [enrollStepOneViewModel createUserWithBasicProfile:nil andLicenseProfile:userLicenseProfile];
            });
            
            it(@"then we should send issuing_authority field filled with country_subdivision_code", ^{
                expect(user.license.issuingAuthority).to.equal(@"HI");
                expect(user.license.subdivisionCode).beNil();
                expect(user.license.subdivisionName).beNil();
            });
        });
    });
    
    describe(@"Given the scenario of having an license user for a country only without issuing_authority field", ^{
        before(^{
            enrollStepOneViewModel = [EHIEnrollmentStepOneViewModel new];
            
            //mocking selected country with false license_issuing_authority_required
            selectedCountry = [EHICountry modelWithDictionary:@{
                @"code"                               : EHICountryCodeUK,
                @"license_issuing_authority_required" : @(NO),
                @"enable_country_sub_division"        : @(YES)
            }];
            
            [enrollStepOneViewModel setSelectedCountry:selectedCountry];
        });
        
        context(@"when creating user profile to the request", ^{
            before(^{
                //mocking user license profile
                userLicenseProfile = [EHIUserLicenseProfile modelWithDictionary:@{
                   @"country_subdivision_name" : @"Hawaii",
                   @"country_subdivision_code" : @"HI"
               }];
                
                user = [enrollStepOneViewModel createUserWithBasicProfile:nil andLicenseProfile:userLicenseProfile];
            });
            
            it(@"then subdivisions fields should be used normally", ^{
                expect(user.license.subdivisionCode).to.equal(@"HI");
                expect(user.license.subdivisionName).to.equal(@"Hawaii");
            });
        });
    });
});

SpecEnd
