//
//  EHIDealLabelViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIDealInterfaces.h"

@interface EHIDealLabelViewModel : EHIViewModel <MTRReactive, EHIDealInteractable, EHIDealDelegator>
@property (copy, nonatomic, readonly) NSString *dealName;
@property (copy, nonatomic, readonly) NSString *terms;
@end
