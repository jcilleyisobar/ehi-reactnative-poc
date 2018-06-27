//
//  EHISettingsSecurityViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 1/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISettingsControlViewModel.h"

typedef NS_ENUM(NSUInteger, EHISettingsSecurityRow) {
    EHISettingsSecurityRowTouchId,
};

@interface EHISettingsSecurityViewModel : EHISettingsControlViewModel

@property (assign, nonatomic) EHISettingsSecurityRow row;

@end
