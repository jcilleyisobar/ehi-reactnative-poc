//
//  EHIPreferencesProfile.h
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserEmailPreferences.h"

@interface EHIUserPreferencesProfile : EHIModel
@property (copy  , nonatomic, readonly) NSString *sourceCode;
@property (strong, nonatomic) EHIUserEmailPreferences *email;
@end
