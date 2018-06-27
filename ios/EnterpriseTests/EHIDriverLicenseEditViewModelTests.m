//
//  EHIDriverLicenseEditTests.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 10/5/17.
//Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "EHIDriverLicenseEditViewModel.h"

SpecBegin(EHIDriverLicenseEditTests)

describe(@"EHIDriverLicenseEditViewModelTests", ^{
    __block EHIDriverLicenseEditViewModel *viewModel;
    
    beforeAll(^{
        viewModel = [EHIDriverLicenseEditViewModel new];
    });
    
    describe(@"Given the scenario where country is nil", ^{
        it(@"The app should not crash", ^{
            [viewModel createLicenseProfile];
        });
    });
    
    describe(@"Given the scenario of having optionals issue dates and expiration dates and the forms validation", ^{
        context(@"when user isn't in North America (US/CA) and both issue date and expiration date fields are optional", ^{
            EHICountry *country = [EHICountry modelWithDictionary:@{
               @"code" : EHICountryCodeFrance,
               @"license_expiry_date" : @"OPTIONAL",
               @"license_issue_date"  : @"OPTIONAL"
            }];
            
            it(@"then if user has expiration date filled, issue date should not be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:YES]).to.beFalsy();
            });
            it(@"then if user has expiration date not filled, issue date should be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:NO]).to.beTruthy();
            });
            
            it(@"then if user has issue date filled, expiration date should not be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:YES]).to.beFalsy();
            });
            it(@"then if user has issue date not filled, expiration date should be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:NO]).to.beTruthy();
            });
        });
        
        context(@"when user isn't in North America (US/CA) and just EXPIRY DATE is optional", ^{
            EHICountry *country = [EHICountry modelWithDictionary:@{
                @"code" : EHICountryCodeFrance,
                @"license_expiry_date" : @"OPTIONAL",
                @"license_issue_date"  : @"MANDATORY"
            }];
            
            it(@"then expiry date should not be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:YES]).to.beFalsy();
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:NO]).to.beFalsy();
            });
            
            it(@"then issue date should be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:YES]).to.beTruthy();
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:NO]).to.beTruthy();
            });
        });
        
        context(@"when user isn't in North America (US/CA) and just ISSUE DATE is optional", ^{
            EHICountry *country = [EHICountry modelWithDictionary:@{
                @"code" : EHICountryCodeFrance,
                @"license_expiry_date" : @"MANDATORY",
                @"license_issue_date"  : @"OPTIONAL"
            }];
            
            it(@"then expiry date should not be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:YES]).to.beTruthy();
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:NO]).to.beTruthy();
            });
            
            it(@"then issue date should be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:YES]).to.beFalsy();
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:NO]).to.beFalsy();
            });
        });
        
        context(@"when user is in NorthAmerica and just expiration date is optional", ^{
            EHICountry *country = [EHICountry modelWithDictionary:@{
                @"code" : EHICountryCodeUS,
                @"license_expiry_date" : @"OPTIONAL",
                @"license_issue_date"  : @"MANDATORY"
            }];
            
            it(@"then expiry date should not be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:YES]).to.beFalsy();
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:NO]).to.beFalsy();
            });
            
            it(@"then issue date should be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:YES]).to.beTruthy();
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:NO]).to.beTruthy();
            });
        });
        
        context(@"when user is in NorthAmerica and just issue date is optional", ^{
            EHICountry *country = [EHICountry modelWithDictionary:@{
               @"code" : EHICountryCodeUS,
               @"license_expiry_date" : @"MANDATORY",
               @"license_issue_date"  : @"OPTIONAL"
            }];
            
            it(@"then expiry date should not be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:YES]).to.beTruthy();
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:NO]).to.beTruthy();
            });
            
            it(@"then issue date should be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:YES]).to.beFalsy();
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:NO]).to.beFalsy();
            });
        });
        
        context(@"when user is in NorthAmerica and both expire and issue dates are optional", ^{
            EHICountry *country = [EHICountry modelWithDictionary:@{
                @"code" : EHICountryCodeUS,
                @"license_expiry_date" : @"OPTIONAL",
                @"license_issue_date"  : @"OPTIONAL"
            }];
            
            it(@"then expiry date should not be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:YES]).to.beFalsy();
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:NO]).to.beFalsy();
            });
            
            it(@"then issue date should be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:YES]).to.beFalsy();
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:NO]).to.beFalsy();
            });
        });

        context(@"when user is in NorthAmerica and both expire and issue dates are MANDATORY", ^{
            EHICountry *country = [EHICountry modelWithDictionary:@{
                @"code" : EHICountryCodeUS,
                @"license_expiry_date" : @"MANDATORY",
                @"license_issue_date"  : @"MANDATORY"
            }];
            
            it(@"then expiry date should not be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:YES]).to.beTruthy();
                expect([viewModel shouldValidateLicenseExpirationDateWithFilledIssueDate:NO]).to.beTruthy();
            });
            
            it(@"then issue date should be validated", ^{
                viewModel = [[EHIDriverLicenseEditViewModel alloc] initWithModel:country];
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:YES]).to.beTruthy();
                expect([viewModel shouldValidateLicenseIssueDateWithFilledExpirationDate:NO]).to.beTruthy();
            });
        });

    });
});

SpecEnd
