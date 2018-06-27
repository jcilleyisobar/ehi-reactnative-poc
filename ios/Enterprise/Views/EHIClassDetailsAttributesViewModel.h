//
//  EHIClassDetailsAttributesViewModel.h
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIClassDetailsTitledInfoModel.h"

@interface EHIClassDetailsAttributesViewModel : EHIViewModel <MTRReactive>
@property (strong, nonatomic, readonly) EHIClassDetailsTitledInfoModel *passengerInfoModel;
@property (strong, nonatomic, readonly) EHIClassDetailsTitledInfoModel *luggageInfoModel;

/** Make/Model disclaimer found on the details screen */
@property (copy  , nonatomic, readonly) NSString *makeModelDisclaimer;

@end
