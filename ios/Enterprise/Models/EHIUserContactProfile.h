//
//  EHIUserContactProfile.h
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPhone.h"
#import "EHIUserEmailPreferences.h"

@interface EHIUserContactProfile : EHIModel
@property (copy, nonatomic) NSString *email;
@property (copy, nonatomic) NSString *maskedEmail;
@property (copy, nonatomic) NSArray<EHIPhone> *phones;
@end
