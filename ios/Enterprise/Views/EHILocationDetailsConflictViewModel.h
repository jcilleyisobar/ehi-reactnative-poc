//
//  EHILocationDetailsConflictViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/7/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHILocationDetailsConflictViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *openHours;
@end
