//
//  EHIDriverInfoViewModel_Private.h
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 1/17/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDriverInfoViewModel.h"

@interface EHIDriverInfoViewModel (Private)

- (EHIDriverInfo *)buildDriverInfoForRequestWithDriverInfo:(EHIDriverInfo *)driverInformation;

@end
