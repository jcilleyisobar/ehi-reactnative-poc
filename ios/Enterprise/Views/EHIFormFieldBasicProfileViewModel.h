//
//  EHIFormFieldBasicProfileViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldViewModel.h"
#import "EHIUserBasicProfile.h"

@interface EHIFormFieldBasicProfileViewModel : EHIFormFieldViewModel

@property (assign, nonatomic) BOOL hideAcessoryIcon;

@end

@interface EHIFormFieldBasicProfileViewModel (Generators)

+ (instancetype)nameFieldForProfile:(EHIUserBasicProfile *)profile;
+ (instancetype)memberIdFieldForProfile:(EHIUserBasicProfile *)profile;
+ (instancetype)accountFieldForCorporateAccount:(EHIContractDetails *)corporateAccount;
+ (instancetype)accountFieldForMissingAccount;

@end
